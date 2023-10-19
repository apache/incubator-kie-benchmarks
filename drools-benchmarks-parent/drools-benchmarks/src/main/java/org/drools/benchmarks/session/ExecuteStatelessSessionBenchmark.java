/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.session;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.model.A;
import org.drools.benchmarks.common.model.B;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.commands.runtime.BatchExecutionCommandImpl;
import org.drools.commands.runtime.rule.FireAllRulesCommand;
import org.drools.commands.runtime.rule.InsertObjectCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.StatelessKieSession;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 10000)
@Measurement(iterations = 50000)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ExecuteStatelessSessionBenchmark extends AbstractBenchmark {

    @Param({"1"})
    private int rulesNr;

    @Param({"1"})
    private int factsNr;

    private StatelessKieSession statelessKieSession;

    private Command command;

    @Setup
    public void setupKieBase() {
        final DRLProvider drlProvider = new RulesWithJoinsProvider(1, false, true);
        this.kieBase = BuildtimeUtil.createKieBaseFromDrl(drlProvider.getDrl(rulesNr));
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        this.statelessKieSession = kieBase.newStatelessKieSession();
        this.command = createCommand();
    }

    @Benchmark
    public void test(final Blackhole eater) {
        eater.consume( statelessKieSession.execute(command) );
    }

    private BatchExecutionCommandImpl createCommand() {
        final List<Command> commands = new ArrayList<>();
        commands.add(new InsertObjectCommand( new A( rulesNr + 1 ) ));
        for ( int i = 0; i < factsNr; i++ ) {
            commands.add(new InsertObjectCommand( new B( rulesNr + i + 3 ) ));
        }
        commands.add(new FireAllRulesCommand());
        return new BatchExecutionCommandImpl(commands);
    }
}
