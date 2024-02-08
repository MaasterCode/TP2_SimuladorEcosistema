	package simulator.model;

import java.util.function.Predicate;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Alimentation.Diet;
import simulator.model.AnimalState.State;

public class Sheep extends Animal{

	private Animal _danger_source;
	private SelectionStrategy _danger_strategy;
	
	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy,
			Vector2D pos) {
		
		super("Sheep",Diet.HERVIVORE,40.0, 35.0, mate_strategy,pos);
		
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
		
			
			
		switch(this._state) {
		
		case NORMAL:
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
						(this, this._region_mngr.get_animals_in_range(this, null)); //null de momento
			}
			if (this._danger_source != null){
				this._state = State.DANGER;
			} 
			else if (this._desire >= 65.0) {
				this._state = State.MATE;
			}
			
		case DANGER:
				if (this._danger_source != null && this._state == State.DEAD)
					this._danger_source = null;
				
				if (this._danger_source == null) {
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
					/*	this._danger_source = 
								this._danger_strategy.select(this, 
										this._region_mngr.get_animals_in_range(this,
												this._danger_strategy));
					*/ //ARREGLAR, FALLA
					// Asi es como funciona el Predicate (un ejemplo, luego se borra):
					Predicate<Animal> p = (animal) -> (animal.get_diet() == Diet.CARNIVORE) ;
					}
					else if (this._danger_source == null) {
						if (this._desire < 65.0)
							this._state = State.NORMAL;
						else
							this._state = State.MATE;
					}
				}
		case DEAD:
			break;
		case HUNGER:
			break;
		case MATE:
			
			if (this._mate_target != null) {
				
				if (this._mate_target.get_state() == State.DEAD || 
						this._mate_target.get_position().
						distanceTo(this.get_position())> this._sight_range)
					this._mate_target = null;
				
				else {
					this._dest = this._mate_target.get_position();
					this.move(2.0*this._speed*dt*Math.exp((this._energy-100.0)*0.007));
					this._age += dt;
					if (this._energy - 20.0*dt*1.2 >= 0)
						this._energy -= 20.0*dt*1.2;
					if (this._desire + 40.0*dt <= 100)
						this._desire += 40.0*dt;
					
					if (this._pos.distanceTo(this._mate_target.get_position()) < 8.0) {
						this._desire = 0.0;
						this._mate_target.set_desire(0.0);	
					}
					
					if (this._baby == null) {
						if (Utils._rand.nextDouble() < 0.9) {
							
						}
						
						
					}
					
				}
				
			}
					
			if (this._mate_target == null) {
				//Hacer la búsqueda de pareja (con lo de get_animals_in_range)
				this.move(this._speed*dt*Math.exp((this._energy-100.0)*0.007));
				
				
			}
			
			break;
		default:
			break;
				
		}
			
			
			
			
			
	}
	
	
}
