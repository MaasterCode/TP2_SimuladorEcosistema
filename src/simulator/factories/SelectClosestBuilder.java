package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectClosest;

public class SelectClosestBuilder extends Builder<SelectClosest> {

	public SelectClosestBuilder() {
		super("SelectClosest", "");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected SelectClosest create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		return new SelectClosest();
	}

}
