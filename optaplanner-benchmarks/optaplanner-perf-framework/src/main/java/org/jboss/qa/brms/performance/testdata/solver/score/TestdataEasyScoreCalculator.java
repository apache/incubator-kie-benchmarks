package org.jboss.qa.brms.performance.testdata.solver.score;

import org.jboss.qa.brms.performance.testdata.domain.TestdataSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

public class TestdataEasyScoreCalculator implements EasyScoreCalculator<TestdataSolution> {

    @Override
    public Score calculateScore(TestdataSolution testdataSolution) {
        SimpleScore score = SimpleScore.of(0);
        if (!testdataSolution.getEntityList().isEmpty()) {
            score.add(SimpleScore.of(1));
        } else {
            score.add(SimpleScore.of(-1));
        }
        if (!testdataSolution.getValueList().isEmpty()) {
            score.add(SimpleScore.of(1));
        } else {
            score.add(SimpleScore.of(-1));
        }
        return score;
    }
}
