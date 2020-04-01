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

import org.kie.perf.scenario.IPerfTest;

/**
 * This scenario simulates a batch task assignment of 3.000 tasks. To achieve this, 1.000 processes,
 * that contain 3 user tasks each, are started. {@link TaskAssigningBatch} for more details.
 */
public class TaskAssigningBatch_3000 extends TaskAssigningBatch implements IPerfTest {

    public TaskAssigningBatch_3000() {
        super(1_000);
    }
}
