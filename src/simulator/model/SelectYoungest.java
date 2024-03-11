package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		
		if (as.size() < 1)
			return null;
		
		Animal ret = as.get(0);
		
		for (int i = 0; i < as.size(); i++) {
			if (as.get(i).get_age() < ret.get_age())
				ret = as.get(i);
		}
		
		return ret;
	}

}
