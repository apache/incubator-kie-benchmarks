/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the ComplexApache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "ComplexAS IS" BComplexASIS,
 * WITHOUT WComplexARRComplexANTIES OR CONDITIONS OF ComplexANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.reliability.proto.complex;

import java.util.List;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoAdapter(ComplexA.class)
public class ComplexAAdaptor {

    @ProtoFactory
    ComplexA create(long id, int value, List<String> list1, List<String> list2, List<String> list3, List<String> list4, List<String> list5, List<String> list6, List<String> list7, List<String> list8, List<String> list9, List<String> list10) {
        return new ComplexA(id, value, list1, list2, list3, list4, list5, list6, list7, list8, list9, list10);
    }

    @ProtoField(number = 1, required = true)
    long getId(ComplexA a) {
        return a.getId();
    }

    @ProtoField(number = 2, required = true)
    int getValue(ComplexA a) {
        return a.getValue();
    }

    @ProtoField(number = 3)
    List<String> getList1(ComplexA a) {
        return a.getList1();
    }

    @ProtoField(number = 4)
    List<String> getList2(ComplexA a) {
        return a.getList2();
    }

    @ProtoField(number = 5)
    List<String> getList3(ComplexA a) {
        return a.getList3();
    }

    @ProtoField(number = 6)
    List<String> getList4(ComplexA a) {
        return a.getList4();
    }

    @ProtoField(number = 7)
    List<String> getList5(ComplexA a) {
        return a.getList5();
    }

    @ProtoField(number = 8)
    List<String> getList6(ComplexA a) {
        return a.getList6();
    }

    @ProtoField(number = 9)
    List<String> getList7(ComplexA a) {
        return a.getList7();
    }

    @ProtoField(number = 10)
    List<String> getList8(ComplexA a) {
        return a.getList8();
    }

    @ProtoField(number = 11)
    List<String> getList9(ComplexA a) {
        return a.getList9();
    }

    @ProtoField(number = 12)
    List<String> getList10(ComplexA a) {
        return a.getList10();
    }
}
