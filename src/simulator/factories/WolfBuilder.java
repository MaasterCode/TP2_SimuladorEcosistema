package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Wolf>{
	
	Factory<SelectionStrategy> strategy_factory;
	
	public WolfBuilder( Factory<SelectionStrategy> select_strategy_factory) {
		super("Wolf", "");
		this.strategy_factory = select_strategy_factory;
	}

	@Override
	protected Wolf create_instance(JSONObject data) {
		SelectionStrategy mate_strategy = new SelectFirst();
		SelectionStrategy danger_strategy = new SelectFirst();
		Vector2D pos = null;
		if (data.has("mate_strategy")) {
			mate_strategy = this.strategy_factory.create_instance(data.getJSONObject("mate_strategy"));
		}
		if (data.has("danger_strategy")) {
			danger_strategy = this.strategy_factory.create_instance(data.getJSONObject("danger_strategy"));
		}
		
		if( data.has("pos")) {
			JSONObject pos_json = data.getJSONObject("data").getJSONObject("pos");

	        // Extraer los valores de x_range y y_range
	        JSONArray xRange = pos_json.getJSONArray("x_range");
	        JSONArray yRange = pos_json.getJSONArray("y_range");
	        pos = Vector2D.get_random_vector(xRange.getDouble(0), xRange.getDouble(1));
	        
		}
		return new Wolf(mate_strategy, danger_strategy, pos);
	}

}
