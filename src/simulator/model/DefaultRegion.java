package simulator.model;


public class DefaultRegion extends Region {

	@Override
	public void update(double dt) {
		//Update no hace nada para DefaultRegion
	}

	@Override
	public double get_food(Animal a, double dt) {
		
		double f = 0.0;
		
		//int n = (int) _animal_list.stream().filter(animal -> animal.get_diet() == Diet.HERBIVORE).count();
		
		 if (a.get_diet() == Diet.HERBIVORE) {
			f = 60.0*Math.exp(-Math.max(0,nHerbivores-5.0)*2.0)*dt;
		}
		return f;
	}
	
	
	
}
