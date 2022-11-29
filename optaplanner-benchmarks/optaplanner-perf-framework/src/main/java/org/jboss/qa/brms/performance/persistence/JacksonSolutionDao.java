package org.jboss.qa.brms.performance.persistence;

import org.optaplanner.persistence.jackson.impl.domain.solution.JacksonSolutionFileIO;

import java.io.File;

public class JacksonSolutionDao<Solution_>  extends AbstractSolutionDao<Solution_>{

    JacksonSolutionFileIO<Solution_> jacksonSolutionFileIO;

    public JacksonSolutionDao(String dirName, JacksonSolutionFileIO<Solution_> solutionFileIO) {
        super(dirName);
        jacksonSolutionFileIO = solutionFileIO;
    }

    @Override
    public String getFileExtension() {
        return jacksonSolutionFileIO.getInputFileExtension();
    }

    @Override
    public Solution_ readSolution(File inputSolutionFile) {
        Solution_ solution = jacksonSolutionFileIO.read(inputSolutionFile);
        logger.info("Opened: {}", inputSolutionFile);
        return solution;
    }

    @Override
    public void writeSolution(Solution_ solution_, File outputSolutionFile) {
        jacksonSolutionFileIO.write(solution_, outputSolutionFile);
        logger.info("Saved: {}", outputSolutionFile);
    }
}
