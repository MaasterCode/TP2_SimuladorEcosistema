package factories;

import org.json.JSONObject;

import simulator.model.Sheep;

public class SheepBuilder<Sheep> extends Builder<Sheep>{

	public SheepBuilder(StrategyFactory select_strategy_factory) {
		super("Sheep", "");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Sheep create_instance(JSONObject data) {
		
		return null;
	}
	
}
