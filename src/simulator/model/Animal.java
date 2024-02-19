package simulator.model;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Alimentation.Diet;
import simulator.model.AnimalState.State;

public abstract class Animal implements Entity, Animalnfo {

	protected String _genetic_code;
	protected Diet _diet;
	protected State _state;
	protected Vector2D _pos;
	protected Vector2D _dest;
	protected double _energy;
	protected double _speed;
	protected double _age;
	protected double _desire;
	protected double _sight_range;
	protected Animal _mate_target;
	protected Animal _baby;
	protected AnimalMapView _region_mngr;
	protected SelectionStrategy _mate_strategy;
	
	
	// Constructores
	protected Animal(String genetic_code, Diet diet, double sight_range,
			double init_speed, SelectionStrategy mate_strategy, Vector2D pos)
			throws IllegalArgumentException{
		
		if (genetic_code != null && genetic_code != "") {
			_genetic_code = genetic_code;
		}
		else
			throw new IllegalArgumentException("genetic code cannot be empty.");
		
		if (sight_range > 0)
			_sight_range = sight_range;
		else
			throw new IllegalArgumentException("sight has to be a positive number higher than 0.");
		
		if (init_speed > 0)
			_speed = Utils.get_randomized_parameter(init_speed, 0.1);
		else
			throw new IllegalArgumentException("speed has to be a positive number higher than 0.");
		
		if (mate_strategy != null)
			_mate_strategy = mate_strategy;
		else
			throw new IllegalArgumentException("mate strategy cannot be null.");
		
		_state = State.NORMAL;
		_energy = 100.0;
		_desire = 0.0;
		_dest = null;
		_mate_target = null;
		_baby = null;
		_region_mngr = null;
		_age = 0;
		
		
	}
	protected Animal(Animal p1, Animal p2) {
		
		_dest = null;
		_baby = null;
		_mate_target = null;
		_region_mngr = null;
		_state = State.NORMAL;
		_desire = 0.0;
		_genetic_code = p1._genetic_code;
		_diet = p1._diet;
		_energy = (p1._energy + p2._energy)/2;
		_pos = p1.get_position().plus(Vector2D.get_random_vector(-1,1).scale(60.0*(Utils._rand
				.nextGaussian()+1)));
		_sight_range = Utils.get_randomized_parameter
				((p1.get_sight_range()+p2.get_sight_range())/2, 0.2);
		_speed = Utils.get_randomized_parameter((p1.get_speed()+p2.get_speed())/2, 0.2);
		
	}
	
	
	// Metodos "get"
	public State get_state() {
		return _state;
	}
	public Vector2D get_position() {
		return _pos;
	}
	public String get_genetic_code() {
		return _genetic_code;
	}
	public Diet get_diet() {
		return _diet;
	}
	public double get_speed() {
		return _speed;
	}
	public double get_sight_range() {
		return _sight_range;
	}
	public double get_energy() {
		return _energy;
	}
	public double get_age() {
		return _age;
	}
	public Vector2D	get_destination() {
		return _dest;
	}
	public boolean is_pregnant() {
		return (_baby != null);
	}
	
	
	//Métodos "Set"
	public void set_genetic_code(String _genetic_code) {
		this._genetic_code = _genetic_code;
	}
	public void set_diet(Diet _diet) {
		this._diet = _diet;
	}
	public void set_state(State _state) {
		this._state = _state;
	}
	public void set_pos(Vector2D _pos) {
		this._pos = _pos;
	}
	public void set_dest(Vector2D _dest) {
		this._dest = _dest;
	}
	public void set_energy(double _energy) {
		this._energy = _energy;
	}
	public void set_speed(double _speed) {
		this._speed = _speed;
	}
	public void set_age(double _age) {
		this._age = _age;
	}
	public void set_desire(double _desire) {
		this._desire = _desire;
	}
	public void set_sight_range(double _sight_range) {
		this._sight_range = _sight_range;
	}
	public void set_mate_target(Animal _mate_target) {
		this._mate_target = _mate_target;
	}
	public void set_baby(Animal _baby) {
		this._baby = _baby;
	}
	public void set_region_mngr(AnimalMapView _region_mngr) {
		this._region_mngr = _region_mngr;
	}
	public void set_mate_strategy(SelectionStrategy _mate_strategy) {
		this._mate_strategy = _mate_strategy;
	}
	
	
	
	void init(AnimalMapView reg_mngr){
		
		_region_mngr = reg_mngr;
		
		double x,y;
		
		if (_pos == null) {
			 x = Utils._rand.nextDouble(0, _region_mngr.get_width());
			 y = Utils._rand.nextDouble(0, _region_mngr.get_height());
			_pos = new Vector2D(x,y);
		}
		else {
			 x = _pos.getX();
			 y = _pos.getY();
			
			while (x >= _region_mngr.get_width()) x = (x - _region_mngr.get_width());
			while (x < 0) x = (x + _region_mngr.get_width());
			while (y >= _region_mngr.get_height()) y = (y - _region_mngr.get_height());
			while (y < 0) y = (y + _region_mngr.get_height());

			_pos = new Vector2D(x,y);
			
		}
		 x = Utils._rand.nextDouble(0, _region_mngr.get_width());
		 y = Utils._rand.nextDouble(0, _region_mngr.get_height());
		_dest = new Vector2D(x,y);
		
		
		
		
		
	}

	Animal deliver_baby() {
		Animal aux = null;
		return aux;
	}
	
	protected void move(double speed) {
		_pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));

	}
	
	protected boolean isOutOfBounders() {
		return this._pos.getX() > this._region_mngr.get_width() || this._pos.getY() > this._region_mngr.get_height() 
				|| this._pos.getX() < 0.0 || this._pos.getY() < 0.0;
	}
	
	public JSONObject as_JSON() {
		JSONObject value = new JSONObject()
				 .put("pos", _pos)
				 .put("gcode", _genetic_code)
			     .put("diet", _diet)
			     .put("state", _state);
		return value;
	}
	
}
