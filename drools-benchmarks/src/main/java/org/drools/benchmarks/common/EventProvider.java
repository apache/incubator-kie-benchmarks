/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.common;

import java.util.SortedSet;

public interface EventProvider {

    <T extends Event> SortedSet<T> getEvents(Class<T> eventClass, int eventsCount, long startTime, long timeIncrement)
            throws ProviderException;

    <T extends Event> SortedSet<T> getEvents(Class<T> eventClass, int eventsCount,
            long startTime, long timeIncrement, long duration) throws ProviderException;

}
