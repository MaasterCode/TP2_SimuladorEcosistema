package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		
		if (as.size() < 1)
			return null;
		
		Animal ret = as.get(0);
		
		for (int i = 1; i < as.size(); i++) {
			if (a.get_position().distanceTo(as.get(i).get_position()) 
					< a.get_position().distanceTo(ret.get_position())) {
				ret = as.get(i);
			}
		}
		
		return ret;
	}

}
