package simulator.model;

import simulator.misc.Vector2D;
import simulator.model.Alimentation.Diet;

public class Wolf extends Animal{

	protected Animal _hunt_target;
	protected SelectionStrategy _hunting_strategy;
	
	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy,
			Vector2D pos) {
		super("Wolf",Diet.CARNIVORE,50.0,60.0, mate_strategy,pos);
		this._hunting_strategy = hunting_strategy;
		this._hunt_target = null;
	}
	
	public Wolf(Wolf p1, Animal p2) {
		super(p1,p2);
		this._hunting_strategy = p1._hunting_strategy; // Hacer un get de _hunting_strategy
		this._hunt_target = null;
	}

	@Override
	public void update(double dt) {

		switch(this._state) {
		
		case NORMAL:
			break;
		case DEAD:
			break;
		case HUNGER:
			break;
		case MATE:
			break;
		case DANGER:
			break;
		default:
			break;
		
		
		
		
		}
		
		
	}
	
}
