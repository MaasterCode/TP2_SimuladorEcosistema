package simulator.factories;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.LinkedList;

public class BuilderBasedFactory<T> implements Factory<T> {
	private Map<String, Builder<T>> _builders;
	private List<JSONObject> _builders_info;
	
	public BuilderBasedFactory() {
		_builders = new HashMap<>();
		_builders_info = new LinkedList<>();
	}
	public BuilderBasedFactory(List<Builder<T>> builders) {
		this();
		
		for (Builder<T> b : builders) {
			add_builder(b);
		}
	
	}
	public void add_builder(Builder<T> b) {
		_builders.put(b.get_type_tag(), b);
		_builders_info.add(b.get_info());
	}
	@Override
	public T create_instance(JSONObject info) {
		if (info == null) {
		throw new IllegalArgumentException("’info’ cannot be null");
		}
			
		if (_builders.containsKey(info.getString("type"))) {
			
			JSONObject obj = info.has("data") ? info.getJSONObject("data") : new JSONObject();
			
			Builder<T> b = _builders.get(info.get("type"));
			
			T c = b.create_instance(obj);
		
			if (c == null)
				throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());
			
			return c;
		}
	
		else
			throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());
	
	}
	@Override
	public List<JSONObject> get_info() {
		return Collections.unmodifiableList(_builders_info);
	}
}
