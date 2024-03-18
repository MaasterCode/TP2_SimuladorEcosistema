package simulator.model;

import simulator.model.Alimentation.Diet;

public class DefaultRegion extends Region {

	@Override
	public void update(double dt) {
		
	}

	@Override
	public double get_food(Animal a, double dt) {
		
		double f = 0.0;
		
		 if (a.get_diet() == Diet.HERBIVORE) 
			f = 60.0*Math.exp(-Math.max(0,nHerbivores-5.0)*2.0)*dt;	
		 
		 return f;
	}
	
	
	
}
