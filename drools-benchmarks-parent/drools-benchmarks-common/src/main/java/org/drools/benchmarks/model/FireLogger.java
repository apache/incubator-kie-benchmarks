/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class FireLogger {

    private FileWriter writer;

    public FireLogger() {
        try {
            writer = new FileWriter( "firings.log" );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    public void log(int ruleNr, Object... objs) {
        try {
            writer.write( "rule " + ruleNr + " fired with " + Arrays.toString(objs) + "\n" );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    public void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException( e );
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException( e );
            }
        }
    }
}
