package simulator.factories;

import java.util.ArrayList;
import java.util.List;

import simulator.model.SelectionStrategy;

public class StrategyFactory {
	
	public StrategyFactory() {
		// initialize the strategies factory
		List<Builder<SelectionStrategy>> selection_strategy_builders = new ArrayList<>();
		selection_strategy_builders.add(new SelectFirstBuilder<>());
		selection_strategy_builders.add(new SelectClosestBuilder<>());
		selection_strategy_builders.add(new SelectYoungestBuilder<>());
		Factory<SelectionStrategy> selection_strategy_factory = new BuilderBasedFactory<SelectionStrategy>(selection_strategy_builders);
	}

}
