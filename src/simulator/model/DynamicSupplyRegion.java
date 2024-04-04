package simulator.model;

import simulator.misc.Utils;
import simulator.model.Alimentation.Diet;

public class DynamicSupplyRegion extends Region {

	
	private double _food;
	private double _growth;
	
	
	public DynamicSupplyRegion(double f, double g) {
		_food = f;
		_growth = g;
	}
	
	@Override
	public void update(double dt) {
		
		if (Utils._rand.nextDouble() < 0.5) 
			_food += dt*_growth;
		
	}

	@Override
	public double get_food(Animal a, double dt) {
		
		double food = 0.0;
		
		if (a.get_diet() == Diet.HERBIVORE) 
			food =  Math.min(_food,60.0*Math.exp(-Math.max(0,nHerbivores-5.0)*2.0)*dt);
		
		_food -= food;
		
		return food;
	}

	public String toString() {
		return "Dynamic region";
	}

}
