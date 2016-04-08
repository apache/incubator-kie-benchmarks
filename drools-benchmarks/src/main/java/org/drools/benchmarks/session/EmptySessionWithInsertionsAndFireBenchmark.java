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

package org.drools.benchmarks.session;

import java.util.Date;

import org.openjdk.jmh.annotations.Benchmark;

public class EmptySessionWithInsertionsAndFireBenchmark extends AbstractEmptySessionBenchmark {

    @Benchmark
    public void testCreateEmptySession() {
        createKieSession();
        kieSession.insert( "1" );
        kieSession.insert( new Integer(1) );
        kieSession.insert( new Long(1L) );
        kieSession.insert( new Short((short)1) );
        kieSession.insert( new Double(1.0) );
        kieSession.insert( new Float(1.0) );
        kieSession.insert( new Character('1') );
        kieSession.insert( Boolean.TRUE );
        kieSession.insert( String.class );
        kieSession.insert( new Date() );
        kieSession.fireAllRules();
    }
}
