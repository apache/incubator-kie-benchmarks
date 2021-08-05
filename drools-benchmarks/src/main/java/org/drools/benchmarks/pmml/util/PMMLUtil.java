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

import java.io.IOException;

import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.pmml.api.runtime.PMMLRuntime;

public final class PMMLUtil {

    public static PMMLRuntime getPMMLRuntimeWithResources(final boolean useCanonicalModel,
                                                          final Resource... resources) throws IOException {
        final KieContainer kieContainer = BuildtimeUtil.createKieContainerFromResources(useCanonicalModel, resources);
        return kieContainer.newKieSession().getKieRuntime(PMMLRuntime.class);
    }

    private PMMLUtil() {
        // It is not allowed to instantiate util classes.
    }
}
