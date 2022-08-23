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

import org.drools.io.ClassPathResource;
import org.kie.api.KieServices;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.compilation.PMMLCompilationContext;
import org.kie.pmml.compiler.PMMLCompilationContextImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public final class PMMLUtil {

    public static File getPMMLFile(String filePath, String fileName) {
        ClassPathResource res = (ClassPathResource) KieServices.get().getResources()
                .newClassPathResource(filePath);
        try(InputStream inputStream = res.getInputStream()) {
            return writeResourceToFile(inputStream, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static KieMemoryCompiler.MemoryCompilerClassLoader compileModel(File pmmlFile) {
        CompilationManager compilationManager = org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve CompilationManager"));
        KieMemoryCompiler.MemoryCompilerClassLoader toReturn =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        PMMLCompilationContext pmmlCompilationContext = new PMMLCompilationContextImpl(pmmlFile.getName(), toReturn);
        compilationManager.processResource(pmmlCompilationContext, new EfestoFileResource(pmmlFile));
        return toReturn;
    }

    static File writeResourceToFile(InputStream configStream, String fileName) throws IOException {
        String tempDirPath = "./tmp_pmml";
        File tempDir = new File(tempDirPath);
        if (!tempDir.exists()) {
            Files.createDirectory(tempDir.toPath());
        }
        File toReturn = new File(tempDir, fileName);
        Files.copy(configStream, toReturn.toPath(), REPLACE_EXISTING);
        return toReturn;
    }

    private PMMLUtil() {
        // It is not allowed to instantiate util classes.
    }
}
