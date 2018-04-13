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
package org.jboss.qa.brms.performance.examples.cloudbalancing.solver.move;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudBalance;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudComputer;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Arrays;
import java.util.Collection;

public class CloudProcessSwapMove extends AbstractMove<CloudBalance> {

    private CloudProcess leftCloudProcess;
    private CloudProcess rightCloudProcess;

    public CloudProcessSwapMove(CloudProcess leftCloudProcess, CloudProcess rightCloudProcess) {
        this.leftCloudProcess = leftCloudProcess;
        this.rightCloudProcess = rightCloudProcess;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<CloudBalance> scoreDirector) {
        return !ObjectUtils.equals(leftCloudProcess.getComputer(), rightCloudProcess.getComputer());
    }

    @Override
    public CloudProcessSwapMove createUndoMove(ScoreDirector<CloudBalance> scoreDirector) {
        return new CloudProcessSwapMove(rightCloudProcess, leftCloudProcess);
    }

    @Override
    public void doMoveOnGenuineVariables(ScoreDirector<CloudBalance> scoreDirector) {
//    public void doMove(ScoreDirector scoreDirector) {
        CloudComputer oldLeftCloudComputer = leftCloudProcess.getComputer();
        CloudComputer oldRightCloudComputer = rightCloudProcess.getComputer();
        CloudBalancingMoveHelper.moveCloudComputer(scoreDirector, leftCloudProcess, oldRightCloudComputer);
        CloudBalancingMoveHelper.moveCloudComputer(scoreDirector, rightCloudProcess, oldLeftCloudComputer);
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftCloudProcess, rightCloudProcess);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(leftCloudProcess.getComputer(), rightCloudProcess.getComputer());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CloudProcessSwapMove) {
            CloudProcessSwapMove other = (CloudProcessSwapMove) o;
            return new EqualsBuilder()
                    .append(leftCloudProcess, other.leftCloudProcess)
                    .append(rightCloudProcess, other.rightCloudProcess)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftCloudProcess)
                .append(rightCloudProcess)
                .toHashCode();
    }

    @Override
    public String toString() {
        return leftCloudProcess + " {" + leftCloudProcess.getComputer() + "} <-> "
                + rightCloudProcess + " {" + rightCloudProcess.getComputer() + "}";
    }

}
