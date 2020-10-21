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

package org.drools.benchmarks.common.util;

import java.util.Collection;
import java.util.Map;

import org.drools.core.event.ProcessEventSupport;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.internal.process.CorrelationKey;

public class DummyProcessRuntime implements InternalProcessRuntime {

    @Override
    public void dispose() {
        // Nothing to do here.
    }

    @Override
    public void setProcessEventSupport(final ProcessEventSupport processEventSupport) {
        // Nothing to do here.
    }

    @Override
    public void clearProcessInstances() {
        // Nothing to do here.
    }

    @Override
    public void clearProcessInstancesState() {
        // Nothing to do here.
    }

    @Override
    public void addEventListener(final ProcessEventListener processEventListener) {
        // Nothing to do here.
    }

    @Override
    public void removeEventListener(final ProcessEventListener processEventListener) {
        // Nothing to do here.
    }

    @Override
    public Collection<ProcessEventListener> getProcessEventListeners() {
        return null;
    }

    @Override
    public ProcessInstance startProcess(final String s) {
        return null;
    }

    @Override
    public ProcessInstance startProcess(final String s, final Map<String, Object> map) {
        return null;
    }

    @Override
    public ProcessInstance startProcess(String s, AgendaFilter agendaFilter) { return null; }

    @Override
    public ProcessInstance startProcess(String s, Map<String, Object> map, AgendaFilter agendaFilter) { return null; }

    @Override public ProcessInstance startProcessFromNodeIds(String s, Map<String, Object> map, String... strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessInstance createProcessInstance(final String s, final Map<String, Object> map) {
        return null;
    }

    @Override
    public ProcessInstance startProcessInstance(final long l) {
        return null;
    }

    @Override
    public void signalEvent(final String s, final Object o) {
        // Nothing to do here.
    }

    @Override
    public void signalEvent(final String s, final Object o, final long l) {
        // Nothing to do here.
    }

    @Override
    public Collection<ProcessInstance> getProcessInstances() {
        return null;
    }

    @Override
    public ProcessInstance getProcessInstance(final long l) {
        return null;
    }

    @Override
    public ProcessInstance getProcessInstance(final long l, final boolean b) {
        return null;
    }

    @Override
    public void abortProcessInstance(final long l) {
        // Nothing to do here.
    }

    @Override
    public WorkItemManager getWorkItemManager() {
        return null;
    }

    @Override
    public ProcessInstance startProcess(final String s, final CorrelationKey correlationKey, final Map<String, Object> map) {
        return null;
    }

    @Override
    public ProcessInstance createProcessInstance(final String s, final CorrelationKey correlationKey, final Map<String, Object> map) {
        return null;
    }

    @Override public ProcessInstance startProcessFromNodeIds(String s, CorrelationKey correlationKey, Map<String, Object> map,
            String... strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessInstance getProcessInstance(final CorrelationKey correlationKey) {
        return null;
    }
}
