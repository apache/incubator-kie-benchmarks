/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.benchmarks.dmn.feel.infixexecutors;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;

public class FEELInfixExecutorBenchmarkUtils {


    private FEELInfixExecutorBenchmarkUtils() {
    }

    static Object[] getObjectArray(String types) {
        String[] parsedTypes = types.split(" ");
        String leftType = parsedTypes[0];
        String rightType = parsedTypes[1];
        return new Object[]{getObject(leftType, 1), getObject(rightType, 2)};
    }

    static Object[] getBooleanArray(String args) {
        String[] parsedArgs = args.split(" ");
        String leftArg = parsedArgs[0];
        String rightArg = parsedArgs[1];
        return new Object[]{Boolean.valueOf(leftArg), Boolean.valueOf(rightArg)};
    }

    private static Object getObject(String type, int value) {
        switch (type) {
            case "String":
                return String.valueOf(value);
            case "Duration":
                return Duration.ofDays(value);
            case "OffsetDateTime":
                return OffsetDateTime.now();
            case "ChronoPeriod":
                ChronoLocalDate startDate = ChronoLocalDate.from(OffsetDateTime.now());
                ChronoLocalDate endDate = startDate.plus(value, ChronoUnit.YEARS);
                return ChronoPeriod.between(startDate, endDate);
            case "int":
                return value;
            default:
                throw new IllegalArgumentException("Unexpected type " + type);
        }
    }
}   