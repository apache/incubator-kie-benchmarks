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

package org.drools.benchmarks.model.event;

public abstract class Event {
    protected int id;
    /** Duration of event in ms */
    protected long duration;
    /** Description of event */
    protected String description;

    public Event() {
        duration = 0;
    }

    public Event(int id) {
        this();
        this.id = id;
    }

    public Event(int id, long duration) {
        this(id);
        this.duration = duration;
    }

    public Event(int id, String description) {
        this(id);
        this.description = description;
    }

    public Event(int id, long duration, String description) {
        this.id = id;
        this.duration = duration;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}