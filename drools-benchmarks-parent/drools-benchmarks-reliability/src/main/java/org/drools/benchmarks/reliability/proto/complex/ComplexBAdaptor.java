/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.benchmarks.reliability.proto.complex;

import java.util.List;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoAdapter(ComplexB.class)
public class ComplexBAdaptor {

    @ProtoFactory
    ComplexB create(long id, int value, List<String> list1, List<String> list2, List<String> list3, List<String> list4, List<String> list5, List<String> list6, List<String> list7, List<String> list8, List<String> list9, List<String> list10) {
        return new ComplexB(id, value, list1, list2, list3, list4, list5, list6, list7, list8, list9, list10);
    }

    @ProtoField(number = 1, required = true)
    long getId(ComplexB b) {
        return b.getId();
    }

    @ProtoField(number = 2, required = true)
    int getValue(ComplexB b) {
        return b.getValue();
    }

    @ProtoField(number = 3)
    List<String> getList1(ComplexB b) {
        return b.getList1();
    }

    @ProtoField(number = 4)
    List<String> getList2(ComplexB b) {
        return b.getList2();
    }

    @ProtoField(number = 5)
    List<String> getList3(ComplexB b) {
        return b.getList3();
    }

    @ProtoField(number = 6)
    List<String> getList4(ComplexB b) {
        return b.getList4();
    }

    @ProtoField(number = 7)
    List<String> getList5(ComplexB b) {
        return b.getList5();
    }

    @ProtoField(number = 8)
    List<String> getList6(ComplexB b) {
        return b.getList6();
    }

    @ProtoField(number = 9)
    List<String> getList7(ComplexB b) {
        return b.getList7();
    }

    @ProtoField(number = 10)
    List<String> getList8(ComplexB b) {
        return b.getList8();
    }

    @ProtoField(number = 11)
    List<String> getList9(ComplexB b) {
        return b.getList9();
    }

    @ProtoField(number = 12)
    List<String> getList10(ComplexB b) {
        return b.getList10();
    }
}
