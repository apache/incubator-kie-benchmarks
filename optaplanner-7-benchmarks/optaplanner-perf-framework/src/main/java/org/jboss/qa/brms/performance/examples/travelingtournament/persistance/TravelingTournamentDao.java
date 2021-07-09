package org.jboss.qa.brms.performance.examples.travelingtournament.persistance;

import org.jboss.qa.brms.performance.persistence.XStreamSolutionDao;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

public class TravelingTournamentDao extends XStreamSolutionDao<TravelingTournament> {

    public TravelingTournamentDao() {
        super("travelingtournament", TravelingTournament.class);
    }
}
