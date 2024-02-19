package simulator.model;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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
		//Inicializar _animal_region con una estructura de datos adecuada, no sé cómo.
	}
	
	
	@Override
	public int get_cols() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int get_rows() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int get_width() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int get_height() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int get_region_width() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int get_region_height() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double get_food(Animal a, double dt) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter) {
		// TODO Auto-generated method stub
		return null;
	}

}
