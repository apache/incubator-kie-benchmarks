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

    private static final String PATH_TO_SOLVE_CONFIG =
            "/org/jboss/qa/brms/performance/examples/conferencescheduling/solver/conferenceSchedulingSolverConfig.xml";

    public enum DataSet {
        TALKS_36_TIMESLOTS_12_ROOMS_5("TIME_12;ROOM_5;SPEAKER_26;TALK_36"),
        TALKS_108_TIMESLOTS_18_ROOMS_10("TIME_18;ROOM_10;SPEAKER_74;TALK_108"),
        TALKS_216_TIMESLOTS_18_ROOMS_20("TIME_18;ROOM_20;SPEAKER_146;TALK_216");

        private int timeslotListSize;
        private int roomListSize;
        private int speakerListSize;
        private int talkListSize;

        DataSet(String generatorParameters) {
            Stream<String> parameters = Arrays.stream(generatorParameters.split(";", 4));

            parameters.forEach(s -> {
                if (s.startsWith("TIME")) {
                    timeslotListSize = Integer.parseInt(s.split("_")[1]);
                } else if (s.startsWith("ROOM")) {
                    roomListSize = Integer.parseInt(s.split("_")[1]);
                } else if (s.startsWith("SPEAKER")) {
                    speakerListSize = Integer.parseInt(s.split("_")[1]);
                } else if (s.startsWith("TALK")) {
                    talkListSize = Integer.parseInt(s.split("_")[1]);
                }
            });

            parameters.close();
        }

        public int getTimeslotListSize() {
            return timeslotListSize;
        }

        public int getRoomListSize() {
            return roomListSize;
        }

        public int getSpeakerListSize() {
            return speakerListSize;
        }

        public int getTalkListSize() {
            return talkListSize;
        }
    }

    public ConferenceSolution loadSolvingProblem(ConferenceScheduling.DataSet dataset) {
        return new ConferenceSchedulingGenerator().createConferenceSolution(dataset.timeslotListSize,
                                                                            dataset.roomListSize,
                                                                            dataset.speakerListSize,
                                                                            dataset.talkListSize);
    }

    @Override
    public ConferenceSolution loadSolvingProblem(File file) {
        throw new UnsupportedOperationException("Unsupported data set loading from file");
    }

    @Override
    public SolverFactory<ConferenceSolution> getDefaultSolverFactory() {
        return SolverFactory.createFromXmlInputStream(this.getClass().getResourceAsStream(PATH_TO_SOLVE_CONFIG));
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
