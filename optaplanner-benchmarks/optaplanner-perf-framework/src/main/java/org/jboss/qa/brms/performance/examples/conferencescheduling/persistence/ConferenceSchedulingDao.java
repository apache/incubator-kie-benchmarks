package org.jboss.qa.brms.performance.examples.conferencescheduling.persistence;

import java.io.File;

import org.jboss.qa.brms.performance.examples.common.persistence.AbstractSolutionDao;
import org.jboss.qa.brms.performance.examples.conferencescheduling.domain.ConferenceSolution;

public class ConferenceSchedulingDao extends AbstractSolutionDao<ConferenceSolution> {
    private ConferenceSchedulingXlsxFileIO conferenceSchedulingXlsxFileIO = new ConferenceSchedulingXlsxFileIO();

    public ConferenceSchedulingDao() {
        super("conferencescheduling");
    }

    @Override
    public String getFileExtension() {
        return conferenceSchedulingXlsxFileIO.getInputFileExtension();
    }

    @Override
    public ConferenceSolution readSolution(File inputSolutionFile) {
        return conferenceSchedulingXlsxFileIO.read(inputSolutionFile);
    }

    @Override
    public void writeSolution(ConferenceSolution conferenceSolution, File outputSolutionFile) {
        throw new UnsupportedOperationException("Unsupported solution write");
    }
}
