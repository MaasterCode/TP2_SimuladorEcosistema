package simulator.model;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONObject;
import org.json.JSONArray;

public class RegionManager implements AnimalMapView{

	
	protected int _width;
	protected int _height;
	protected int _cols;
	protected int _rows;
	protected int _region_width;
	protected int _region_height;
	protected Region[][] _regions;
	protected Map<Animal,Region> _animal_region;
	
	
	public RegionManager(int cols, int rows, int width, int height) {
		_cols = cols;
		_rows = rows;
		_width = width;
		_height = height;
		_region_width = _width/cols;
		_region_height = _height/rows;
		initialize_regions();
		_animal_region = new HashMap<Animal,Region>();
	}
	
	private void initialize_regions() {
		
		for (int i = 0; i < _rows; i++) {
			for (int j = 0; j < _cols; j++) {
				
				_regions[i][j] = new DefaultRegion();
				
			}
		}
		
	}
	
	
	@Override
	public int get_cols() {
		return _cols;
	}
	@Override
	public int get_rows() {
		return _rows;
	}
	@Override
	public int get_width() {
		return _width;
	}
	@Override
	public int get_height() {
		return _height;
	}
	@Override
	public int get_region_width() {
		return _region_width;
	}
	@Override
	public int get_region_height() {
		return _region_height;
	}
	
	@Override
	public double get_food(Animal a, double dt) {
		return this._animal_region.get(a).get_food(a, dt);
	}

	@Override
	public List<Animal> get_animals_in_range(Animal a, Predicate<Animal> filter) {
	    List<Animal> animalsInRange = new ArrayList<>();

	    // X's e Y's que están dentro del rango de visión del animal "a", es decir, las posiciones mínimas y máximas de la lista de regiones que están dentro del rango de visión.
	    int minX = Math.max(0, (int) (a.get_position().getX() - a.get_sight_range()) / get_region_width());
	    int minY = Math.max(0, (int) (a.get_position().getY() - a.get_sight_range()) / get_region_height());
	    int maxX = Math.min(get_cols(), (int) (a.get_position().getX() + a.get_sight_range()) / get_region_width());
	    int maxY = Math.min(get_rows(), (int) (a.get_position().getY() + a.get_sight_range()) / get_region_height());

	    // Vamos mirando todas las regiones entre las mínimas y máximas posiciones que cumplen lo anterior.
	    for (int i = minX; i < maxX; i++) {
	        for (int j = minY; j < maxY; j++) {
	            Region region = _regions[i][j];
	        
	            if (region != null) {
	             
	                for (Animal animal : region.getAnimals()) {
	                    // Está en el rango de visión del animal? && cumple la condición del filter?
	                    if (animalInRange(a, animal) && filter.test(animal)) 
	                        animalsInRange.add(animal);
	                    
	                }
	            }
	        }
	    }
	    return animalsInRange;
	}

	public boolean animalInRange(Animal a, Animal b) {
	    return a.get_position().distanceTo(b.get_position()) < a.get_sight_range();
	}

	
	
	
	public void set_region(int row, int col, Region r) {
		
		if ((row >= 0 && row <= this._rows) && (col >= 0 && col <= this._cols)) {
			for (Animal a : this._regions[row][col].getAnimals()) {
				
				r.add_animal(a);
				this._animal_region.put(a, r);
				
			}
		
			this._regions[row][col] = r;
		}
	}
	
	public void register_animal(Animal a) {
	
		// Calculamos la fila y columna en la que está el animal
		int row = (int) a.get_position().getX()/this._region_width;
		int col = (int) a.get_position().getY()/this._region_height;
		
		_regions[row][col].add_animal(a);
		
		this._animal_region.put(a, _regions[row][col]);
		
	}
	
	public void unregister_animal(Animal a) {
		
		this._animal_region.get(a).remove_animal(a);;
		
		this._animal_region.remove(a);
		
		
	}
	
	public void update_animal_region(Animal a) {
		int row = (int) a.get_position().getX()/this._region_height;
		int col = (int) a.get_position().getY()/this._region_width;
		
		
		Region oldR = _animal_region.get(a);
		Region check = _regions[row][col];
		
		if (oldR == null ||!oldR.equals(check)) {
			
			this._animal_region.put(a, check);
			
			if (oldR != null) 
				this._animal_region.remove(a, oldR);	
			
		}
		
	}
	
	public void update_all_regions(double dt) {
		
		for (int i = 0; i  < _rows; i++) {
			for (int j = 0; j < _cols; j++) {
				
				_regions[i][j].update(dt);
				
			}
		}
		
	}

	public JSONObject as_JSON() {
	    JSONObject rs = new JSONObject(); // Objeto JSON para almacenar todas las regiones
	    
	    JSONArray rArray = new JSONArray(); // Array JSON para almacenar las regiones individuales
	    
	    for (int i = 0; i < _rows; i++) {
	        for (int j = 0; j < _cols; j++) {
	            JSONObject r = new JSONObject(); // Objeto JSON para representar cada región
	            
	            r.put("row", i); // Agregar la fila de la región al objeto JSON
	            r.put("col", j); // Agregar la columna de la región al objeto JSON
	            
	            Region region = _regions[i][j]; // Obtener la región en la fila i y columna j
	            
	            if (region != null) {
	                JSONObject dataJSON = region.as_JSON(); // Obtener la representación JSON de la región
	                r.put("data", dataJSON); // Agregar los datos de la región al objeto JSON
	            } else {
	                r.put("data", JSONObject.NULL); // Si la región está vacía, agregar un valor nulo
	            }
	            
	            rArray.put(r); // Agregar el objeto JSON de la región al array de regiones
	        }
	    }
	    
	    rs.put("regiones", rArray); // Agregar el array de regiones al objeto JSON principal
	    
	    return rs; // Devolver el objeto JSON que representa todas las regiones
	}

	
	
}
