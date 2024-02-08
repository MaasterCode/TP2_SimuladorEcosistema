package simulator.model;

import java.util.List;
import java.util.function.Predicate;

public interface AnimalMapView extends MapInfo, FoodSupplier { 
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter);
}

/*

 Le pasas el animal que quieres ver y en el filtro le pasas el codigo genetico del animal.
 Con 2 for busca (con el radio, es decir, el campo visual del animal) todos los animales
 que esten dentro del radio y los añade 
 
 
  */
