package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal {

	private final static double MAX_SHEEP_AGE = 8.0;
	private final static double ENERGY_DECREASE = 20.0;
	private final static double DESIRE_INCREASE = 40.0;
	private final static double DESIRE_LIMIT = 65.0;
	private final static double MULTIPLIER = 1.2;
	private final static double MOVE_MULTIPLIER = 2.0;
	
	
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
			changeToNormalState();
		}

		if (this._energy == MIN_ENERGY || this._age > MAX_SHEEP_AGE)
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

		this.move(this._speed * dt * Math.exp((this._energy - 100.0) * 0.007));

		this._age += dt;

		double newEnergy = this._energy - ENERGY_DECREASE * dt;
		this._energy = Utils.constrain_value_in_range(newEnergy, MIN_INTERVAL, MAX_INTERVAL);

		double newDesire = this._desire + DESIRE_INCREASE * dt;
		this._desire = Utils.constrain_value_in_range(newDesire, MIN_INTERVAL, MAX_INTERVAL);

	}

	private void normalChangeLogic() {

		if (this._danger_source == null)
			this._danger_source = this._danger_strategy.select(this,
					this._region_mngr.get_animals_in_range(this, animal -> animal.get_diet() == Diet.CARNIVORE));

		if (this._danger_source != null)
			changeToDangerState();

		else if (this._desire > DESIRE_LIMIT)
			changeToMateState();

	}

	private void changeToNormalState() {

		this._state = State.NORMAL;
		this._danger_source = null;
		this._mate_target = null;

	}

	private void dangerLogic(double dt) {

		if (this._danger_source != null && this._danger_source.get_state() == State.DEAD)
			this._danger_source = null;

		if (this._danger_source == null)
			this.normalLogic(dt);

		else {
			this._dest = this._pos.plus(_pos.minus(_danger_source.get_position()).direction());

			this.move(MOVE_MULTIPLIER * this._speed * dt * Math.exp((this._energy - 100.0) * 0.007));

			this._age += dt;

			double newEnergy = this._energy - ENERGY_DECREASE * dt * MULTIPLIER;
			this._energy = Utils.constrain_value_in_range(newEnergy, MIN_INTERVAL, MAX_INTERVAL);

			double newDesire = this._desire + DESIRE_INCREASE * dt;
			this._desire = Utils.constrain_value_in_range(newDesire, MIN_INTERVAL, MAX_INTERVAL);

		}

	}

	private void dangerChangeLogic() {

		if (this._danger_source == null
				|| this._danger_source.get_position().distanceTo(this.get_position()) > this._sight_range) {
			this._danger_source = this._danger_strategy.select(this,
					this._region_mngr.get_animals_in_range(this, animal -> animal.get_diet() == Diet.CARNIVORE));

			if (this._danger_source == null) {

				if (this._desire < 65.0) {
					changeToNormalState();
				}

				else {
					changeToMateState();
				}

			}

		}
	}

	private void changeToDangerState() {

		this._state = State.DANGER;
		this._mate_target = null;

	}

	private void mateLogic(double dt) {

		if (this._mate_target != null && (this._mate_target.get_state() == State.DEAD
				|| this._mate_target.get_position().distanceTo(this.get_position()) > this._sight_range))
			this._mate_target = null;

		if (this._mate_target == null) {
			this._mate_target = this._mate_strategy.select(this,
					this._region_mngr.get_animals_in_range(this, animal -> animal.get_genetic_code() == "Sheep"));

			if (this._mate_target == null)
				this.normalLogic(dt);
		
		}


		if (this._mate_target != null) {

			this._dest = this._mate_target.get_position();

			this.move(MOVE_MULTIPLIER * this._speed * dt * Math.exp((this._energy - 100.0) * 0.007));

			this._age += dt;

			double newEnergy = this._energy - ENERGY_DECREASE * dt * MULTIPLIER;
			this._energy = Utils.constrain_value_in_range(newEnergy, MIN_INTERVAL, MAX_INTERVAL);

			double newDesire = this._desire + DESIRE_INCREASE * dt;
			this._desire = Utils.constrain_value_in_range(newDesire, MIN_INTERVAL, MAX_INTERVAL);
			
			if (this._pos.distanceTo(this._mate_target.get_position()) < MAX_DISTANCE) {
				
				this._desire = 0.0;
				
				this._mate_target.set_desire(0.0);
				
				if (this._baby == null && Utils._rand.nextDouble() < PROBABILITY)
					this._baby = new Sheep(this, this._mate_target);

				newEnergy = this._energy - 10.0;
				this._energy = Utils.constrain_value_in_range(newEnergy, MIN_INTERVAL, MAX_INTERVAL);

				
				this._mate_target = null;

			}
		}

	}

	private void mateChangeLogic() {

		if (this._danger_source == null) {

			this._danger_source = this._danger_strategy.select(this,
					this._region_mngr.get_animals_in_range(this, animal -> animal.get_diet() == Diet.CARNIVORE));
		}

		if (this._danger_source != null)
			changeToDangerState();

		else if (this._desire < 65.0)
			changeToNormalState();

	}

	private void changeToMateState() {

		this._state = State.MATE;
		this._danger_source = null;

	}

	private void changeState(double dt) {

		switch (this._state) {
		case DEAD:
			return;
		case HUNGER:
			break;
		case NORMAL:

			normalLogic(dt);

			normalChangeLogic();

			break;

		case DANGER:

			dangerLogic(dt);

			dangerChangeLogic();

			break;

		case MATE:

			mateLogic(dt);

			mateChangeLogic();

			break;

		default:
			break;

		}

	}

}
