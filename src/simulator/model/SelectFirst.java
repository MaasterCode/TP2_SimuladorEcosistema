package simulator.model;

import java.util.List;

public class SelectFirst implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {

		if(as.isEmpty()) return null;
		return as.get(0);
		
/*		for(Animal ani : as) {
			if (ani != a) return ani;
		}	
		return null;*/
	}

}
