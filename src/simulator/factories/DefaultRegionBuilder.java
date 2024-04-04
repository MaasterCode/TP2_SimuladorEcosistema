package simulator.factories;

import org.json.JSONObject;
import simulator.model.DefaultRegion;
import simulator.model.Region;

public class DefaultRegionBuilder extends Builder<Region> {

	public DefaultRegionBuilder() {
		super("default", "Infinite food supply");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected DefaultRegion create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		return new DefaultRegion();
	}
	
	protected void fill_in_data(JSONObject data) {
		
	}

}
