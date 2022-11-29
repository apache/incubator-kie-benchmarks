package org.jboss.qa.brms.performance.examples.nurserostering.persistence;

import org.jboss.qa.brms.performance.persistence.JacksonSolutionDao;
import org.jboss.qa.brms.performance.persistence.XStreamSolutionDao;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;

public class NurseRosteringDao extends JacksonSolutionDao<NurseRoster> {

    public NurseRosteringDao() {
        super("nurserostering", NurseRoster.class);
    }
}
