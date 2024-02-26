package factories;

import java.util.ArrayList;
import java.util.List;

import simulator.model.Animal;

public class AnimalFactory {
	
	public AnimalFactory() {
		List<Builder<Animal>> animal_builders = new ArrayList<>();
		animal_builders.add(new WolfBuilder<>(new StrategyFactory()));
		animal_builders.add(new SheepBuilder<>(new StrategyFactory()));
		Factory<Animal> animal_strategy_factory = new BuilderBasedFactory<Animal>(animal_builders);
	}

}
