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

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.server.api.model.instance.TaskEventInstance;

public class TaskStatisticsUtilTest {

    @Test
    public void delaysBetweenCompleteAndStartEvents_worksAsExpected_forVariousEvents() {
        Instant now = Instant.now();
        TaskEventInstance task1Completed = new TaskEventInstance();
        task1Completed.setType("COMPLETED");
        task1Completed.setLogTime(Date.from(now));

        TaskEventInstance task2Started = new TaskEventInstance();
        task2Started.setType("STARTED");
        task2Started.setLogTime(Date.from(now.plus(5, ChronoUnit.SECONDS)));

        TaskEventInstance task2Completed = new TaskEventInstance();
        task2Completed.setType("COMPLETED");
        task2Completed.setLogTime(Date.from(now.plus(10, ChronoUnit.SECONDS)));

        TaskEventInstance task3OtherEvent = new TaskEventInstance();
        task3OtherEvent.setType("SOME_OTHER_EVENT_TYPE");
        task3OtherEvent.setLogTime(Date.from(now.plus(15, ChronoUnit.SECONDS)));

        TaskEventInstance task3Started = new TaskEventInstance();
        task3Started.setType("STARTED");
        task3Started.setLogTime(Date.from(now.plus(20, ChronoUnit.SECONDS)));

        TaskEventInstance task3Completed = new TaskEventInstance();
        task3Completed.setType("COMPLETED");
        task3Completed.setLogTime(Date.from(now.plus(25, ChronoUnit.SECONDS)));

        SortedSet<TaskEventInstance> events =
                new TreeSet<>(Comparator.comparing(taskEventInstance -> taskEventInstance.getLogTime().getTime()));
        events.add(task1Completed);
        events.add(task2Started);
        events.add(task2Completed);
        events.add(task3OtherEvent);
        events.add(task3Started);
        events.add(task3Completed);
        List<Long> delays = TaskStatisticsUtil.delaysBetweenCompleteAndStartEvents(events);

        Assertions.assertThat(delays).containsExactly(5_000L, 10_000L);
    }

    @Test
    public void median_single() {
        List<Long> numbers = Arrays.asList(1L);
        Assertions.assertThat(TaskStatisticsUtil.median(numbers)).isEqualTo(1L);
    }

    @Test
    public void median_odd() {
        List<Long> numbers = Arrays.asList(2L, 3L, 4L);
        Assertions.assertThat(TaskStatisticsUtil.median(numbers)).isEqualTo(3L);
    }

    @Test
    public void median_even() {
        List<Long> numbers = Arrays.asList(2L, 4L, 6L, 8L);
        Assertions.assertThat(TaskStatisticsUtil.median(numbers)).isEqualTo(5L);
    }
}
