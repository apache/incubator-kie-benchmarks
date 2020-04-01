
package org.jbpm.test.performance.taskassigning;

import org.kie.perf.scenario.IPerfTest;

/**
 * This scenario simulates a frequent updates to task assigning of 600 tasks. To achieve this, 200 processes,
 * that contain 3 user tasks each, are started. {@link TaskAssigningUpdates} for more details.
 */
public class TaskAssigningUpdates_600 extends TaskAssigningUpdates implements IPerfTest {

    public TaskAssigningUpdates_600() {
        super(200, 300);
    }
}
