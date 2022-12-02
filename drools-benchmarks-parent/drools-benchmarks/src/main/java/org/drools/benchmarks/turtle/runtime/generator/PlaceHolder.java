/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.turtle.runtime.generator;

import java.util.concurrent.TimeUnit;

/**
 * Represents place holder's name, sendEvents value, end value and optional time unit type.
 */
public class PlaceHolder {
    private final String name;
    private final TimeUnit timeUnit;

    private final int startValue;
    private final int endValue;

    public PlaceHolder(final String name, final int startValue, final int endValue) {
        this(name, startValue, endValue, null);
    }

    public PlaceHolder(final String name, final int startValue, final int endValue, final TimeUnit timeUnit) {
        this.name = name;
        this.startValue = startValue;
        this.endValue = endValue;
        this.timeUnit = timeUnit;
    }

    @Override
    public String toString() {
        return "PlaceHolder[name=" + name + ", timeUnit=" + timeUnit + ", startValue=" + startValue + ", endValue=" + endValue + "]";
    }

    public String getName() {
        return name;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getStartValue() {
        return startValue;
    }

    public int getEndValue() {
        return endValue;
    }
}
