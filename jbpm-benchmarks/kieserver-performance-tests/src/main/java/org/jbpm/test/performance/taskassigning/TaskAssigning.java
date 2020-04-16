/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.jbpm.test.performance.taskassigning;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jbpm.test.performance.kieserver.KieServerClient;
import org.jbpm.test.performance.kieserver.KieServerTestConfig;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.UserTaskServicesClient;

/**
 * A Common ground for task assigning load test scenarios. Before running any of these scenarios, following
 * preconditions have to be met:
 * <li>
 *      Two KIE servers are configured; the first one hosts a task planning extension and the second one task assignment
 *      and jBPM extensions. However, the scenario communicates only with the jBPM extension via KIE server remote API.
 * </li>
 * <li>
 *     KIE server task planning extension is configured to optimize for a planning window that can contain all tasks
 *     created in this scenario.
 * </li>
 */
public abstract class TaskAssigning {
    protected static final String CONTAINER_ID = "kieserver-assets";

    private static final String DATA_SOURCE_JNDI = KieServerTestConfig.getInstance().getDataSourceJndi();
    private static final int ABORT_PROCESS_BATCH_SIZE = 100;

    private final KieServerClient client;
    private final ProcessServicesClient processClient;
    private final UserTaskServicesClient taskClient;
    private final QueryServicesClient queryClient;

    public TaskAssigning() {
        // Must be set before the KIE Server client is created.
        System.setProperty("org.kie.server.bypass.auth.user", Boolean.toString(true));
        client = new KieServerClient();
        processClient = client.getProcessClient();
        taskClient = client.getTaskClient();
        queryClient = client.getQueryClient();
    }

    protected void registerQuery(String queryName, String queryCode) {
        QueryDefinition query = new QueryDefinition();
        query.setName(queryName);
        query.setSource(DATA_SOURCE_JNDI);
        query.setExpression(queryCode);
        query.setTarget("CUSTOM");
        queryClient.replaceQuery(query);
    }

    protected void abortAllProcesses() {
        List<ProcessInstance> processInstanceList = processClient.findProcessInstances(CONTAINER_ID, 0, Integer.MAX_VALUE);
        List<Long> processInstanceIdList = processInstanceList.stream()
                .map(processInstance -> processInstance.getId())
                .collect(Collectors.toList());
        abortAllProcessesInBatch(processInstanceIdList, ABORT_PROCESS_BATCH_SIZE);
    }

    protected void abortAllProcessesInBatch(List<Long> processInstanceIdList, int batchSize) {
        AtomicInteger counter = new AtomicInteger();
        processInstanceIdList.stream()
                .collect(Collectors.groupingBy(__ -> counter.incrementAndGet() / batchSize))
                .values()
                .forEach(batch -> processClient.abortProcessInstances(CONTAINER_ID, batch));
    }

    protected void startProcesses(String processId, int count) {
        for (int i = 0; i < count; i++) {
            getProcessClient().startProcess(CONTAINER_ID, processId);
        }
    }

    protected void shutdownExecutorService(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    public ProcessServicesClient getProcessClient() {
        return processClient;
    }

    public UserTaskServicesClient getTaskClient() {
        return taskClient;
    }

    public QueryServicesClient getQueryClient() {
        return queryClient;
    }
}
