/*
 * Copyright 2014 JBoss Inc
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
package org.jboss.qa.brms.performance.examples.tsp.domain.solver;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jboss.qa.brms.performance.examples.tsp.domain.Domicile;
import org.jboss.qa.brms.performance.examples.tsp.domain.TravelingSalesmanTour;
import org.jboss.qa.brms.performance.examples.tsp.domain.Visit;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

public class DomicileDistanceVisitDifficultyWeightFactory implements SelectionSorterWeightFactory<TravelingSalesmanTour, Visit> {

    @Override
    public Comparable createSorterWeight(TravelingSalesmanTour tour, Visit visit) {
        Domicile domicile = tour.getDomicile();
        long domicileRoundTripDistance = domicile.getDistanceTo(visit) + visit.getDistanceTo(domicile);
        return new DomicileDistanceVisitDifficultyWeight(visit, domicileRoundTripDistance);
    }

    public static class DomicileDistanceVisitDifficultyWeight implements Comparable<DomicileDistanceVisitDifficultyWeight> {

        private final Visit visit;
        private final long domicileRoundTripDistance;

        public DomicileDistanceVisitDifficultyWeight(Visit visit, long domicileRoundTripDistance) {
            this.visit = visit;
            this.domicileRoundTripDistance = domicileRoundTripDistance;
        }

        @Override
        public int compareTo(DomicileDistanceVisitDifficultyWeight other) {
            return new CompareToBuilder()
                    .append(domicileRoundTripDistance, other.domicileRoundTripDistance)
                    .append(visit.getLocation().getLatitude(), other.visit.getLocation().getLatitude())
                    .append(visit.getId(), other.visit.getId())
                    .toComparison();
        }

    }

}
