package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Alimentation.Diet;
import simulator.model.AnimalState.State;

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

		/*1.*/ // Si el estado es DEAD, no hace nada, de eso ya se encarga el método "changeState(dt)".	
		
		/*2.*/	this.changeState(dt);	
		
		/*3.*/ if (this._pos.outOfBound(this._region_mngr.get_width(), this._region_mngr.get_height())) 
					this._pos.adjust(this._region_mngr.get_width(), this._region_mngr.get_height());
		
		/*4.*/ if (this._energy == 0.0 || this._age > 14.0)
					this._state = State.DEAD;
		
		/*5.*/ if (this._state != State.DEAD) {
					double newEnergy = this._energy+ this._region_mngr.get_food(this, dt);
			
					this._energy =Utils.constrain_value_in_range(newEnergy, 0, 100);
				
		}
		
	}
	
	private void normalState(double dt) {
		
		
		/*2.1.1*/	if(this._pos.distanceTo(this._dest) < 8.0) {
						int max;
						if (this._region_mngr.get_height() < this._region_mngr.get_width())
							max = this._region_mngr.get_width();
						else
							max = this._region_mngr.get_height();
						this._dest = new Vector2D(Vector2D.get_random_vector(0, max));
					}
					
		/*2.1.2*/	this.move(this._speed*dt*Math.exp((_energy-100.0)*0.007));
		
		/*2.1.3*/	this._age += dt;
					
		/*2.1.4*/	double newEnergy = this._energy- 18.0*dt;
					this._energy =Utils.constrain_value_in_range(newEnergy, 0, 100);
					
		/*2.1.5*/	double newDesire = this._desire + 30.0*dt;
					this._desire = Utils.constrain_value_in_range(newDesire, 0, 100);
					
					
			
			
			
		}
	
	private void hungerState(double dt) {
		
		/*2.1*/	if ((this._hunt_target == null) ||(this._hunt_target.get_state() == State.DEAD) 
					|| (this._pos.distanceTo(this._hunt_target._pos) > this._sight_range))
					this._hunt_target = this._hunting_strategy.select
						(this, this._region_mngr.get_animals_in_range
							(this, animal -> animal.get_diet() == Diet.HERBIVORE));
		
	/*2.2*/ if (this._hunt_target == null) 
				this.normalState(dt);
			
			else {
				
		/*2.2.1*/ this._dest = this._hunt_target.get_position();
		
		/*2.2.2*/ this.move( 3.0*_speed*dt*Math.exp((_energy-100.0)*0.007));
		
		/*2.2.3*/ this._age += dt;
		
		/*2.2.4*/ double newEnergy = this._energy - 18.0*1.2*dt;
				  this._energy = Utils.constrain_value_in_range(newEnergy, 0, 100);
				  
		/*2.2.5*/ double newDesire = this._desire + 30.0*dt;
				  this._desire = Utils.constrain_value_in_range(newDesire, 0, 100);
				  
		/*2.2.6*/ if (this._pos.distanceTo(this._hunt_target.get_position()) < 8.0) {
			
			/*2.2.6.1*/	this._hunt_target.set_state(State.DEAD);
						
			/*2.2.6.2*/	this._hunt_target = null;
						
			/*2.2.6.3*/	newEnergy = this._energy + 50.0;
						
						this._energy = Utils.constrain_value_in_range(newEnergy, 0, 100);
						
				  }
		
	
			}
	}
	
	private void mateState(double dt) {
		
		
		/*1.*/	if (this._mate_target != null && (this._mate_target.get_state() == State.DEAD
				|| this._mate_target.get_position().distanceTo(this._pos) > this._sight_range))
				this._mate_target = null;
			
		/*2.*/	if (this._mate_target == null) {
					this._mate_target = this._mate_strategy.select(this, 
							this._region_mngr.get_animals_in_range
							(this, animal -> animal.get_genetic_code() == "wolf"));
					
					if (this._mate_target == null) 
						this.normalState(dt);
					
					else {
						
			/*2.1*/		this._dest = this._mate_target.get_position();
			/*2.2*/		this.move(3.0*this._speed*dt*Math.exp(this._energy-100.0)*0.007);
			/*2.3*/		this._age += dt;
			/*2.4*/		double newEnergy = this._energy - 18.0*1.2*dt;
						this._energy =Utils.constrain_value_in_range(newEnergy, 0, 100);
			/*2.5*/		double newDesire = this._desire + 30.0*dt;
						this._energy =Utils.constrain_value_in_range(newDesire, 0, 100);
			/*2.6*/		if (this._pos.distanceTo(this._dest) < 8.0) {
					
				/*2.6.1*/	this._desire = 0.0;
							this._mate_target.set_desire(0.0);
				
				/*2.6.2*/	if (this._baby == null) {
								if (Utils._rand.nextDouble() < 0.9) {
									this._baby = new Wolf(this, this._mate_target);
									this._mate_target = null;		
						
								}
							}
				
				/*2.6.3*/	newEnergy = this._energy - 10.0;
							this._energy =Utils.constrain_value_in_range(newEnergy, 0, 100);
				
				/*2.6.4*/	this._mate_target = null;			
						}
					
					}
					
					
				}
		
	}
	
	private void changeState(double dt) {
		
		/*2.*/ switch(this._state) {
			
					case DEAD:
						break;
						
						
			/*2.1*/	case NORMAL:
			
		   /*1. - 1.5*/ this.normalState(dt);	
				
			/*2.*/		if (this._energy < 50.0)
							this._state = State.HUNGER;
						else if (this._desire >= 65.0)
							this._state = State.MATE;
						break;
				
						
			/*2.2*/	case HUNGER:
						
					/*1. - 2.*/	this.hungerState(dt);
						
					/*3.*/ 		if (this._energy > 50.0) {
									if (this._desire < 65.0)
										this._state = State.NORMAL;
									else
										this._state = State.MATE;
								}
						break;
						
						
			/*2.3*/	case MATE:
						
					/*1. - 2.*/	this.mateState(dt);
					
					/*3.*/		if (this._energy < 50.0)
									this._state = State.HUNGER;
								else {
									if (this._desire < 65.0)
										this._state = State.NORMAL;
								}	
						break;
						
						
			/*2.4*/	case DANGER: //Nunca va a estar en este estado
						break;
					
					
			}
				
		
		
	}
	
	
}
