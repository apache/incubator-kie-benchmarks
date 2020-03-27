package org.jbpm.test.performance.taskassigning;

import org.kie.perf.scenario.IPerfTest;

/**
 * This scenario simulates a frequent updates to task assigning of 3.000 tasks. To achieve this, 1.000 processes,
 * that contain 3 user tasks each, are started. {@link TaskAssigningUpdates} for more details.
 */
public class TaskAssigningUpdates_3000 extends TaskAssigningUpdates implements IPerfTest {

    public TaskAssigningUpdates_3000() {
        super(1_000);
    }
}
