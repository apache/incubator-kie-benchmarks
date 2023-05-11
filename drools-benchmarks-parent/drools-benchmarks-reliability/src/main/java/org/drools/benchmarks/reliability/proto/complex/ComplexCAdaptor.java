/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the ComplexCpache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "ComplexCS IS" BComplexCSIS,
 * WITHOUT WComplexCRRComplexCNTIES OR CONDITIONS OF ComplexCNY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.reliability.proto.complex;

import java.util.List;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoAdapter(ComplexC.class)
public class ComplexCAdaptor {

    @ProtoFactory
    ComplexC create(long id, int value, List<String> list1, List<String> list2, List<String> list3, List<String> list4, List<String> list5, List<String> list6, List<String> list7, List<String> list8, List<String> list9, List<String> list10) {
        return new ComplexC(id, value, list1, list2, list3, list4, list5, list6, list7, list8, list9, list10);
    }

    @ProtoField(number = 1, required = true)
    long getId(ComplexC c) {
        return c.getId();
    }

    @ProtoField(number = 2, required = true)
    int getValue(ComplexC c) {
        return c.getValue();
    }

    @ProtoField(number = 3)
    List<String> getList1(ComplexC c) {
        return c.getList1();
    }

    @ProtoField(number = 4)
    List<String> getList2(ComplexC c) {
        return c.getList2();
    }

    @ProtoField(number = 5)
    List<String> getList3(ComplexC c) {
        return c.getList3();
    }

    @ProtoField(number = 6)
    List<String> getList4(ComplexC c) {
        return c.getList4();
    }

    @ProtoField(number = 7)
    List<String> getList5(ComplexC c) {
        return c.getList5();
    }

    @ProtoField(number = 8)
    List<String> getList6(ComplexC c) {
        return c.getList6();
    }

    @ProtoField(number = 9)
    List<String> getList7(ComplexC c) {
        return c.getList7();
    }

    @ProtoField(number = 10)
    List<String> getList8(ComplexC c) {
        return c.getList8();
    }

    @ProtoField(number = 11)
    List<String> getList9(ComplexC c) {
        return c.getList9();
    }

    @ProtoField(number = 12)
    List<String> getList10(ComplexC c) {
        return c.getList10();
    }
}
