package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {
	
	private double factor = 2.0;
	private double food = 1000;

	public DynamicSupplyRegionBuilder() {
		super("dynamic", "Dynamic food suply");
		
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		if (data.has("factor")) {
			factor = data.getDouble("factor");
		}
		
		if (data.has("food")) {
			food = data.getDouble("food");
		}
		return new DynamicSupplyRegion(factor, food);
	}
	
	@Override
	protected void fill_in_data(JSONObject data) {
		String factorString = String.format("food increase factor (optional, default %d", factor);
		String foodString = String.format("initial amount of food (optional, default %d", food);
		data.put("factor", factorString);
		data.put("food", foodString);
	}

}
