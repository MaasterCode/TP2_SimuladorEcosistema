package simulator.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import simulator.model.Alimentation.Diet;

public abstract class Region implements Entity, FoodSupplier, RegionInfo{

	protected List<Animal> _animal_list;
	// En vez de hacer nHerbivores podemos hacer dos atributos nHerb y nCarn para tener directamente la
	// n de herbivoros o carnivoros.
	public Region() {
		// Inicializa los animales
		// Comprobar si se hace así
		_animal_list = new ArrayList<Animal>();
	}
	
	
	// Añade el animal del parámetro al final de la lista.
	public final void add_animal(Animal a) {
		
		
		this._animal_list.add(a);
	}
	
	// Elimina el animal del parámetro de la lista.	
	public final void remove_animal(Animal a) {
		this._animal_list.remove(a);
	}
	
	public final List<Animal> getAnimals(){
		return this._animal_list;
	}
	
	// Devuelve como JSONObject la lista de animales de la region.
	// Probablemente esté mal, revisar.
	public JSONObject as_JSON() {
		JSONObject value = new JSONObject()
				 .put("animals",this._animal_list);
		return value;
	}
	
	protected int nHerbivores() {
		int n = 0;
		
		for (int i = 0; i < this._animal_list.size(); i++) {
			if (this._animal_list.get(i).get_diet() == Diet.HERBIVORE)
				n++;
		}
		
		return n;
	}
	
	
	/* Métodos de las interfaces que "implementa":
	
		*public void update(double dt);
		
		*double get_food(Animal a, double dt);
		
		*Iterable<AnimalInfo>
	
	*/
}
