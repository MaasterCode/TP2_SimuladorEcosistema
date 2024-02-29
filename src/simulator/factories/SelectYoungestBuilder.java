package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectYoungest;

public class SelectYoungestBuilder extends Builder<SelectYoungest> {

	public SelectYoungestBuilder() {
		super("SelectYoungest", "");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected SelectYoungest create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		return new SelectYoungest();
	}

}
