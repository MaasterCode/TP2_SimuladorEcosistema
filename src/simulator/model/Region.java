package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.Alimentation.Diet;

public abstract class Region implements Entity, FoodSupplier, RegionInfo{

	protected List<Animal> _animal_list;
	protected int nHerbivores;
	public Region() {
		_animal_list = new ArrayList<Animal>();
		nHerbivores = 0;
	}
	
	
	// Añade el animal del parámetro al final de la lista.
	public final void add_animal(Animal a) {
		if (a.get_diet() == Diet.HERBIVORE)
			nHerbivores++;
		this._animal_list.add(a);
	}
	
	// Elimina el animal del parámetro de la lista.	
	public final void remove_animal(Animal a) {
		if (a.get_diet() == Diet.HERBIVORE)
			nHerbivores--;
		this._animal_list.remove(a);
	}
	// Devuelve la lista de animales siendo inmodificable.
	public final List<Animal> getAnimals(){
		return Collections.unmodifiableList(this._animal_list);
	}
	
	public List<AnimalInfo> getAnimalsInfo() {
		return new ArrayList<>(_animal_list);
	}
	
	
	public JSONObject as_JSON() {
		JSONArray as = new JSONArray();
		
		for (Animal a : this._animal_list) {
			
			as.put(a.as_JSON());
			
		}
		
		
		JSONObject value = new JSONObject()
				 .put("animals",as);
		return value;
	}
	
	/* Métodos de las interfaces que "implementa":
	
		*public void update(double dt);
		
		*double get_food(Animal a, double dt);
		
		*Iterable<AnimalInfo>
	
	*/
}
