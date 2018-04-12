/*
 * Copyright 2010 JBoss Inc
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
package org.jboss.qa.brms.performance.examples.cloudbalancing.solver.move.factory;

import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudBalance;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudProcess;
import org.jboss.qa.brms.performance.examples.cloudbalancing.solver.move.CloudProcessSwapMove;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CloudProcessSwapMoveFactory implements MoveListFactory<CloudBalance> {

    @Override
    public List<CloudProcessSwapMove> createMoveList(CloudBalance cloudBalance) {
        List<CloudProcess> cloudProcessList = cloudBalance.getProcessList();
        List<CloudProcessSwapMove> moveList = new ArrayList<CloudProcessSwapMove>();
        for (ListIterator<CloudProcess> leftIt = cloudProcessList.listIterator(); leftIt.hasNext();) {
            CloudProcess leftCloudProcess = leftIt.next();
            for (ListIterator<CloudProcess> rightIt = cloudProcessList.listIterator(leftIt.nextIndex()); rightIt.hasNext();) {
                CloudProcess rightCloudProcess = rightIt.next();
                moveList.add(new CloudProcessSwapMove(leftCloudProcess, rightCloudProcess));
            }
        }
        return moveList;
    }

}
