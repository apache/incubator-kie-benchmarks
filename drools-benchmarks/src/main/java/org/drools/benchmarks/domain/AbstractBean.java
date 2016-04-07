/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractBean {
    private static final AtomicLong idGenerator = new AtomicLong( 0 );

    private final long id;

    protected AbstractBean() {
        id = idGenerator.getAndIncrement();
    }

    @Override
    public int hashCode() {
        return (int)(id ^ (id >>> 32));
    }

    @Override
    public boolean equals( Object obj ) {
        return this.getClass() == obj.getClass() && id == ((AbstractBean)obj).id;
    }
}
