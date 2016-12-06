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

import org.drools.benchmarks.domain.B;
import org.drools.benchmarks.domain.C;
import org.drools.benchmarks.domain.D;
import org.drools.benchmarks.domain.E;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.RuleEngineOption;

public final class TestUtil {

    public static boolean isSmokeTestsRun() {
        return Boolean.TRUE.toString().equals(System.getProperty(Constants.PROP_KEY_SMOKE_TESTS));
    }

    public static boolean isDebug() {
        return Boolean.TRUE.toString().equals(System.getProperty(Constants.PROP_KEY_DEBUG));
    }

    public static boolean useReteoo() {
        return RuleEngineOption.RETEOO.toString().equals(System.getProperty(Constants.PROP_KEY_ENGINE_TYPE));
    }

    public static boolean dumpDrl() {
        return isDebug() || Boolean.TRUE.toString().equals(System.getProperty(Constants.PROP_KEY_DUMP_DRL));
    }

    public static boolean dumpRete() {
        return isDebug() || Boolean.TRUE.toString().equals(System.getProperty(Constants.PROP_KEY_DUMP_RETE));
    }

    public static void insertJoinedFactsToSession(final KieSession kieSession, final int numberOfJoins, final int baseFactValue) {
        switch (numberOfJoins) {

            case 1:
                kieSession.insert( new B( baseFactValue + 3 ) );
                break;
            case 2:
                kieSession.insert( new B( baseFactValue + 3 ) );
                kieSession.insert( new C( baseFactValue + 4 ) );
                break;
            case 3:
                kieSession.insert( new B( baseFactValue + 3 ) );
                kieSession.insert( new C( baseFactValue + 4 ) );
                kieSession.insert( new D( baseFactValue + 5 ) );
                break;
            case 4:
                kieSession.insert( new B( baseFactValue + 3 ) );
                kieSession.insert( new C( baseFactValue + 4 ) );
                kieSession.insert( new D( baseFactValue + 5 ) );
                kieSession.insert( new E( baseFactValue + 6 ) );
                break;
            default:
                throw new IllegalArgumentException("Unsupported number of joins (" + numberOfJoins + ")!");
        }
    }

    private TestUtil() {
    }
}
