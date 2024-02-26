package simulator.model;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONObject;

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
		_regions = new DefaultRegion[_region_width][_region_height];
		//Inicializar _animal_region con una estructura de datos adecuada, ver si es así:
		_animal_region = new HashMap<Animal,Region>();
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter) {
		List<Animal> AnimalsInRange = new ArrayList<>();
		
		// Acá se miran primero las regiones cercanas a las del animal e únicamente,
		// para recorrer sus listas de animales y haciendo una condicion que cumpla
		// la funcion animalInRange(a,b) y filter.test(a).
		
		return null;
	}
	
	public boolean animalInRange(Animal a, Animal b) {
		if (a.get_position().distanceTo(b.get_position()) < a.get_sight_range())
			return true;
		else
			return false;
	}
	
	public void set_region(int row, int col, Region r) {
		
		if ((row >= 0 && row <= this._rows) && (col >= 0 && col <= this._cols)) {
			
			Region aux = this._regions[row][col];
			
			this._regions[row][col] = r;
			
			for (Animal a : aux.getAnimals()) {
				
				_animal_region.put(a, this._regions[row][col]);
				
				this._regions[row][col].add_animal(a);
				
			}
			
			aux._animal_list.clear();
			
		}
		
		
	}
	
	public void register_animal(Animal a) {
		//Revisar, probablemente esté mal
		int i = 0;
		int j = 0;
		boolean encontrado = false;
		
		while (i < this._rows && !encontrado) {
			
			j = 0;
			
			while(j < this._cols && !encontrado) {
				/*
				 * Si la pos del animal está dentro del height y width de la region que estamos mirando
				 * encontrado = true*/
				if (!a.get_position().outOfBound((i+1)*this._region_height, (j+1)*this._region_width)) {
					encontrado = true;
				}
				
				j++;
			}
			
			
			i++;
		}
		
		if (encontrado)
			_regions[i][j].add_animal(a);
		
	}
	
	public void unregister_animal(Animal a) {
		
		
		
	}
	
	public void update_animal_region(Animal a) {
		
	}
	
	public void update_all_regions(double dt) {
		
	}

	public JSONObject as_JSON() {
		return null;
	}
	
	
}
