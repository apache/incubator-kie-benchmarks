package org.jbpm.test.performance.taskassigning;

import org.kie.perf.scenario.IPerfTest;

/**
 * This scenario simulates a batch task assignment of 30.000 tasks. To achieve this, 10.000 processes,
 * that contain 3 user tasks each, are started. {@link TaskAssigningBatch} for more details.
 */
public class TaskAssigningBatch_30000 extends TaskAssigningBatch implements IPerfTest {

    public TaskAssigningBatch_30000() {
        super(10_000);
    }
}
