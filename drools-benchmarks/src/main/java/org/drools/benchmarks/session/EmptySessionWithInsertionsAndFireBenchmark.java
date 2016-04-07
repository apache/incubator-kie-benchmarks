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

import org.drools.benchmarks.util.TestUtil;
import org.kie.internal.utils.KieHelper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Date;

@Warmup(iterations = 100)
@Measurement(iterations = 100)
public class EmptySessionWithInsertionsAndFireBenchmark extends AbstractSessionBenchmark {

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        kieBase = new KieHelper().build(TestUtil.getKieBaseConfiguration());
    }

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
