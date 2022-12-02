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

package org.drools.benchmarks.common.util;

public final class Constants {

    public static final String PROP_KEY_SMOKE_TESTS = "smoke.tests";
    public static final String PROP_KEY_DUMP_RETE = "dump.rete";
    public static final String PROP_KEY_DUMP_DRL = "dump.drl";
    public static final String PROP_KEY_DEBUG = "debug";

    public enum AccumulateFunction {
        AVERAGE {
            @Override
            public String toString() {
                return "average";
            }
        },
        MIN {
            @Override
            public String toString() {
                return "min";
            }
        },
        MAX {
            @Override
            public String toString() {
                return "max";
            }
        },
        COUNT {
            @Override
            public String toString() {
                return "count";
            }
        },
        SUM {
            @Override
            public String toString() {
                return "sum";
            }
        },
        COLLECT_LIST {
            @Override
            public String toString() {
                return "collectList";
            }
        },
        COLLECT_SET {
            @Override
            public String toString() {
                return "collectSet";
            }
        }
    }

    private Constants() {
        // Intentionally private - it should be not possible to instantiate util classes.
    }
}
