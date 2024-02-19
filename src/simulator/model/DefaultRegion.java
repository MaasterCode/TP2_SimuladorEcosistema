package simulator.model;

import simulator.model.Alimentation.Diet;

public class DefaultRegion extends Region {

	@Override
	public void update(double dt) {
		//Update no hace nada para DefaultRegion
	}

	@Override
	public double get_food(Animal a, double dt) {
		if (a.get_diet() == Diet.CARNIVORE)
			return 0.0;
		else if (a.get_diet() == Diet.HERBIVORE) {
			return 60.0*Math.exp(-Math.max(0,nHerbivores()-5.0)*2.0)*dt;
		}
		return 0;
	}
	// HerbivoreCount devuelve el numero de animales herbivoros en la lista, ya que 
	// la función necesita el numero n de animales herbivoros:
	// 60.0*Math.exp(-Math.max(0,n-5.0)*2.0)*dt
	
	
	
}
