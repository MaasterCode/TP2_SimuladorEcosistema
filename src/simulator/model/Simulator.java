package simulator.model;

import java.util.List;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;

import simulator.factories.Factory;

public class Simulator {
	
	private RegionManager reg_manager;
	private List<Animal> animal_list;
	private double current_time;
	private Factory<Animal> animals_factory;
	private Factory<Region> regions_factory;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory, Factory<Region> regions_factory) {
		this.reg_manager = new RegionManager(cols, rows, width, height);
		this.current_time = 0.0;
		animal_list = new ArrayList<>();
		this.animals_factory = animals_factory;
		this.regions_factory = regions_factory;
	}
	
	// CAMBIAR A PRIVATE UNA VEZ AÑADIDAS LAS FACTORIAS
	private void set_region(int row, int col, Region r) {
		reg_manager.set_region(row, col, r);
	}
	// CAMBIAR A "void set_region(int row, int col, JSONObject r_json)" UNA VEZ AÑADIDAS LAS FACTORIAS
	public void set_region(int row, int col, JSONObject r_json) {
		Region r = this.regions_factory.create_instance(r_json);
		this.set_region(row, col, r);
	}
	
	public void add_animal(Animal a) {
		
		
			animal_list.add(a);
			this.reg_manager.register_animal(a);
		
	}
	
	public void add_animal(JSONObject a_json) {
		Animal a = animals_factory.create_instance(a_json);
		this.add_animal(a);
	}
	
	public MapInfo get_map_info() {
		return reg_manager;
	}
	
	public List<? extends AnimalInfo> get_animals() {
		return Collections.unmodifiableList(animal_list);
	}
	
	public double get_time() {
		return this.current_time;
	}
	
	public void advance(double dt) {
		this.current_time += dt;
		List<Animal> deadAnimals = new ArrayList<>();
		
		for (Animal a : animal_list)
			if (a.get_state().equals(State.DEAD)) {
				deadAnimals.add(a);
				this.reg_manager.unregister_animal(a);
			}

		this.animal_list.removeAll(deadAnimals);

		
		for(Animal a: this.animal_list) {
			a.update(dt);
			
			this.reg_manager.update_animal_region(a);
		}
		this.reg_manager.update_all_regions(dt);
		
		int num_animals = animal_list.size();
		for(int i = 0; i < num_animals; i++) {
			if(animal_list.get(i).is_pregnant()) {
				add_animal(animal_list.get(i).deliver_baby());
			}
		}
		
	}
	
	public JSONObject as_JSON() {
		JSONObject json_return = new JSONObject();
		json_return.put("time",this.current_time);
		json_return.put("state", this.reg_manager.as_JSON());
		return json_return;
	}

}
