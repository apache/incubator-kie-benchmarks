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

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.KieSessionOption;

public final class RuntimeUtil {

    public static KieSession createKieSession(final KieBase kieBase, final KieSessionOption... kieSessionOptions) {
        return kieBase.newKieSession(getKieSessionConfiguration(kieSessionOptions), null);
    }

    private static KieSessionConfiguration getKieSessionConfiguration(final KieSessionOption... kieSessionOptions) {
        final KieSessionConfiguration kieSessionConfiguration = KieServices.Factory.get().newKieSessionConfiguration();
        for (final KieSessionOption kieSessionOption : kieSessionOptions) {
            kieSessionConfiguration.setOption(kieSessionOption);
        }
        return kieSessionConfiguration;
    }

    private RuntimeUtil() {
        // It is not allowed to instantiate util classes.
    }
}
