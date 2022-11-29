package org.jboss.qa.brms.performance.examples.travelingtournament.persistance;

import org.jboss.qa.brms.performance.persistence.JacksonSolutionDao;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentSolutionFileIO;

public class TravelingTournamentDao extends JacksonSolutionDao<TravelingTournament> {

    public TravelingTournamentDao() {
        super("travelingtournament", new TravelingTournamentSolutionFileIO());
    }
}
