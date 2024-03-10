package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy> {

	public SelectYoungestBuilder() {
		super("youngest", "{}");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected SelectYoungest create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		return new SelectYoungest();
	}

}
