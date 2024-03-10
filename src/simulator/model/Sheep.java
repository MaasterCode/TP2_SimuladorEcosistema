package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

/* *
 * 
 NOTA:
 
 Predicate es una lambda funcion, utilizamos el tipo que queremos, luego "->"
 y por ultimo la linea que tiene que cumplir, como si fuese el return de una función,
 es decir, es una función en una linea en la que ponemos el tipo que queremos 
 y luego su valor de return.
 * 
 * */

public class Sheep extends Animal {

	private Animal _danger_source;
	private SelectionStrategy _danger_strategy;

	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {

		super("Sheep", Diet.HERBIVORE, 40.0, 35.0, mate_strategy, pos);

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

		if (this._state.equals(State.DEAD))
			return;

		this.changeState(dt);

		if (this._pos.outOfMap(this._region_mngr.get_width(), this._region_mngr.get_height())) {

			this._pos.adjust(this._region_mngr.get_width(), this._region_mngr.get_height());
			this._state = State.NORMAL;
			this._danger_source = null;
			this._mate_target = null;
		}

		if (this._energy == 0.0 || this._age > 8.0)
			this._state = State.DEAD;

		if (this._state != State.DEAD) {
			double newEnergy = this._energy + this._region_mngr.get_food(this, dt);

			this._energy = Utils.constrain_value_in_range(newEnergy, 0, 100);

		}
	}

	private void normal_logic(double dt) {

		if (this._pos.distanceTo(this._dest) < 8.0) {

			double x = Utils._rand.nextDouble(this._region_mngr.get_width());
			double y = Utils._rand.nextDouble(this._region_mngr.get_height());

			this._dest = new Vector2D(x, y);
		}

		this.move(this._speed * dt * Math.exp((this._energy - 100.0) * 0.007));

		this._age += dt;

		double newEnergy = this._energy - 20.0 * dt;
		this._energy = Utils.constrain_value_in_range(newEnergy, 0, 100);

		double newDesire = this._desire + 40.0 * dt;
		this._desire = Utils.constrain_value_in_range(newDesire, 0, 100);

	}

	private void danger_logic(double dt) {

		if (this._danger_source != null && this._danger_source.get_state() == State.DEAD)
			this._danger_source = null;

		if (this._danger_source == null)
			this.normal_logic(dt);

		else {
			this._dest = this._pos.plus(_pos.minus(_danger_source.get_position()).direction());

			this.move(2.0 * this._speed * dt * Math.exp((this._energy - 100.0) * 0.007));

			this._age += dt;

			double newEnergy = this._energy - 20.0 * dt * 1.2;
			this._energy = Utils.constrain_value_in_range(newEnergy, 0, 100);

			double newDesire = this._desire + 40.0 * dt;
			this._desire = Utils.constrain_value_in_range(newDesire, 0, 100);

		}

		if (this._danger_source == null
				|| this._danger_source.get_position().distanceTo(this.get_position()) > this._sight_range) {
			this._danger_source = this._danger_strategy.select(this,
					this._region_mngr.get_animals_in_range(this, animal -> animal.get_diet() == Diet.CARNIVORE));

			if (this._danger_source == null) {

				if (this._desire < 65.0) {
					this._state = State.NORMAL;
					this._danger_source = null;
					this._mate_target = null;
				}

				else {
					this._state = State.MATE;
					this._danger_source = null;
				}

			}

		}

	}

	private void mate_logic(double dt) {

		if (this._mate_target != null) {

			if (this._mate_target.get_state() == State.DEAD
					|| this._mate_target.get_position().distanceTo(this.get_position()) > this._sight_range)
				this._mate_target = null;
		}
		if (this._mate_target == null) {
			this._mate_target = this._mate_strategy.select(this,
					this._region_mngr.get_animals_in_range(this, animal -> animal.get_genetic_code() == "Sheep"));
			if (this._mate_target == null) {

				this.normal_logic(dt);
			}
		}
		
		if (_mate_target != null){

			this._dest = this._mate_target.get_position();
			this.move(2.0 * this._speed * dt * Math.exp((this._energy - 100.0) * 0.007));
			this._age += dt;
			double newEnergy = this._energy - 20.0 * dt * 1.2;
			this._energy = Utils.constrain_value_in_range(newEnergy, 0, 100);

			double newDesire = this._desire + 40.0 * dt;
			this._desire = Utils.constrain_value_in_range(newDesire, 0, 100);

			if (this._pos.distanceTo(this._mate_target.get_position()) < 8.0) {
				this._desire = 0.0;
				this._mate_target.set_desire(0.0);
				if (this._baby == null) {
					if (Utils._rand.nextDouble() < 0.9) 
						this._baby = new Sheep(this, this._mate_target);
						
					
				}
				this._mate_target = null;

			}
		}

		if (this._danger_source == null) {

			this._danger_source = this._danger_strategy.select(this,
					this._region_mngr.get_animals_in_range(this, animal -> animal.get_diet() == Diet.CARNIVORE));
		}

		if (this._danger_source != null) {
			this._state = State.DANGER;
			this._mate_target = null;
		}

		else if (this._desire < 65.0)
			this._state = State.NORMAL;
		
	}

	private void changeState(double dt) {

		switch (this._state) {
		case DEAD:
			return;
		case HUNGER:
			break;
		case NORMAL:

			this.normal_logic(dt);
			if (this._danger_source == null) {
				this._danger_source = this._danger_strategy.select(this,
						this._region_mngr.get_animals_in_range(this, animal -> animal.get_diet() == Diet.CARNIVORE));
			}
			if (this._danger_source != null) {
				this._state = State.DANGER;
				this._mate_target = null;
			} else if (this._desire > 65.0) {
				this._state = State.MATE;
				this._danger_source = null;
			}
			break;
		case DANGER:
			this.danger_logic(dt);
			break;
		case MATE:
			this.mate_logic(dt);
			break;
		default:
			break;

		}

	}

}
