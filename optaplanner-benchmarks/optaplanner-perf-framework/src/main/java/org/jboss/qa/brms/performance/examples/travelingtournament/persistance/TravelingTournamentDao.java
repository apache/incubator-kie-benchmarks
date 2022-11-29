package org.jboss.qa.brms.performance.examples.travelingtournament.persistance;

import org.jboss.qa.brms.performance.persistence.JacksonSolutionDao;
import org.jboss.qa.brms.performance.persistence.XStreamSolutionDao;
import org.optaplanner.examples.travelingtournament.domain.Team;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TravelingTournamentDao extends JacksonSolutionDao<TravelingTournament> {

    public TravelingTournamentDao() {
        super("travelingtournament", TravelingTournament.class);
    }

//    @Override
//    public TravelingTournament readSolution(File inputSolutionFile) {
//        TravelingTournament travelingTournament =  super.readSolution(inputSolutionFile);
//        java.util.Map<Long, Team> teamsById = travelingTournament.getTeamList().stream()
//                .collect(Collectors.toMap(Team::getId, Function.identity()));
//        /*
//         * Replace the duplicate team instances in the distanceToTeamMap by references to instances from
//         * the teamList.
//         */
//        for (Team team : travelingTournament.getTeamList()) {
//            Map<Team, Integer> newTravelDistanceMap = deduplicateMap(team.getDistanceToTeamMap(),
//                    teamsById, Team::getId);
//            team.setDistanceToTeamMap(newTravelDistanceMap);
//        }
//        return travelingTournament;
//    }
//    protected <Key_, Value_, Index_> Map<Key_, Value_> deduplicateMap(Map<Key_, Value_> originalMap, Map<Index_, Key_> index,
//                                                                      Function<Key_, Index_> idFunction) {
//        if (originalMap == null || originalMap.isEmpty()) {
//            return originalMap;
//        }
//
//        Map<Key_, Value_> newMap = new LinkedHashMap<>(originalMap.size());
//        originalMap.forEach((key, value) -> newMap.put(index.get(idFunction.apply(key)), value));
//        return newMap;
//    }
}
