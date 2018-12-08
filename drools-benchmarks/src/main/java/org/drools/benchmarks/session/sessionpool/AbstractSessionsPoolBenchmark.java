/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.session.sessionpool;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.providers.SimpleRulesWithConstraintsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.model.A;
import org.drools.benchmarks.model.B;
import org.drools.benchmarks.model.C;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.event.ProcessEventSupport;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.kie.api.KieServices;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieContainerSessionsPool;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.process.CorrelationKey;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(5)
public abstract class AbstractSessionsPoolBenchmark {

    @Param({"10"})
    private int numberOfRules;

    @Param({"1"})
    private int initialSessionPoolSize;

    @Param({"10"})
    private int numberOfFacts;

    protected KieContainer kieContainer;
    protected KieContainerSessionsPool sessionsPool;

    @Setup
    public void setupContainerAndPool() throws IOException {
        ProcessRuntimeFactory.setProcessRuntimeFactoryService((wm) -> new DummyListeningProcessRuntime(wm));
        final Resource drlResource = KieServices.get().getResources()
                .newReaderResource(new StringReader(getDRLProvider().getDrl(numberOfRules)))
                .setResourceType(ResourceType.DRL)
                .setSourcePath("drlFile.drl");
        kieContainer = BuildtimeUtil.createKieContainerFromResources(false, drlResource);
        sessionsPool = kieContainer.newKieSessionsPool(initialSessionPoolSize);
    }

    @TearDown
    public void disposeContainer() {
        sessionsPool.shutdown();
        kieContainer.dispose();
    }

    private DRLProvider getDRLProvider() {
        // First 2 nodes are shared
        final String constraints =
                "A(value == 0) \n"
                + "B(value == 1) \n"
                + "C(value > ${i})";
        return new SimpleRulesWithConstraintsProvider(constraints);
    }

    protected Collection<Object> generateFacts() {
        final List<Object> facts = new ArrayList<>();
        facts.add(new A(0));
        facts.add(new B(1));
        for (int i = 2; i < numberOfFacts; i++) {
            facts.add(new C(i));
        }
        return facts;
    }

    protected void insertFactsIntoSession(final KieSession kieSession, final Collection<Object> facts, final Blackhole eater) {
        facts.forEach(fact -> eater.consume(kieSession.insert(fact)));
    }

    protected void exerciseSession(final KieSession ksession, final Collection<Object> factsForSession, final Blackhole eater) {
        insertFactsIntoSession( ksession, factsForSession, eater );
        eater.consume(ksession.fireAllRules());
    }

    /**
     * Simple simulation of how a JBPM ProcessRuntimeImpl registers an event listener.
     * Session pooling should then make sure that no multiple process runtimes are generated
     * which would lead to a multiplication of event listeners.
     */
    private static class DummyListeningProcessRuntime implements InternalProcessRuntime {

        public DummyListeningProcessRuntime(InternalWorkingMemory workingMemory) {
            workingMemory.addEventListener(new DefaultAgendaEventListener());
        }

        @Override
        public void dispose() { }

        @Override
        public void setProcessEventSupport( ProcessEventSupport processEventSupport ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void clearProcessInstances() {
            // do nothing.
        }

        @Override
        public void clearProcessInstancesState() {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance startProcess( String processId, CorrelationKey correlationKey, Map<String, Object> parameters ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance createProcessInstance( String processId, CorrelationKey correlationKey, Map<String, Object> parameters ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance getProcessInstance( CorrelationKey correlationKey ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void addEventListener( ProcessEventListener listener ) {
            // do nothing.
        }

        @Override
        public void removeEventListener( ProcessEventListener listener ) {
            // do nothing.
        }

        @Override
        public Collection<ProcessEventListener> getProcessEventListeners() {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance startProcess( String processId ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance startProcess( String processId, Map<String, Object> parameters ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance createProcessInstance( String processId, Map<String, Object> parameters ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance startProcessInstance( long processInstanceId ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void signalEvent( String type, Object event ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void signalEvent( String type, Object event, long processInstanceId ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public Collection<ProcessInstance> getProcessInstances() {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance getProcessInstance( long processInstanceId ) {
            return null;
        }

        @Override
        public ProcessInstance getProcessInstance( long processInstanceId, boolean readonly ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void abortProcessInstance( long processInstanceId ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public WorkItemManager getWorkItemManager() {
            throw new UnsupportedOperationException( );
        }
    }
}
