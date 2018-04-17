package org.jboss.qa.brms.performance.construction;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;

public abstract class AbstractConstructionHeuristicPlannerBenchmark extends AbstractPlannerBenchmark {

    public abstract ConstructionHeuristicType getConstructionHeuristicType();

}
