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

package org.jbpm.test.performance.taskassigning.updates;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.jbpm.test.performance.taskassigning.TaskAssigning;
import org.jbpm.test.performance.taskassigning.TaskStatisticsUtil;
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
    private static final long TASK_COMPLETION_DELAY_MILLIS = 5000L;

    private final int processCount;
    private final int taskCount;
    private final int userCount;

    private Set<Long> completedTasks;
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
        completedTasks = new ConcurrentHashMap<>().newKeySet();
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
        LOGGER.debug(String.format("Scenario %s started.", getClass().getSimpleName()));
        try {
            scenario();
            LOGGER.debug(String.format("Scenario %s finished.", getClass().getSimpleName()));
        } finally {
            afterScenario();
        }
    }

    private void scenario() {
        // Create tasks by starting new processes.
        startProcesses(processCount);
        LOGGER.debug("All process instances have been started.");
        Set<Long> tasksInProgress = new ConcurrentHashMap<>().newKeySet();
        // Keep completing tasks to introduce changes and trigger incremental re-planning.
        while (completedTasks.size() < taskCount) {
            TaskQueryFilterSpec filterSpec = new TaskQueryFilterSpec();
            List<TaskInstance> assignedTasks = getQueryClient()
                    .findHumanTasksWithFilters(ASSIGNED_TASKS_QUERY_NAME, filterSpec, 0, userCount);
            if (assignedTasks.isEmpty()) { // If there are no tasks to work on, wait and retry.
                sleep(500L);
                continue;
            }
            LOGGER.trace(String.format("Completing %d tasks. Already finished %d tasks of %d.",
                    assignedTasks.size(), completedTasks.size(), taskCount));
            // Run each task in a separate thread.
            for (TaskInstance task : assignedTasks) {
                // Avoid submitting task some other thread already takes care of
                if (!tasksInProgress.contains(task.getId())) {
                    tasksInProgress.add(task.getId());
                    CompletableFuture.runAsync(() -> {
                        getTaskClient().startTask(CONTAINER_ID, task.getId(), task.getActualOwner());
                        sleep(TASK_COMPLETION_DELAY_MILLIS);
                        getTaskClient().completeTask(CONTAINER_ID, task.getId(), task.getActualOwner(), new HashMap<>());
                        completedTasks.add(task.getId());
                        tasksInProgress.remove(task.getId());
                    }, threadPoolExecutor);
                }
            }
        }
        LOGGER.debug("All tasks have been completed.");

        Map<String, NavigableSet<TaskEventInstance>> taskEventsPerUser = getTaskEventsPerUser();
        LOGGER.debug(String.format("Computing delays between tasks for %d users", taskEventsPerUser.size()));
        List<Long> allDelaysBetweenCompleteAndStartEvents = taskEventsPerUser.entrySet().stream()
                .map((entry) -> TaskStatisticsUtil.delaysBetweenCompleteAndStartEvents(entry.getValue()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        long median = TaskStatisticsUtil.median(allDelaysBetweenCompleteAndStartEvents);
        LOGGER.debug(String.format(
                "Median of delay between completing one task and starting another per a user is %d milliseconds", median));
        taskAssignmentDuration.update(median, TimeUnit.MILLISECONDS);
    }

    private void afterScenario() {
        shutdownExecutorService(threadPoolExecutor);
        abortAllProcesses();
    }

    /**
     * Filters STARTED and COMPLETED {@link TaskEventInstance} and groups them by users working on corresponding tasks.
     * The events are ordered chronologically.
     */
    private Map<String, NavigableSet<TaskEventInstance>> getTaskEventsPerUser() {
        Map<String, NavigableSet<TaskEventInstance>> taskEventsPerUser = new HashMap<>();
        Comparator<TaskEventInstance> taskEventInstanceComparator =
                Comparator.comparing(taskEventInstance -> taskEventInstance.getLogTime().getTime());
        for (Long taskId : completedTasks) {
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
