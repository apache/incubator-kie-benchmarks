/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.reliability.proto;

import org.drools.benchmarks.common.model.B;
import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoAdapter(B.class)
public class BAdaptor {

    @ProtoFactory
    B create(long id, int value) {
        return new B(id, value);
    }

    @ProtoField(number = 1, required = true)
    long getId(B b) {
        return b.getId();
    }

    @ProtoField(number = 2, required = true)
    int getValue(B b) {
        return b.getValue();
    }
}
