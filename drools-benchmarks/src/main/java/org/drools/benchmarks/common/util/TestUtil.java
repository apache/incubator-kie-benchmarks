/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.drools.benchmarks.common.util;

import org.kie.api.KieBaseConfiguration;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.conf.RuleEngineOption;

public final class TestUtil {

    public static boolean isSmokeTestsRun() {
        return Boolean.TRUE.toString().equals(System.getProperty(Constants.PROP_KEY_SMOKE_TESTS));
    }

    public static boolean useReteoo() {
        return RuleEngineOption.RETEOO.toString().equals(System.getProperty(Constants.PROP_KEY_ENGINE_TYPE));
    }

    public static KieBaseConfiguration getKieBaseConfiguration() {
        final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        if (TestUtil.useReteoo()) {
            kieBaseConfiguration.setOption( RuleEngineOption.RETEOO );
        } else {
            kieBaseConfiguration.setOption( RuleEngineOption.PHREAK );
        }
        return kieBaseConfiguration;
    }

    private TestUtil() {
    }
}
