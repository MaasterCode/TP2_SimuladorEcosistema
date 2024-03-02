package simulator.model;

import java.util.List;
import org.json.JSONObject;

import simulator.factories.Factory;

public class Simulator {
	
	private RegionManager reg_manager;
	private List<Animal> animal_list;
	private double current_time;
	// Falta añadir las factorias
	
	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory, Factory<Region> regions_factory) {
		this.reg_manager = new RegionManager(cols, rows, width, height);
		this.current_time = 0.0;
	}
	
	// CAMBIAR A PRIVATE UNA VEZ AÑADIDAS LAS FACTORIAS
	public void set_region(int row, int col, Region r) {
		reg_manager.set_region(row, col, r);
	}
	// CAMBIAR A "void set_region(int row, int col, JSONObject r_json)" UNA VEZ AÑADIDAS LAS FACTORIAS
	public void set_region(int row, int col, JSONObject r_json) {
		
	}
	
	public void add_animal(Animal a) {
		
	}
	
	public void add_animal(JSONObject a_json) {
		
	}
	
	public MapInfo get_map_info() {
		return reg_manager;
	}
	
	public List<? extends Animalnfo> get_animals() {
		return this.animal_list;
	}
	
	public double get_time() {
		return this.current_time;
	}
	
	public void advance(double dt) {
		this.current_time += dt;
		List<Animal> deadAnimals =  this.animal_list.stream().filter(animal -> animal.get_state() == State.DEAD).toList();
		this.animal_list.removeAll(deadAnimals);
		for(Animal a : deadAnimals) {
			this.reg_manager.unregister_animal(a);
		}
		for(Animal a: this.animal_list) {
			a.update(dt);
			this.reg_manager.update_animal_region(a);
		}
		this.reg_manager.update_all_regions(dt);
		
		for(Animal a : this.animal_list) {
			if(a.is_pregnant()) {
				this.add_animal(a.deliver_baby().as_JSON());
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
