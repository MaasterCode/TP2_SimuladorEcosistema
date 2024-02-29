package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;

public class DynamicSupplyRegionBuilder extends Builder<DynamicSupplyRegion> {

	public DynamicSupplyRegionBuilder() {
		super("DynamicSupplyRegion", "");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		double factor = 2.0;
		double food = 1000;
		if (data.has("factor")) {
			factor = data.getDouble("factor");
		}
		
		if (data.has("food")) {
			food = data.getDouble("food");
		}
		return new DynamicSupplyRegion(factor, food);
	}

}
