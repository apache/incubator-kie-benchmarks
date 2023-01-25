package org.jboss.qa.brms.performance.configuration;

import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.TailChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.SubListChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.SubListSwapMoveSelectorConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoveSelectorConfigurations {

    public static List<MoveSelectorConfig> createChangeMoveSelectorList() {
        return Collections.<MoveSelectorConfig>singletonList(new ChangeMoveSelectorConfig());
    }

    public static List<MoveSelectorConfig> createSwapMoveSelectorList() {
        return Collections.<MoveSelectorConfig>singletonList(new SwapMoveSelectorConfig());
    }

    public static List<MoveSelectorConfig> createPillarChangeMoveSelectorList() {
        return Collections.<MoveSelectorConfig>singletonList(new PillarChangeMoveSelectorConfig());
    }

    public static List<MoveSelectorConfig> createPillarSwapMoveSelectorList() {
        return Collections.<MoveSelectorConfig>singletonList(new PillarSwapMoveSelectorConfig());
    }

    public static List<MoveSelectorConfig> createSubChainChangeMoveSelectorList() {
        return Collections.<MoveSelectorConfig>singletonList(new SubChainChangeMoveSelectorConfig());
    }

    public static List<MoveSelectorConfig> createMultiPilarSelectorList() {
        return Stream.concat(MoveSelectorConfigurations.createPillarSwapMoveSelectorList().stream(),
                MoveSelectorConfigurations.createPillarChangeMoveSelectorList().stream()).collect(Collectors.toList());
    }

    public static List<MoveSelectorConfig> createSubChainSwapMoveSelectorList() {
        return Collections.<MoveSelectorConfig>singletonList(new SubChainSwapMoveSelectorConfig());
    }

    public static List<MoveSelectorConfig> createTailChainSwapMoveSelectorList() {
        return Collections.<MoveSelectorConfig>singletonList(new TailChainSwapMoveSelectorConfig());
    }

    public static List<MoveSelectorConfig> createAllChainedSelectorList() {
        List<MoveSelectorConfig> selectors = new ArrayList<MoveSelectorConfig>();
        selectors.add(new ChangeMoveSelectorConfig());
        selectors.add(new SwapMoveSelectorConfig());
        selectors.add(new SubChainChangeMoveSelectorConfig());
        selectors.add(new SubChainSwapMoveSelectorConfig());
        selectors.add(new TailChainSwapMoveSelectorConfig());
        return selectors;
    }

    public static List<MoveSelectorConfig> createAllNonChainedSelectorList() {
        List<MoveSelectorConfig> selectors = new ArrayList<MoveSelectorConfig>();
        selectors.add(new ChangeMoveSelectorConfig());
        selectors.add(new SwapMoveSelectorConfig());
        selectors.add(new PillarChangeMoveSelectorConfig());
        selectors.add(new PillarSwapMoveSelectorConfig());
        return selectors;
    }

    public static List<MoveSelectorConfig> createSubListChangeMoveSelectorList() {
        return Collections.singletonList(new SubListChangeMoveSelectorConfig());
    }

    public static List<MoveSelectorConfig> createSubListSwapMoveSelectorList() {
        return Collections.singletonList(new SubListSwapMoveSelectorConfig());
    }

    public static List<MoveSelectorConfig> createAllListSelectorList() {
        List<MoveSelectorConfig> selectors = new ArrayList<MoveSelectorConfig>();
        selectors.add(new ChangeMoveSelectorConfig());
        selectors.add(new SwapMoveSelectorConfig());
        selectors.add(new SubListChangeMoveSelectorConfig());
        selectors.add(new SubListSwapMoveSelectorConfig());
        // TODO add ListTailSwapMoveSelectorConfig when it is implemented.
        return selectors;
    }
}
