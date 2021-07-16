package org.jboss.qa.brms.performance.examples.flightcrewscheduling.persistence;

import java.io.File;

import org.jboss.qa.brms.performance.persistence.AbstractSolutionDao;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;
import org.optaplanner.examples.flightcrewscheduling.persistence.FlightCrewSchedulingXlsxFileIO;

public class FlightCrewSchedulingDao extends AbstractSolutionDao<FlightCrewSolution> {

    private FlightCrewSchedulingXlsxFileIO fileIO = new FlightCrewSchedulingXlsxFileIO();

    public FlightCrewSchedulingDao() {
        super("flightcrewscheduling");
    }

    @Override
    public String getFileExtension() {
        return "xlsx";
    }

    @Override
    public FlightCrewSolution readSolution(File inputSolutionFile) {
        return fileIO.read(inputSolutionFile);
    }

    @Override
    public void writeSolution(FlightCrewSolution flightCrewSolution, File outputSolutionFile) {
        fileIO.write(flightCrewSolution, outputSolutionFile);
    }
}
