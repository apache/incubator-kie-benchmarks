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

package org.jbpm.test.performance.taskassigning.batch;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.jbpm.test.performance.taskassigning.TaskAssigning;
import org.kie.perf.SharedMetricRegistry;
import org.kie.perf.scenario.IPerfTest;
import org.kie.server.api.model.definition.TaskQueryFilterSpec;
import org.kie.server.api.model.instance.TaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This scenario simulates a batch task assignment. The measured metric is a delay between a moment when all tasks have
 * been created and all of them have been assigned to users.
 */
abstract class TaskAssigningBatch extends TaskAssigning implements IPerfTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAssigningBatch.class);

    private static final String PROCESS_ID = "test-jbpm-assignment.testTaskAssignment";
    private static final String UNASSIGNED_TASKS_QUERY_NAME = "unassignedTasksQuery";
    private static final String ASSIGNED_TASKS_QUERY_NAME = "assignedTasksQuery";
    private static final String UNASSIGNED_TASKS_QUERY =
            "select ti.taskId from AuditTaskImpl ti where ti.actualOwner = '' and ti.status != 'Exited'";
    private static final String ASSIGNED_TASKS_QUERY =
            "select ti.taskId from AuditTaskImpl ti where ti.actualOwner != '' and ti.status = 'Reserved'";

    private final int processCount;

    private ScheduledExecutorService scheduledExecutorService;
    private CountDownLatch allAssignedLatch;
    private Meter completedScenario;
    private Timer startProcessesDuration;
    private Timer taskAssignmentDuration;

    public TaskAssigningBatch(int processCount) {
        this.processCount = processCount;
    }

    @Override
    public void init() {
        registerQuery(UNASSIGNED_TASKS_QUERY_NAME, UNASSIGNED_TASKS_QUERY);
        registerQuery(ASSIGNED_TASKS_QUERY_NAME, ASSIGNED_TASKS_QUERY);
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        completedScenario = metrics.meter(MetricRegistry.name(getClass(), "taskassigning.batch.completed"));
        startProcessesDuration = metrics.timer(MetricRegistry.name(getClass(), "taskassigning.batch.processes_started.duration"));
        taskAssignmentDuration = metrics.timer(MetricRegistry.name(getClass(), "taskassigning.batch.tasks_assigned.duration"));
    }

    private void beforeScenario() {
        abortAllProcesses();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        allAssignedLatch = new CountDownLatch(1);
    }

    @Override
    public void execute() {
        beforeScenario();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            TaskQueryFilterSpec filterSpec = new TaskQueryFilterSpec();
            List<TaskInstance> unassignedTasks = getQueryClient().findHumanTasksWithFilters(UNASSIGNED_TASKS_QUERY_NAME,
                    filterSpec, 0, 1);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Unassigned tasks present: " + unassignedTasks.size());
            }
            if (unassignedTasks.isEmpty()) {
                List<TaskInstance> assignedTasks = getQueryClient().findHumanTasksWithFilters(ASSIGNED_TASKS_QUERY_NAME,
                        filterSpec, 0, Integer.MAX_VALUE);
                if (assignedTasks.size() == 3 * processCount) {
                    LOGGER.debug("No more unassigned tasks found.");
                    allAssignedLatch.countDown();
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);

        LOGGER.debug("Creating processes ...");
        Timer.Context startProcessDurationContext = startProcessesDuration.time();
        Timer.Context taskAssignmentDurationContext = taskAssignmentDuration.time();

        // Start processes in multiple threads and wait for all of them to be started.
        startProcesses(PROCESS_ID, processCount);
        startProcessDurationContext.stop();
        LOGGER.debug("All processes have been started");

        try {
            allAssignedLatch.await();
            taskAssignmentDurationContext.stop();
            LOGGER.debug("All tasks have been assigned");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted waiting during test.", e);
        }
        completedScenario.mark();

        afterScenario();
    }

    private void afterScenario() {
        shutdownExecutorService(scheduledExecutorService);
        abortAllProcesses();
    }

    @Override
    public void close() {
        // nothing to do
    }
}
