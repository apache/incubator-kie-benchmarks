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
import org.jboss.qa.brms.performance.examples.conferencescheduling.generator.ConferenceSchedulingGenerator;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Talk;


public final class ConferenceSchedulingExample extends AbstractExample<ConferenceSolution> {

    public static final String DRL_FILE =
            "org/optaplanner/examples/conferencescheduling/solver/conferenceSchedulingScoreRules.drl";

    private static final String SOLVER_CONFIG =
            "/org/jboss/qa/brms/performance/examples/conferencescheduling/solver/conferenceSchedulingSolverConfig.xml";

    public ConferenceSolution loadSolvingProblem(ConferenceSchedulingExample.DataSet dataset) {
        File exampleDataDir = new File(System.getProperty("user.dir"), "target");
        exampleDataDir.mkdirs();
        System.setProperty(CommonApp.DATA_DIR_SYSTEM_PROPERTY, exampleDataDir.getAbsolutePath());
        // TODO: refactor the ConferenceSchedulingGenerator in optaplanner-examples to reuse it
        return new ConferenceSchedulingGenerator().createConferenceSolution(
                dataset.timeslotListSize,
                dataset.roomListSize,
                dataset.speakerListSize,
                dataset.talkListSize);
    }

    @Override
    public ConferenceSolution loadSolvingProblem(File file) {
        throw new UnsupportedOperationException("Unsupported data set loading from file");
    }

    @Override
    public SolverConfig getBaseSolverConfig() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setSolutionClass(ConferenceSolution.class);
        solverConfig.setEntityClassList(Collections.singletonList(Talk.class));
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        solverConfig.getScoreDirectorFactoryConfig().setScoreDrlList(Collections.singletonList(DRL_FILE));
        solverConfig.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");

        return solverConfig;
    }

    @Override
    protected String getSolverConfigResource() {
        return SOLVER_CONFIG;
    }

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
    }
}
