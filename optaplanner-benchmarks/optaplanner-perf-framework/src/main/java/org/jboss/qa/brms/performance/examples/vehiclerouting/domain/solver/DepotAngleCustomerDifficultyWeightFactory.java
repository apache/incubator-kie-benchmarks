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
package org.jboss.qa.brms.performance.examples.vehiclerouting.domain.solver;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.Customer;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.Depot;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

/**
 * On large datasets, the constructed solution looks like pizza slices.
 */
public class DepotAngleCustomerDifficultyWeightFactory
        implements SelectionSorterWeightFactory<VehicleRoutingSolution, Customer> {

    @Override
    public Comparable createSorterWeight(VehicleRoutingSolution vehicleRoutingSolution, Customer customer) {
        Depot depot = vehicleRoutingSolution.getDepotList().get(0);
        return new DepotAngleCustomerDifficultyWeight(customer,
                customer.getLocation().getAngle(depot.getLocation()),
                customer.getLocation().getDistanceTo(depot.getLocation())
                + depot.getLocation().getDistanceTo(customer.getLocation()));
    }

    public static class DepotAngleCustomerDifficultyWeight
            implements Comparable<DepotAngleCustomerDifficultyWeight> {

        private final Customer customer;
        private final double depotAngle;
        private final long depotRoundTripDistance;

        public DepotAngleCustomerDifficultyWeight(Customer customer,
                double depotAngle, long depotRoundTripDistance) {
            this.customer = customer;
            this.depotAngle = depotAngle;
            this.depotRoundTripDistance = depotRoundTripDistance;
        }

        @Override
        public int compareTo(DepotAngleCustomerDifficultyWeight other) {
            return new CompareToBuilder()
                    .append(depotAngle, other.depotAngle)
                    .append(depotRoundTripDistance, other.depotRoundTripDistance) // Ascending (further from the depot are more difficult)
                    .append(customer.getId(), other.customer.getId())
                    .toComparison();
        }

    }

}
