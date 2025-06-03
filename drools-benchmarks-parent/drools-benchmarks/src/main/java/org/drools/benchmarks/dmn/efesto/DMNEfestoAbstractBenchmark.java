/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.benchmarks.dmn.efesto;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.io.ClassPathResource;
import org.kie.api.io.Resource;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.efesto.compiler.model.DmnCompilationContext;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.api.model.GeneratedModelResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoFileSetResource;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoStringResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoLocalRuntimeContext;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import org.kie.memorycompiler.KieMemoryCompiler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class DMNEfestoAbstractBenchmark extends AbstractBenchmark {

    private static final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader =
            new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    private static final CompilationManager compilationManager =
            SPIUtils.getCompilationManager(true).orElseThrow(() -> new RuntimeException("Compilation Manager not " +
                    "available"));
    private static final RuntimeManager runtimeManager =
            org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(true).orElseThrow(() -> new RuntimeException("Runtime Manager not available"));

    protected Map<String, GeneratedResources> generatedResources;
    protected ModelLocalUriId modelLocalUriId;
    protected EfestoLocalRuntimeContext runtimeContext;
    protected Map<String, Object> inputData;

    protected EfestoLocalRuntimeContext getRuntimeContext(Map<String, GeneratedResources> generatedResourcesMap, ModelLocalUriId modelLocalUriId) {
        EfestoLocalRuntimeContext toReturn = (EfestoLocalRuntimeContext) ContextStorage.getEfestoRuntimeContext(modelLocalUriId);
        if (toReturn == null) {
            toReturn = EfestoRuntimeContextUtils.buildWithParentClassLoader(memoryCompilerClassLoader, generatedResourcesMap);
            ContextStorage.putEfestoRuntimeContext(modelLocalUriId, toReturn);

        }
        return toReturn;
    }

    protected Collection<EfestoOutput> evaluate(EfestoLocalRuntimeContext runtimeContext,
                                                ModelLocalUriId modelLocalUriId, Map<String, Object> inputData) {
        EfestoInput<Map<String, Object>> inputDMN = new BaseEfestoInput<>(modelLocalUriId, inputData);
        return runtimeManager.evaluateInput(runtimeContext, inputDMN);
    }

    protected ModelLocalUriId getModelLocalUriId(Map<String, GeneratedResources> generatedResources) {
        List<GeneratedModelResource> generatedModelResources = generatedResources.get("dmn")
                .stream()
                .filter(GeneratedModelResource.class::isInstance)
                .map(GeneratedModelResource.class::cast)
                .toList();
        if (generatedModelResources.size() != 1) {
            throw new IllegalStateException("Expected exactly one generated model resource");
        }
        return generatedModelResources.get(0).getModelLocalUriId();
    }

    protected HashMap getInputData() {
        return new HashMap<>(DMNFactory.newContext().getAll());
    }

    protected Map<String, GeneratedResources> compileModel(Resource dmnResource, String model) {
        try (InputStream inputStream = dmnResource.getInputStream()) {
            EfestoInputStreamResource toProcessDmn = new EfestoInputStreamResource(inputStream, model + ".dmn");
            EfestoCompilationContext dmnCompilationContext = DmnCompilationContext.buildWithParentClassLoader(memoryCompilerClassLoader);
            compilationManager.processResource(dmnCompilationContext, toProcessDmn);
            return dmnCompilationContext.getGeneratedResourcesMap();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Map<String, GeneratedResources> compileModel(String dmn, String model) {
        ModelLocalUriId dmnModelLocalUriId = new ModelLocalUriId(LocalUri.Root.append("dmn").append(model));
        EfestoStringResource toProcessDmn = new EfestoStringResource(dmn, dmnModelLocalUriId);
        EfestoCompilationContext dmnCompilationContext = DmnCompilationContext.buildWithParentClassLoader(memoryCompilerClassLoader);
        compilationManager.processResource(dmnCompilationContext, toProcessDmn);
        return dmnCompilationContext.getGeneratedResourcesMap();
    }

    protected Map<String, GeneratedResources> compileModel(File dmnFile) {
        EfestoFileResource toProcessDmn = new EfestoFileResource(dmnFile);
        EfestoCompilationContext dmnCompilationContext = DmnCompilationContext.buildWithParentClassLoader(memoryCompilerClassLoader);
        compilationManager.processResource(dmnCompilationContext, toProcessDmn);
        return dmnCompilationContext.getGeneratedResourcesMap();
    }

    protected Map<String, GeneratedResources> compileModels(Collection<File> dmnFiles) {
        ModelLocalUriId dmnModelLocalUriId = new ModelLocalUriId(LocalUri.Root.append("dmn").append("benchmarks"));
        EfestoFileSetResource toProcessDmn = new EfestoFileSetResource(new HashSet<>(dmnFiles), dmnModelLocalUriId);
        EfestoCompilationContext dmnCompilationContext = DmnCompilationContext.buildWithParentClassLoader(memoryCompilerClassLoader);
        compilationManager.processResource(dmnCompilationContext, toProcessDmn);
        return dmnCompilationContext.getGeneratedResourcesMap();
    }

    protected String getDmnContent(String resourceName) {
        final Resource dmnResource = new ClassPathResource(resourceName);
        try (InputStream inputStream = dmnResource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}   