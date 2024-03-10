package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal {

	protected Animal _hunt_target;
	protected SelectionStrategy _hunting_strategy;

	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos) {
		super("Wolf", Diet.CARNIVORE, 50.0, 60.0, mate_strategy, pos);
		this._hunting_strategy = hunting_strategy;
		this._hunt_target = null;
	}

	public Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		this._hunting_strategy = p1.get_hunting_strategy();
		this._hunt_target = null;
	}

	
	public SelectionStrategy get_hunting_strategy() {
		return this._hunting_strategy;
	}
	
	@Override
	public void update(double dt) {

		this.changeState(dt);

		if (this._pos.outOfMap(this._region_mngr.get_width(), this._region_mngr.get_height())) {
			this._pos.adjust(this._region_mngr.get_width(), this._region_mngr.get_height());
			this._state = State.NORMAL;
			this._hunt_target = null;
			this._mate_target = null;
		}

		if (this._energy == 0.0 || this._age > 14.0)
			this._state = State.DEAD;

		if (this._state != State.DEAD) {
			double newEnergy = this._energy + this._region_mngr.get_food(this, dt);

			this._energy = Utils.constrain_value_in_range(newEnergy, 0, 100);

		}

	}

	private void normalState(double dt) {

		if (this._pos.distanceTo(this._dest) < 8.0) {

			double x = Utils._rand.nextDouble(this._region_mngr.get_width());
			double y = Utils._rand.nextDouble(this._region_mngr.get_height());

			this._dest = new Vector2D(x, y);
		}

		this.move(this._speed * dt * Math.exp((_energy - 100.0) * 0.007));

		this._age += dt;

		double newEnergy = this._energy - 18.0 * dt;
		this._energy = Utils.constrain_value_in_range(newEnergy, 0, 100);

		double newDesire = this._desire + 30.0 * dt;
		this._desire = Utils.constrain_value_in_range(newDesire, 0, 100);

	}

	private void hungerState(double dt) {

		if ((this._hunt_target == null) || (this._hunt_target.get_state() == State.DEAD)
				|| (this._pos.distanceTo(this._hunt_target.get_position()) > this._sight_range))
			this._hunt_target = this._hunting_strategy.select(this,
					this._region_mngr.get_animals_in_range(this, animal -> animal.get_diet() == Diet.HERBIVORE));

		if (this._hunt_target == null)
			this.normalState(dt);

		else {

			this._dest = this._hunt_target.get_position();

			this.move(3.0 * _speed * dt * Math.exp((_energy - 100.0) * 0.007));

			this._age += dt;

			double newEnergy = this._energy - 18.0 * 1.2 * dt;
			this._energy = Utils.constrain_value_in_range(newEnergy, 0, 100);

			double newDesire = this._desire + 30.0 * dt;
			this._desire = Utils.constrain_value_in_range(newDesire, 0, 100);

			if (this._pos.distanceTo(this._hunt_target.get_position()) < 8.0) {

				this._hunt_target.set_state(State.DEAD);

				this._hunt_target = null;

				newEnergy = this._energy + 50.0;

				this._energy = Utils.constrain_value_in_range(newEnergy, 0, 100);

			}

		}
	}

	private void mateState(double dt) {

		if (this._mate_target != null && (this._mate_target.get_state() == State.DEAD
				|| this._mate_target.get_position().distanceTo(this._pos) > this._sight_range))
			this._mate_target = null;

		if (this._mate_target == null) {
			this._mate_target = this._mate_strategy.select(this,
					this._region_mngr.get_animals_in_range(this, animal -> animal.get_genetic_code() == "Wolf"));
			
			if (this._mate_target == null)
				this.normalState(dt);
		}

		if (this._mate_target != null) {

			this._dest = this._mate_target.get_position();
			this.move(3.0 * this._speed * dt * Math.exp(this._energy - 100.0) * 0.007);
			this._age += dt;
			double newEnergy = this._energy - 18.0 * 1.2 * dt;
			this._energy = Utils.constrain_value_in_range(newEnergy, 0, 100);
			double newDesire = this._desire + 30.0 * dt;
			this._desire = Utils.constrain_value_in_range(newDesire, 0, 100);
			if (this._pos.distanceTo(this._mate_target.get_position()) < 8.0) {

				this._desire = 0.0;
				this._mate_target.set_desire(0.0);

				if (this._baby == null) {
					if (Utils._rand.nextDouble() < 0.9) 
						this._baby = new Wolf(this, this._mate_target);
				}

				newEnergy = this._energy - 10.0;
				this._energy = Utils.constrain_value_in_range(newEnergy, 0, 100);

				this._mate_target = null;
			}

		}

	}

	private void changeState(double dt) {

		switch (this._state) {

		case DEAD:
			return;

		case NORMAL:

			this.normalState(dt);

			if (this._energy < 50.0) {
				this._state = State.HUNGER;
				this._mate_target = null;
			} else if (this._desire > 65.0) {
				this._state = State.MATE;
				this._hunt_target = null;
			}
			break;

		case HUNGER:

			this.hungerState(dt);

			if (this._energy > 50.0) {
				if (this._desire < 65.0) {
					this._state = State.NORMAL;
					this._mate_target = null;
					this._hunt_target = null;
				}
				else {
					this._state = State.MATE;
					this._hunt_target = null;
				}
			}
			break;

		case MATE:

			this.mateState(dt);

			if (this._energy < 50.0) {
				this._state = State.HUNGER;
				this._mate_target = null;
			}
			else {
				if (this._desire < 65.0) {
					this._state = State.NORMAL;
					this._mate_target = null;
					this._hunt_target = null;
				}
			}
			break;

		case DANGER:
			break;

		}

	}

}
