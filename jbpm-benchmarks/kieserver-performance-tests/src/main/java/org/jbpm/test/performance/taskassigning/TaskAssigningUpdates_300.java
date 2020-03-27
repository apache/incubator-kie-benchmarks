package org.jbpm.test.performance.taskassigning;

import org.kie.perf.scenario.IPerfTest;

/**
 * This scenario simulates a frequent updates to task assigning of 300 tasks. To achieve this, 100 processes,
 * that contain 3 user tasks each, are started. {@link TaskAssigningUpdates} for more details.
 */
public class TaskAssigningUpdates_300 extends TaskAssigningUpdates implements IPerfTest {

    public TaskAssigningUpdates_300() {
        super(100);
    }
}
