package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Alimentation.Diet;

public class Wolf extends Animal {

	private static final double MAX_WOLF_AGE = 14.0;
	private final static double ENERGY_DECREASE = 18.0;
	private final static double DESIRE_INCREASE = 30.0;
	private final static double DESIRE_LIMIT = 65.0;
	private final static double ENERGY_LIMIT = 50.0;
	private final static double MULTIPLIER = 1.2;
	private final static double MOVE_MULTIPLIER = 3.0;
	private final static double MATE_ENERGY_LOSS = 10.0;

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
		
		if (this._state == State.DEAD) return;

		this.changeState(dt);

		if (this._pos.outOfMap(this._region_mngr.get_width(), this._region_mngr.get_height())) {

			this._pos.adjust(this._region_mngr.get_width(), this._region_mngr.get_height());

			changeToNormalState();
		}

		if (this._energy == MIN_ENERGY || this._age > MAX_WOLF_AGE)
			this._state = State.DEAD;

		if (this._state != State.DEAD) {

			double newEnergy = this._energy + this._region_mngr.get_food(this, dt);

			this._energy = Utils.constrain_value_in_range(newEnergy, MIN_INTERVAL, MAX_INTERVAL);

		}

	}

	private void normalLogic(double dt) {

		if (this._pos.distanceTo(this._dest) < MAX_DISTANCE) {

			double x = Utils._rand.nextDouble(this._region_mngr.get_width());
			double y = Utils._rand.nextDouble(this._region_mngr.get_height());

			this._dest = new Vector2D(x, y);
		}

		this.move(this._speed * dt * Math.exp((_energy - 100.0) * 0.007));

		this._age += dt;

		double newEnergy = this._energy - ENERGY_DECREASE * dt;
		this._energy = Utils.constrain_value_in_range(newEnergy, MIN_INTERVAL, MAX_INTERVAL);

		double newDesire = this._desire + DESIRE_INCREASE * dt;
		this._desire = Utils.constrain_value_in_range(newDesire, MIN_INTERVAL, MAX_INTERVAL);

	}

	private void normalChangeLogic() {

		if (this._energy < ENERGY_LIMIT) {
			changeToHungerState();
		} else if (this._desire > DESIRE_LIMIT) {
			changeToMateState();
		}
	}

	private void changeToNormalState() {

		this._state = State.NORMAL;
		this._mate_target = null;
		this._hunt_target = null;

	}

	private void hungerState(double dt) {

		if ((this._hunt_target == null) || (this._hunt_target.get_state() == State.DEAD)
				|| (this._pos.distanceTo(this._hunt_target.get_position()) > this._sight_range))
			this._hunt_target = this._hunting_strategy.select(this,
					this._region_mngr.get_animals_in_range(this, animal -> animal.get_diet().equals(Diet.HERBIVORE)));

		if (this._hunt_target == null)
			this.normalLogic(dt);

		else {

			this._dest = this._hunt_target.get_position();

			this.move(MOVE_MULTIPLIER * _speed * dt * Math.exp((_energy - 100.0) * 0.007));

			this._age += dt;

			double newEnergy = this._energy - (ENERGY_DECREASE * MULTIPLIER * dt);
			this._energy = Utils.constrain_value_in_range(newEnergy, MIN_INTERVAL, MAX_INTERVAL);

			double newDesire = this._desire + DESIRE_INCREASE * dt;
			this._desire = Utils.constrain_value_in_range(newDesire, MIN_INTERVAL, MAX_INTERVAL);

			if (this._pos.distanceTo(this._hunt_target.get_position()) < MAX_DISTANCE) {

				this._hunt_target.set_state(State.DEAD);

				this._hunt_target = null;

				newEnergy = this._energy + ENERGY_LIMIT;

				this._energy = Utils.constrain_value_in_range(newEnergy, MIN_INTERVAL, MAX_INTERVAL);

			}

		}
	}

	private void hungerChangeLogic() {

		if (this._energy > ENERGY_LIMIT) {
			if (this._desire < DESIRE_LIMIT) {
				changeToNormalState();
			} else {
				changeToMateState();
			}
		}

	}

	private void changeToHungerState() {

		this._state = State.HUNGER;
		this._mate_target = null;

	}

	private void mateState(double dt) {

		if (this._mate_target != null && (this._mate_target.get_state() == State.DEAD || this._mate_target.get_position().distanceTo(this._pos) > this._sight_range)) {
			this._mate_target = null;
		}
			
		if (this._mate_target == null) {
			this._mate_target = this._mate_strategy.select(this, this._region_mngr.get_animals_in_range(this, animal -> animal.get_genetic_code().equalsIgnoreCase("Wolf")));
	
		} 
		
		if (this._mate_target == null) {
			this.normalLogic(dt);
		} else if (this._mate_target != null) {

			this._dest = this._mate_target.get_position();

			this.move(MOVE_MULTIPLIER * this._speed * dt * Math.exp((this._energy - 100.0) * 0.007));

			this._age += dt;

			double newEnergy = this._energy - (ENERGY_DECREASE * MULTIPLIER * dt);
			this._energy = Utils.constrain_value_in_range(newEnergy, MIN_INTERVAL, MAX_INTERVAL);

			double newDesire = this._desire + DESIRE_INCREASE * dt;
			this._desire = Utils.constrain_value_in_range(newDesire, MIN_INTERVAL, MAX_INTERVAL);

			if (this._pos.distanceTo(this._mate_target.get_position()) < MAX_DISTANCE) {

				this._desire = 0.0;
				this._mate_target.set_desire(0.0);

				if (this._baby == null && Utils._rand.nextDouble() < PROBABILITY) {
					
					this._baby = new Wolf(this, this._mate_target);
				}
				newEnergy = this._energy - MATE_ENERGY_LOSS;
				this._energy = Utils.constrain_value_in_range(newEnergy, MIN_ENERGY, MAX_INTERVAL);
				this._mate_target = null;
			}

		}

	}

	private void mateChangeLogic() {

		if (this._energy < ENERGY_LIMIT) {
			changeToHungerState();
		} else if (this._desire < DESIRE_LIMIT) {
			changeToNormalState();
		}
	}

	private void changeToMateState() {

		this._state = State.MATE;
		this._hunt_target = null;

	}

	private void changeState(double dt) {

		switch (this._state) {

		case DEAD:
			return;

		case NORMAL:

			normalLogic(dt);

			normalChangeLogic();

			break;

		case HUNGER:

			hungerState(dt);

			hungerChangeLogic();

			break;

		case MATE:

			mateState(dt);

			mateChangeLogic();

			break;

		case DANGER:
			break;

		}

	}

}
