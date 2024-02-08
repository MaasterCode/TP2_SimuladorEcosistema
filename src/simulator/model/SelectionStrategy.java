package simulator.model;

import java.util.List;

public interface SelectionStrategy {
	
	public Animal select(Animal a, List<Animal> as);
	
}
