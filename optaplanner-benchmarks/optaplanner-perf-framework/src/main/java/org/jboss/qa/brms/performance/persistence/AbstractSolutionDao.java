/*
 * Copyright 2013 JBoss Inc
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
package org.jboss.qa.brms.performance.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public abstract class AbstractSolutionDao<Solution_> implements SolutionDao<Solution_> {

    private static final String MODULE_NAME = "optaplanner-perf-framework";
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected String dirName;
    protected File dataDir;

    public AbstractSolutionDao(String dirName) {
        this.dirName = dirName;
        dataDir = isInDirectory(MODULE_NAME)
                ? new File("data/" + dirName)
                : new File(MODULE_NAME + "/data/" + dirName);
        if (!dataDir.exists()) {
            throw new IllegalStateException("The directory dataDir (" + dataDir.getAbsolutePath()
                    + ") does not exist.\n");
        }
    }

    @Override
    public String getDirName() {
        return dirName;
    }

    @Override
    public File getDataDir() {
        return dataDir;
    }

    private boolean isInDirectory(String dirName) {
        try {
            return new File(".").getCanonicalFile().getName().equals(dirName);
        } catch (IOException ex) {
            // not important
            return false;
        }
    }
}
