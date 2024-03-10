package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy>{

	public SelectFirstBuilder() {
		super("first", "{}");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected SelectFirst create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		return new SelectFirst();
	}

}
