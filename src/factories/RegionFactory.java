package factories;

import java.util.ArrayList;
import java.util.List;

import simulator.model.Region;

public class RegionFactory {
	
	public RegionFactory() {
		List<Builder<Region>> region_builders = new ArrayList<>();
		region_builders.add(new DefaultRegionBuilder<>());
		region_builders.add(new DynamicSupplyRegionBuilder<>());
		Factory<Region> region_strategy_factory = new BuilderBasedFactory<Region>(region_builders);
	}

}
