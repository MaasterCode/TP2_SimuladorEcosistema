	package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Alimentation.Diet;
import simulator.model.AnimalState.State;


/* *
 * 
 NOTA:
 
 Predicate es una lambda funcion, utilizamos el tipo que queremos, luego "->"
 y por ultimo la linea que tiene que cumplir, como si fuese el return de una función,
 es decir, es una función en una linea en la que ponemos el tipo que queremos 
 y luego su valor de return.
 * 
 * */


public class Sheep extends Animal{

	private Animal _danger_source;
	private SelectionStrategy _danger_strategy;
	
	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy,
			Vector2D pos) {
		
		super("Sheep",Diet.HERBIVORE,40.0, 35.0, mate_strategy,pos);
		
		_danger_strategy = danger_strategy;
		_danger_source = null;
		
	}
	
	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this._danger_strategy = p1._danger_strategy;
		this._danger_source = null;
	}

	@Override
	public void update(double dt) {
		
	/*1.*/ // Es que si el estado es DEAD no haga nada, se hace ya en changeState(dt);	
		
	/*2.*/	this.changeState(dt);
			
	/*3.*/	if (this.isOutOfBounders()) {
		
				this._pos.adjust(this._region_mngr.get_width(), this._region_mngr.get_height());
				this._state = State.NORMAL;
			
		 	}
		
	/*4.*/	if (this._energy == 0.0 || this._age > 8.0)
				this._state = State.DEAD;
				
	/*5.*/	if (this._state != State.DEAD) {
				double newEnergy = this._energy+ this._region_mngr.get_food(this, dt);
			
				this._energy =Utils.constrain_value_in_range(newEnergy, 0, 100);
				
		}
	}
	
	private void normalState(double dt) {
		if (this._pos.distanceTo(this._dest) < 8.0) {
			int max;
			if (this._region_mngr.get_height() < this._region_mngr.get_width())
				max = this._region_mngr.get_width();
			else
				max = this._region_mngr.get_height();
			this._dest = new Vector2D(Vector2D.get_random_vector(0, max));
			
		}
		this.move(this._speed*dt*Math.exp((this._energy-100.0)*0.007));
		this._age += dt;
		if (this._energy - 20.0*dt >= 0)
			this._energy -= 20.0*dt;
		if (this._desire + 40.0*dt <= 100)
			this._desire += 40.0*dt;
		
		if (this._danger_source == null) {
			this._danger_source = this._danger_strategy.select
					(this, this._region_mngr.get_animals_in_range
							(this, animal -> animal.get_diet() == Diet.CARNIVORE));
		}
	}
	
	private void dangerState(double dt) {
		if (this._danger_source != null && this._state == State.DEAD)
			this._danger_source = null;
		
		if (this._danger_source == null) 
			this.normalState(dt);
		
		
		else if (this._danger_source != null) {
			this._dest = this._pos.plus(_pos.minus
					(_danger_source.get_position()).direction());
			this.move(2.0*this._speed*dt*Math.exp((this._energy-100.0)*0.007));
			this._age += dt;
			if (this._energy - 20.0*1.2*dt >= 0)
				this._energy -= 20.0*1.2*dt;
			if (this._desire + 40.0*dt <= 100)
				this._desire += 40.0*dt;
		}
		
		if (this._danger_source == null) {
			if (this._desire < 65.0)
				this._state = State.NORMAL;
			else
				this._state = State.MATE;
		}
		
		else {
			if (this._danger_source.get_position().distanceTo
					(this.get_position())> this._sight_range) {
				this._danger_source = 
						this._danger_strategy.select(this, 
								this._region_mngr.get_animals_in_range
								(this, animal -> animal.get_diet() == Diet.CARNIVORE));
	
			}
		}
	}
	
	private void mateState(double dt) {
		
	/*1.*/		if (this._mate_target != null) {
				
					if (this._mate_target.get_state() == State.DEAD || 
						this._mate_target.get_position().
						distanceTo(this.get_position())> this._sight_range)
							this._mate_target = null;
				
	/*2.*/		if (this._mate_target == null) {
						this._mate_target = this._mate_strategy.select(this, 
								this._region_mngr.get_animals_in_range
								(this, animal -> animal.get_genetic_code() == "sheep"));
						if (this._mate_target == null)	{
							
							this.normalState(dt);
						}
							
						else {
							
							
			/*2.1*/			this._dest = this._mate_target.get_position();
			/*2.2*/			this.move(2.0*this._speed*dt*Math.exp((this._energy-100.0)*0.007));
			/*2.3*/			this._age += dt;
			/*2.4*/			if (this._energy - 20.0*dt*1.2 >= 0)
								this._energy -= 20.0*dt*1.2;
			/*2.5*/			if (this._desire + 40.0*dt <= 100)
								this._desire += 40.0*dt;
						
			/*2.6*/			if (this._pos.distanceTo(this._mate_target.get_position()) < 8.0) {
				/*2.6.1*/		this._desire = 0.0;
								this._mate_target.set_desire(0.0);	
				/*2.6.2*/		if (this._baby == null) {
									if (Utils._rand.nextDouble() < 0.9) {
										this._baby = new Sheep(this, this._mate_target);
										this._mate_target = null;
									}
								}
						
							}
						}
					
					}
				
			}
					
			if (this._danger_source == null) {
				
				this._danger_source = this._danger_strategy.select
						(this, this._region_mngr.get_animals_in_range
								(this,animal -> animal.get_diet() == Diet.CARNIVORE ));
				
				
			}
	}
	
	private void changeState(double dt) {
		
		switch(this._state) {
		case DEAD:
			break;
		case HUNGER:
			break;
		case NORMAL:
			
			this.normalState(dt);
			
			if (this._danger_source != null){
				this._state = State.DANGER;
			} 
			else if (this._desire >= 65.0) {
				this._state = State.MATE;
			}
			
		case DANGER:
			this.dangerState(dt);	
			
			 if (this._danger_source == null) {
				if (this._desire < 65.0)
					this._state = State.NORMAL;
				else
					this._state = State.MATE;
			}
				
		
		case MATE:
	
			this.mateState(dt);
			
			if (this._danger_source != null) {
				this._state = State.DANGER;
			}
			else {
				if (this._desire < 65.0)
					this._state = State.NORMAL;
			}
			
			break;
		default:
			break;
				
		}
		
	}
	
	
}
