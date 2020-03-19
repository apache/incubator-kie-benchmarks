package org.jbpm.test.performance.taskassigning;

import org.kie.perf.scenario.IPerfTest;

/**
 * This scenario simulates a batch task assignment of 300 tasks. To achieve this, 100 processes,
 * that contain 3 user tasks each, are started. {@link TaskAssigningBatch} for more details.
 * */
public class TaskAssigningBatch_300 extends TaskAssigningBatch implements IPerfTest {

    public TaskAssigningBatch_300() {
        super(100);
    }
}
