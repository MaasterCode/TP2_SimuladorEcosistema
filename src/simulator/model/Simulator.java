package simulator.model;

import java.util.List;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;

import simulator.factories.Factory;

public class Simulator implements Observable<EcoSysObserver> {

	private RegionManager reg_manager;
	private List<Animal> _animal_list;
	private List<EcoSysObserver> observer_list;
	private double current_time;
	private Factory<Animal> animals_factory;
	private Factory<Region> regions_factory;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory,
			Factory<Region> regions_factory) {
		this.reg_manager = new RegionManager(cols, rows, width, height);
		this.current_time = 0.0;
		_animal_list = new ArrayList<>();
		this.animals_factory = animals_factory;
		this.regions_factory = regions_factory;
	}

	private void set_region(int row, int col, Region r) {
		reg_manager.set_region(row, col, r);
		for (EcoSysObserver ob : observer_list) {
			ob.onRegionSet(row, col, reg_manager, r);
		}
	}

	public void set_region(int row, int col, JSONObject r_json) {
		Region r = this.regions_factory.create_instance(r_json);
		this.set_region(row, col, r);
	}

	public void add_animal(Animal a) {

		_animal_list.add(a);
		this.reg_manager.register_animal(a);
		for (EcoSysObserver ob : observer_list) {
			ob.onAnimalAdded(current_time, reg_manager, new ArrayList<>(_animal_list), a);
		}
		
	}

	public void add_animal(JSONObject a_json) {
		Animal a = animals_factory.create_instance(a_json);
		this.add_animal(a);
	}

	public MapInfo get_map_info() {
		return reg_manager;
	}

	public List<? extends AnimalInfo> get_animals() {
		return Collections.unmodifiableList(_animal_list);
	}

	public double get_time() {
		return this.current_time;
	}

	public void advance(double dt) {
		this.current_time += dt;
		List<Animal> deadAnimals = new ArrayList<>();

		for (Animal a : _animal_list)
			if (a.get_state().equals(State.DEAD)) {
				deadAnimals.add(a);
				this.reg_manager.unregister_animal(a);
			}

		this._animal_list.removeAll(deadAnimals);

		for (Animal a : this._animal_list) {
			a.update(dt);

			this.reg_manager.update_animal_region(a);
		}
		this.reg_manager.update_all_regions(dt);

		int num_animals = _animal_list.size();
		for (int i = 0; i < num_animals; i++) {
			if (_animal_list.get(i).is_pregnant()) {
				add_animal(_animal_list.get(i).deliver_baby());
			}
		}
		for (EcoSysObserver ob : observer_list) {
			ob.onAvanced( current_time, reg_manager, new ArrayList<>(_animal_list), dt);
		}

	}

	public JSONObject as_JSON() {
		JSONObject json_return = new JSONObject();
		json_return.put("time", this.current_time);
		json_return.put("state", this.reg_manager.as_JSON());
		return json_return;
	}

	public void reset(int cols, int rows, int width, int height) {
		this._animal_list.clear();
		this.reg_manager = new RegionManager(this.reg_manager._cols, this.reg_manager._rows, this.reg_manager._width,
				this.reg_manager._height);
		this.current_time = 0;
		for (EcoSysObserver ob : observer_list) {
			ob.onReset(height, reg_manager, new ArrayList<>(_animal_list));
		}
	}

	@Override
	public void addObserver(EcoSysObserver o) {
		observer_list.add(o);
		o.onRegister(current_time, reg_manager, new ArrayList<>(_animal_list));
	}

	@Override
	public void removeObserver(EcoSysObserver o) {
		observer_list.remove(o);

	}

}
