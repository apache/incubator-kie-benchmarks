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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.kie.server.api.model.instance.TaskEventInstance;

public class TaskStatisticsUtil {

    /**
     * Computes delays between pairs of two consecutive task events.
     * @param events a pre-sorted collection of events to search in.
     * @param firstEventMatcher {@link Predicate} that picks the first event in the pair
     * @param secondEventMatcher {@link Predicate} that picks the second event in the pair
     */
    public static List<Long> delaysBetweenEvents(Collection<TaskEventInstance> events,
            Predicate<TaskEventInstance> firstEventMatcher, Predicate<TaskEventInstance> secondEventMatcher) {
        List<Long> delays = new ArrayList<>();
        TaskEventInstance previous = null;
        for (TaskEventInstance taskEventInstance : events) {
            if (firstEventMatcher.test(taskEventInstance)) {
                previous = taskEventInstance;
            } else if (previous != null
                    && taskEventInstance.getTaskId().equals(previous.getTaskId())
                    && secondEventMatcher.test(taskEventInstance)) {
                delays.add(taskEventInstance.getLogTime().getTime() - previous.getLogTime().getTime());
                previous = null;
            }
        }
        return delays;
    }

    public static long median(List<Long> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("The list of numbers cannot be empty nor null.");
        }
        Collections.sort(numbers);
        int length = numbers.size();
        if (length % 2 == 1) {
            return numbers.get(length / 2);
        } else {
            long leftMiddle = numbers.get(length / 2 - 1);
            long rightMiddle = numbers.get(length / 2);
            return Math.round(((double) (leftMiddle + rightMiddle)) / 2);
        }
    }
}
