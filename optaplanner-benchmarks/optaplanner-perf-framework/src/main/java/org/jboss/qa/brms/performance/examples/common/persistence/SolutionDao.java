/*
 * Copyright 2010 JBoss Inc
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
package org.jboss.qa.brms.performance.examples.common.persistence;

import org.optaplanner.core.api.domain.solution.Solution;

import java.io.File;

/**
 * Data Access Object for the examples.
 */
public interface SolutionDao {

    String getDirName();

    File getDataDir();

    String getFileExtension();

    Solution readSolution(File inputSolutionFile);

    void writeSolution(Solution solution, File outputSolutionFile);

}
