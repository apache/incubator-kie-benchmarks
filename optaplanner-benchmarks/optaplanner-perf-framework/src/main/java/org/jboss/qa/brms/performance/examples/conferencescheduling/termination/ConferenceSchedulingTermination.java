/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.jboss.qa.brms.performance.examples.conferencescheduling.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.AbstractTermination;

public class ConferenceSchedulingTermination extends AbstractTermination {

    protected long calculateCountLimit;

    public ConferenceSchedulingTermination() {
        this.calculateCountLimit = 10000;
    }

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    @Override
    public boolean isSolverTerminated(SolverScope solverScope) {
        return isTerminated(solverScope.getScoreDirector());
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope phaseScope) {
        return isTerminated(phaseScope.getScoreDirector());
    }

    protected boolean isTerminated(InnerScoreDirector scoreDirector) {
        long calculateCount = scoreDirector.getCalculationCount();
        return calculateCount >= calculateCountLimit;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    @Override
    public double calculateSolverTimeGradient(SolverScope solverScope) {
        return calculateTimeGradient(solverScope.getScoreDirector());
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope phaseScope) {
        return calculateTimeGradient(phaseScope.getScoreDirector());
    }

    protected double calculateTimeGradient(InnerScoreDirector scoreDirector) {
        long calculateCount = scoreDirector.getCalculationCount();
        double timeGradient = ((double) calculateCount) / ((double) calculateCountLimit);
        return Math.min(timeGradient, 1.0);
    }
}
