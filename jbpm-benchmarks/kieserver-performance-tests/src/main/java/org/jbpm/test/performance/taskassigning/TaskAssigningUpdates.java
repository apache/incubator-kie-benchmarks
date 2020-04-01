/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.jbpm.test.performance.taskassigning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.kie.perf.SharedMetricRegistry;
import org.kie.perf.scenario.IPerfTest;
import org.kie.server.api.model.definition.TaskQueryFilterSpec;
import org.kie.server.api.model.instance.TaskEventInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This scenario tests impact of frequent changes to task assigning. After being created by starting processes, tasks
 * are retrieved, started and completed. Together with a delay between starting and completing a task, this scenario
 * simulates users working on their tasks, while each completion of a task represents a problem fact change for the
 * planning extension.
 * <p>
 * The scenario assumes that there are at least twice as many tasks as users. The publish windows size, system property
 * org.kie.server.taskAssigning.publishWindowSize, must be set to 1 on the KIE server.
 * Tasks are started and completed in as many threads as is the number of users configured on
 * the KIE server.
 * <p>
 * The measured metric is a median of delay between completing the first and starting the second one of two subsequent
 * tasks of the same user.
 */
abstract class TaskAssigningUpdates extends TaskAssigning implements IPerfTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAssigningUpdates.class);

    private static final String ASSIGNED_TASKS_QUERY_NAME = "assignedTasksQuery";
    private static final String ASSIGNED_TASKS_QUERY =
            "select ti.taskId,ti.actualOwner from AuditTaskImpl ti where ti.actualOwner != '' and ti.status = 'Reserved'";
    private static final String TASK_STARTED = "STARTED";
    private static final String TASK_COMPLETED = "COMPLETED";
    private static final int START_PROCESS_THREADS = 10;
    private static final long TASK_COMPLETION_DELAY_MILLIS = 5000L;

    private final int processCount;
    private final int taskCount;
    private final int userCount;

    private Set<Long> taskIdSet;
    private ExecutorService threadPoolExecutor;
    private Timer taskAssignmentDuration;

    public TaskAssigningUpdates(int processCount, int userCount) {
        this.processCount = processCount;
        this.taskCount = processCount * 3;
        this.userCount = userCount;
        if (taskCount < 2 * userCount) {
            throw new IllegalArgumentException("For a relevance of the measured metric the number of tasks must be " +
                    "at least 2 * number of users. Please note that the same number of users must be configured on " +
                    "the KIE server before running this scenario");
        }
    }

    private void beforeScenario() {
        abortAllProcesses();
        taskIdSet = new ConcurrentHashMap<>().newKeySet();
        threadPoolExecutor = Executors.newFixedThreadPool(userCount);
    }

    @Override
    public void init() {
        registerQuery(ASSIGNED_TASKS_QUERY_NAME, ASSIGNED_TASKS_QUERY);
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        taskAssignmentDuration = metrics.timer(MetricRegistry.name(getClass(), "taskassigning.update.task_assigned.duration"));
    }

    @Override
    public void execute() {
        beforeScenario();

        // Create tasks by starting new processes.
        startProcessesAsync(processCount, START_PROCESS_THREADS);

        // Keep completing tasks to introduce changes and trigger incremental re-planning.
        while (taskIdSet.size() < taskCount) {
            TaskQueryFilterSpec filterSpec = new TaskQueryFilterSpec();
            List<TaskInstance> assignedTasks = getQueryClient()
                    .findHumanTasksWithFilters(ASSIGNED_TASKS_QUERY_NAME, filterSpec, 0, userCount);
            if (assignedTasks.isEmpty()) { // If there are no tasks to work on, wait and retry.
                sleep(500L);
                continue;
            }

            LOGGER.debug(String.format("Completing %d tasks. Already finished %d tasks of %d.",
                    assignedTasks.size(), taskIdSet.size(), taskCount));
            CompletableFuture[] completableFutures = new CompletableFuture[userCount];
            int i = 0;
            for (TaskInstance task : assignedTasks) { // Run each task in a separate thread.
                completableFutures[i++] = CompletableFuture.runAsync(() -> {
                    getTaskClient().startTask(CONTAINER_ID, task.getId(), task.getActualOwner());
                    sleep(TASK_COMPLETION_DELAY_MILLIS);
                    getTaskClient().completeTask(CONTAINER_ID, task.getId(), task.getActualOwner(), new HashMap<>());
                    LOGGER.debug(String.format("Completed task ID %d", task.getId()));
                    taskIdSet.add(task.getId());
                }, threadPoolExecutor);
            }
        }

        Map<String, NavigableSet<TaskEventInstance>> taskEventsPerUser = getTaskEventsPerUser();
        List<Long> allDelaysBetweenCompleteAndStartEvents = taskEventsPerUser.entrySet().stream()
                .map((entry) -> delaysBetweenCompleteAndStartEvents(entry.getValue()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        long median = median(allDelaysBetweenCompleteAndStartEvents);
        taskAssignmentDuration.update(median, TimeUnit.MILLISECONDS);

        afterScenario();
    }

    private void afterScenario() {
        shutdownExecutorService(threadPoolExecutor);
        abortAllProcesses();
    }

    /**
     * Computes delays between completing and starting consecutive tasks.
     */
    private List<Long> delaysBetweenCompleteAndStartEvents(SortedSet<TaskEventInstance> events) {
        List<Long> delays = new ArrayList<>();
        TaskEventInstance previous = null;
        for (TaskEventInstance taskEventInstance : events) {
            if (TASK_COMPLETED.equals(taskEventInstance.getType())) {
                previous = taskEventInstance;
            } else if (previous != null) { // it must be STARTED event
                delays.add(taskEventInstance.getLogTime().getTime() - previous.getLogTime().getTime());
                previous = null;
            }
        }
        return delays;
    }

    /**
     * Filters STARTED and COMPLETED {@link TaskEventInstance} and groups them by users working on corresponding tasks.
     * The events are ordered chronologically.
     */
    private Map<String, NavigableSet<TaskEventInstance>> getTaskEventsPerUser() {
        Map<String, NavigableSet<TaskEventInstance>> taskEventsPerUser = new HashMap<>();
        Comparator<TaskEventInstance> taskEventInstanceComparator =
                Comparator.comparing(taskEventInstance -> taskEventInstance.getLogTime().getTime());
        for (Long taskId : taskIdSet) {
            List<TaskEventInstance> taskEvents = getTaskClient().findTaskEvents(taskId, 0, Integer.MAX_VALUE);
            for (TaskEventInstance taskEvent : taskEvents) {
                String userId = taskEvent.getUserId();
                NavigableSet<TaskEventInstance> taskEventSet =
                        taskEventsPerUser.computeIfAbsent(userId, (__) -> new TreeSet<>(taskEventInstanceComparator));
                if (TASK_STARTED.equals(taskEvent.getType()) || TASK_COMPLETED.equals(taskEvent.getType())) {
                    taskEventSet.add(taskEvent);
                }
            }
        }
        return taskEventsPerUser;
    }

    private long median(List<Long> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("The list of numbers cannot be empty nor null.");
        }
        Collections.sort(numbers);
        int length = numbers.size();
        if (length % 2 == 1) {
            return numbers.get(length / 2);
        } else {
            long leftMiddle = numbers.get(length / 2 - 1);
            long rightMiddle = numbers.get(length / 2);
            return Math.round(((double) (leftMiddle + rightMiddle)) / 2);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Interrupted during Thread.sleep()");
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}
