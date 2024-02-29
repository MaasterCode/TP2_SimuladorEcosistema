package simulator.control;

import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.Simulator;

public class Controller {
	
	private Simulator _sim;
	
	public Controller(Simulator sim)	 {
		this._sim = sim;
	}
	
	public void load_data(JSONObject data) {
		if (data.has("region")) {
			JSONArray row = data.getJSONArray("row");
			JSONArray col = data.getJSONArray("col");
			JSONObject spec = data.getJSONObject("spec");
			
			for(int i = row.getInt(0); i < row.getInt(1); i++) {
				for(int j = col.getInt(0); j < col.getInt(1); j++) {
					this._sim.set_region(i, j, spec);
				}
			}
		}
		
		int amount = data.getInt("amount");
		JSONObject spec = data.getJSONObject("spec");
		
		for(int i = 0; i < amount; i++) {
			_sim.add_animal(spec);
		}
		
	}
	
	public void run(double t, double dt, boolean sv, OutputStream out) {
		JSONObject init_state = this._sim.as_JSON();
		while(_sim.get_time() < t) {
			_sim.advance(dt);
		}
		
		JSONObject final_state = this._sim.as_JSON();
		JSONObject out_json = new JSONObject();
		out_json.put("in", init_state);
		out_json.put("out", final_state);
		try {
			out.write(out_json.toString().getBytes());
		} catch (IOException e) {
			System.err.println("Error al escribir en el out de OutputStream");
			e.printStackTrace();
		}
		
	}

}
