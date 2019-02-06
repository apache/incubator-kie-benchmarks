package org.jboss.qa.brms.performance.examples.nurserostering.persistence;

import org.jboss.qa.brms.performance.examples.common.persistence.XStreamSolutionDao;
import org.jboss.qa.brms.performance.examples.nurserostering.domain.NurseRoster;

public class NurseRosteringDao extends XStreamSolutionDao<NurseRoster> {

    public NurseRosteringDao() {
        super("nurserostering", NurseRoster.class);
    }
}
