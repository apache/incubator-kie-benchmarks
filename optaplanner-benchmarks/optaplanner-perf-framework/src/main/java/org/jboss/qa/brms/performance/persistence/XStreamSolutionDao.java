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
package org.jboss.qa.brms.performance.persistence;

import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

import java.io.File;

public abstract class XStreamSolutionDao<Solution_> extends AbstractSolutionDao<Solution_> {

    protected XStreamSolutionFileIO<Solution_> xStreamSolutionFileIO;

    public XStreamSolutionDao(String dirName, Class<?>... xStreamAnnotations) {
        super(dirName);
        xStreamSolutionFileIO = new XStreamSolutionFileIO<Solution_>(xStreamAnnotations);
    }

    @Override
    public String getFileExtension() {
        return xStreamSolutionFileIO.getInputFileExtension();
    }

    @Override
    public Solution_ readSolution(File inputSolutionFile) {
        Solution_ solution = xStreamSolutionFileIO.read(inputSolutionFile);
        logger.info("Opened: {}", inputSolutionFile);
        return solution;
    }

    @Override
    public void writeSolution(Solution_ solution, File outputSolutionFile) {
        xStreamSolutionFileIO.write(solution, outputSolutionFile);
        logger.info("Saved: {}", outputSolutionFile);
    }

}
