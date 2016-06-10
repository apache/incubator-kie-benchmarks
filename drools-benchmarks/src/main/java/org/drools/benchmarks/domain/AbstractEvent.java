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

package org.drools.benchmarks.domain;

import java.util.concurrent.TimeUnit;

public abstract class AbstractEvent {

    private final int id;
    private final long timeValue;
    private final TimeUnit timeUnit;
    private final long duration;

    private String description;

    public AbstractEvent(final int id, final long timeValue, final TimeUnit timeUnit, final long duration) {
        this(id, timeValue, timeUnit, duration, "");
    }

    public AbstractEvent(final int id, final long timeValue, final TimeUnit timeUnit, final long duration,
            final String description) {
        this.id = id;
        this.timeValue = timeValue;
        this.timeUnit = timeUnit;
        this.duration = duration;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public long getTimeValue() {
        return timeValue;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public long getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractEvent)) {
            return false;
        }

        AbstractEvent that = (AbstractEvent) o;

        if (getId() != that.getId()) {
            return false;
        }
        if (getTimeValue() != that.getTimeValue()) {
            return false;
        }
        if (getDuration() != that.getDuration()) {
            return false;
        }
        if (getTimeUnit() != that.getTimeUnit()) {
            return false;
        }
        return getDescription() != null ? getDescription().equals(that.getDescription()) : that.getDescription() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (int) (getTimeValue() ^ (getTimeValue() >>> 32));
        result = 31 * result + (getTimeUnit() != null ? getTimeUnit().hashCode() : 0);
        result = 31 * result + (int) (getDuration() ^ (getDuration() >>> 32));
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}
