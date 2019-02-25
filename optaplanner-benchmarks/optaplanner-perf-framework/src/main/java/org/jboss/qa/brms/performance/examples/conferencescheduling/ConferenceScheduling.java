/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.qa.brms.performance.examples.conferencescheduling;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.conferencescheduling.domain.ConferenceSolution;
import org.jboss.qa.brms.performance.examples.conferencescheduling.domain.Talk;
import org.jboss.qa.brms.performance.examples.conferencescheduling.persistence.ConferenceSchedulingGenerator;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;

public class ConferenceScheduling extends AbstractExample<ConferenceSolution> {

    private static final String PATH_TO_DRL_FILE =
            "/org/jboss/qa/brms/performance/examples/conferencescheduling/solver/conferenceSchedulingScoreRules.drl";

    private static final String PATH_TO_SOLVER_CONFIG =
            "/org/jboss/qa/brms/performance/examples/conferencescheduling/solver/conferenceSchedulingSolverConfig.xml";

    public enum DataSet {
        DAY_2_ROOM_5("DAY_2;ROOM_5"),   //12 time slots, 36 talks, 24 speakers
        DAY_3_ROOM_10("DAY_3;ROOM_10"), //18 time slots, 108 talks, 72 speakers
        DAY_3_ROOM_20("DAY_3;ROOM_20"); //18 time slots, 216 talks, 144 speakers

        private int dayListSize;
        private int roomListSize;

        DataSet(String generatorParameters) {
            Stream<String> parameters = Arrays.stream(generatorParameters.split(";", 2));

            parameters.forEach(s -> {
                if (s.startsWith("DAY")) {
                    dayListSize = Integer.parseInt(s.split("_")[1]);
                } else {
                    roomListSize = Integer.parseInt(s.split("_")[1]);
                }
            });

            parameters.close();
        }

        public int getDayListSize() {
            return dayListSize;
        }

        public int getRoomListSize() {
            return roomListSize;
        }

    }

    public ConferenceSolution loadSolvingProblem(ConferenceScheduling.DataSet dataset) {
        return new ConferenceSchedulingGenerator().buildConferenceSolution(dataset.dayListSize,
                                                                           dataset.roomListSize);
    }

    @Override
    public ConferenceSolution loadSolvingProblem(File file) {
        throw new UnsupportedOperationException("Unsupported data set loading from file");
    }

    @Override
    public SolverFactory<ConferenceSolution> getDefaultSolverFactory() {
        return SolverFactory.createFromXmlInputStream(this.getClass().getResourceAsStream(PATH_TO_SOLVER_CONFIG));
    }

    @Override
    public SolverFactory<ConferenceSolution> getBaseSolverFactory() {
        SolverFactory<ConferenceSolution> solverFactory = SolverFactory.createEmpty();
        SolverConfig solverConfig = solverFactory.getSolverConfig();

        solverConfig.setSolutionClass(ConferenceSolution.class);
        solverConfig.setEntityClassList(Collections.singletonList(Talk.class));
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        solverConfig.getScoreDirectorFactoryConfig().setScoreDrlList(Collections.singletonList(PATH_TO_DRL_FILE));
        solverConfig.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");

        return solverFactory;
    }
}
