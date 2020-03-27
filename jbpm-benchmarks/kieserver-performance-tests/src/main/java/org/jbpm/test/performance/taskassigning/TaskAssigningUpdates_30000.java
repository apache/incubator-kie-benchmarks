package org.jbpm.test.performance.taskassigning;

import org.kie.perf.scenario.IPerfTest;

/**
 * This scenario simulates a frequent updates to task assigning of 30.000 tasks. To achieve this, 10.000 processes,
 * that contain 3 user tasks each, are started. {@link TaskAssigningUpdates} for more details.
 */
public class TaskAssigningUpdates_30000 extends TaskAssigningUpdates implements IPerfTest {

    public TaskAssigningUpdates_30000() {
        super(10_000);
    }
}
