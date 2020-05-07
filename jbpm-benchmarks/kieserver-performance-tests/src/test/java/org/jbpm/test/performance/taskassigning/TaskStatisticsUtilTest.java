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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.server.api.model.instance.TaskEventInstance;

public class TaskStatisticsUtilTest {

    @Test
    public void delaysBetweenAddedAndDelegatedEvents_worksAsExpected_forVariousEvents() {
        Instant now = Instant.now();
        TaskEventInstance task1Added = new TaskEventInstance();
        task1Added.setTaskId(1L);
        task1Added.setType("ADDED");
        task1Added.setLogTime(Date.from(now));

        TaskEventInstance task1Delegated = new TaskEventInstance();
        task1Delegated.setTaskId(1L);
        task1Delegated.setType("DELEGATED");
        task1Delegated.setLogTime(Date.from(now.plus(10, ChronoUnit.SECONDS)));

        TaskEventInstance task2Added = new TaskEventInstance();
        task2Added.setTaskId(2L);
        task2Added.setType("ADDED");
        task2Added.setLogTime(Date.from(now.plus(20, ChronoUnit.SECONDS)));

        TaskEventInstance task2Delegated = new TaskEventInstance();
        task2Delegated.setTaskId(2L);
        task2Delegated.setType("DELEGATED");
        task2Delegated.setLogTime(Date.from(now.plus(30, ChronoUnit.SECONDS)));

        TaskEventInstance task3OtherEvent = new TaskEventInstance();
        task3OtherEvent.setTaskId(3L);
        task3OtherEvent.setType("SOME_OTHER_EVENT_TYPE");
        task3OtherEvent.setLogTime(Date.from(now.plus(40, ChronoUnit.SECONDS)));

        TaskEventInstance task3Added = new TaskEventInstance();
        task3Added.setTaskId(3L);
        task3Added.setType("ADDED");
        task3Added.setLogTime(Date.from(now.plus(50, ChronoUnit.SECONDS)));

        TaskEventInstance task3Completed = new TaskEventInstance();
        task3Completed.setTaskId(3L);
        task3Completed.setType("COMPLETED");
        task3Completed.setLogTime(Date.from(now.plus(60, ChronoUnit.SECONDS)));

        Collection<TaskEventInstance> events = new ArrayList<>();
        events.add(task1Added);
        events.add(task1Delegated);
        events.add(task2Added);
        events.add(task2Delegated);
        events.add(task3OtherEvent);
        events.add(task3Added);
        events.add(task3Completed);
        List<Long> delays = TaskStatisticsUtil.delaysBetweenEvents(events,
                (taskEvent) -> "ADDED".equals(taskEvent.getType()),
                (taskEvent) -> "DELEGATED".equals(taskEvent.getType()));

        Assertions.assertThat(delays).containsExactly(10_000L, 10_000L);
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
