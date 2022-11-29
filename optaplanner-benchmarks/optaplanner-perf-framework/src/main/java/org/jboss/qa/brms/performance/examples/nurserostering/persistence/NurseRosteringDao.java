package org.jboss.qa.brms.performance.examples.nurserostering.persistence;

import org.jboss.qa.brms.performance.persistence.JacksonSolutionDao;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.persistence.NurseRosterSolutionFileIO;

public class NurseRosteringDao extends JacksonSolutionDao<NurseRoster> {

    public NurseRosteringDao() {
        super("nurserostering", new NurseRosterSolutionFileIO());
    }
}
