/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.pmml.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.io.ClassPathResource;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.PMMLRuntimeFactory;
import org.kie.pmml.api.compilation.PMMLCompilationContext;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.compiler.PMMLCompilationContextImpl;
import org.kie.pmml.evaluator.utils.SPIUtils;

import static org.kie.efesto.common.api.utils.FileUtils.getFileFromURL;

public final class PMMLUtil {

    private static final PMMLRuntimeFactory PMML_RUNTIME_FACTORY = SPIUtils.getPMMLRuntimeFactory(false);

    public static PMMLRuntime getPMMLRuntimeWithResources(final boolean useCanonicalModel,
                                                          final Resource... resources) throws IOException {
        final KieContainer kieContainer = BuildtimeUtil.createKieContainerFromResources(useCanonicalModel, resources);
        return kieContainer.newKieSession().getKieRuntime(PMMLRuntime.class);

//        return PMML_RUNTIME_FACTORY.getPMMLRuntimeFromClasspath(FILE_NAME);
    }

    public static File getPMMLFile(String filePath) {
        ClassPathResource res = (ClassPathResource) KieServices.get().getResources()
                .newClassPathResource(filePath);
        final URL resUrl;
        try {
            resUrl = res.getURL();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File toReturn = getFileFromURL(resUrl).orElseThrow(() -> new RuntimeException("Failed to retrieve File from " + resUrl));
        if (!toReturn.exists()) {
            throw new RuntimeException(toReturn.getAbsolutePath() + " does not exists");
        }
        return toReturn;
    }

    public static KieMemoryCompiler.MemoryCompilerClassLoader compileModel(File pmmlFile) {
        CompilationManager compilationManager = org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve CompilationManager"));
        KieMemoryCompiler.MemoryCompilerClassLoader toReturn =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        PMMLCompilationContext pmmlCompilationContext = new PMMLCompilationContextImpl(pmmlFile.getName(), toReturn);
        compilationManager.processResource(pmmlCompilationContext, new EfestoFileResource(pmmlFile));
        return toReturn;
    }




    private PMMLUtil() {
        // It is not allowed to instantiate util classes.
    }
}
