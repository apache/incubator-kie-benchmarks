/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.modelcompiler.CanonicalKieModule;
import org.drools.modelcompiler.builder.CanonicalModelKieProject;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.openjdk.jmh.util.FileUtils;

public final class BuildtimeUtil {

    public static ReleaseId createKJarFromResources(final boolean useCanonicalModel, final Resource... resources)
            throws IOException {
        final KieServices kieServices = KieServices.get();
        final KieBuilder kieBuilder = getKieBuilderFromResources(kieServices.newKieFileSystem(), useCanonicalModel, resources);
        generateKJarFromKieBuilder(kieBuilder, useCanonicalModel);
        return kieBuilder.getKieModule().getReleaseId();
    }

    public static void generateKJarFromKieBuilder(final KieBuilder kieBuilder, final boolean useCanonicalModel)
            throws IOException {
        final ReleaseId releaseId = kieBuilder.getKieModule().getReleaseId();
        final InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModule();
        final File kjarFile = bytesToTempFile(releaseId, kieModule.getBytes(), ".jar");
        final KieModule zipKieModule;
        if (useCanonicalModel) {
            zipKieModule = new CanonicalKieModule(releaseId, kieModule.getKieModuleModel(), kjarFile);
        } else {
            zipKieModule = new ZipKieModule(releaseId, kieModule.getKieModuleModel(), kjarFile);
        }
        KieServices.get().getRepository().addKieModule(zipKieModule);
    }

    public static KieBuilder getKieBuilderFromResources(final KieFileSystem kfs, final boolean useCanonicalModel, final Resource... resources) {
        for (final Resource res : resources) {
            kfs.write(res);
        }
        kfs.writeKModuleXML(getDefaultKieModuleModel(KieServices.get()).toXML());

        final KieBuilderImpl kbuilder = (KieBuilderImpl) KieServices.Factory.get().newKieBuilder(kfs);
        if (useCanonicalModel) {
            kbuilder.buildAll(CanonicalModelKieProject::new);
        } else {
            kbuilder.buildAll();
        }

        final List<Message> msgs = kbuilder.getResults().getMessages(Message.Level.ERROR);
        if (msgs.size() > 0) {
            throw new IllegalArgumentException("KieBuilder errors: {\n" + msgs.toString() + "\n}");
        }

        return kbuilder;
    }

    public static KieModuleModel getDefaultKieModuleModel(final KieServices ks) {
        final KieModuleModel kproj = ks.newKieModuleModel();
        final KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("kbase").setDefault(true);
        kieBaseModel1.newKieSessionModel("ksession").setDefault(true);
        return kproj;
    }

    public static File bytesToTempFile(final ReleaseId releaseId, final byte[] bytes, final String extension)
            throws IOException {
        final File file = FileUtils.tempFile(extension);
        try (final FileOutputStream fos = new FileOutputStream(file, false)) {
            fos.write(bytes);
        }
        return file;
    }

    private BuildtimeUtil() {
        // It is not allowed to instantiate util classes.
    }
}
