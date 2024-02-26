package simulator.factories;

import org.json.JSONObject;

import simulator.model.Wolf;

public class WolfBuilder<Wolf> extends Builder<Wolf>{

	public WolfBuilder( StrategyFactory select_strategy_factory) {
		super("Wolf", "");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Wolf create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		return null;
	}

}
