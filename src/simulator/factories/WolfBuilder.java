package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Animal>{
	
	Factory<SelectionStrategy> strategy_factory;
	
	public WolfBuilder( Factory<SelectionStrategy> select_strategy_factory) {
		super("wolf", "{}");
		this.strategy_factory = select_strategy_factory;
	}

	@Override
	protected Wolf create_instance(JSONObject data) {
		SelectionStrategy mate_strategy = new SelectFirst();
		SelectionStrategy hunt_strategy = new SelectFirst();
		Vector2D pos = null;
		if (data.has("mate_strategy")) {
			mate_strategy = this.strategy_factory.create_instance(data.getJSONObject("mate_strategy"));
		}
		if (data.has("hunter_strategy")) {
			hunt_strategy = this.strategy_factory.create_instance(data.getJSONObject("hunter_strategy"));
		}
		
		if( data.has("pos")) {
			JSONObject pos_json = data.getJSONObject("pos");
			
			  // Extraer los valores de x_range y y_range
	        JSONArray xRange = pos_json.getJSONArray("x_range");
	        JSONArray yRange = pos_json.getJSONArray("y_range");

			double x = Utils._rand.nextDouble(xRange.getDouble(0), xRange.getDouble(1));
	        double y = Utils._rand.nextDouble(yRange.getDouble(0), yRange.getDouble(1));
	        pos = new  Vector2D(x,y);
	        
		}
		return new Wolf(mate_strategy, hunt_strategy, pos);
	}

}
