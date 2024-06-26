package simulator.control;

import java.io.OutputStream;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Simulator;
public class Controller {
	
	private Simulator _sim;
	
	public Controller(Simulator sim)	 {
		this._sim = sim;
	}
	
	public void load_data(JSONObject data) {

		if (data.has("regions"))
			load_regions(data.getJSONArray("regions"));
		
		load_animals(data.getJSONArray("animals"));
		
	}
	
	private void load_animals(JSONArray animals) {
		
		for (int i = 0; i < animals.length(); i++) {
			JSONObject ob = animals.getJSONObject(i);
			
			
			for (int j = 0; j < ob.getInt("amount"); j++) {
				JSONObject o = ob.getJSONObject("spec");
				
				_sim.add_animal(o);
				
			}
		}
		
	}
	
	private void load_regions(JSONArray regions) {
		
		for (int i = 0; i < regions.length();i++) {
			JSONObject ob = regions.getJSONObject(i);
			
			JSONArray row = ob.getJSONArray("row");
			JSONArray col = ob.getJSONArray("col");
			JSONObject spec = ob.getJSONObject("spec");
			
			
			int rf = row.getInt(0);
			int rt = row.getInt(1);
			
			int cf = col.getInt(0);
			int ct = col.getInt(1);
			
			for (int j = rf; j <=rt; j++) {
				for (int k = cf; k <= ct; k++) {
					this._sim.set_region(j, k, spec);
				}
			}
			
		}
		
		
	}
	
	public void run(double t, double dt, boolean sv, OutputStream out) {
		
		SimpleObjectViewer view = null;
		JSONObject sim_json = new JSONObject();

		if (sv) {
			MapInfo m = _sim.get_map_info();
			view = new SimpleObjectViewer("[ECOSYSTEM]", m.get_width(), m.get_height(), m.get_cols(), m.get_rows());
			view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}

		sim_json.put("in", this._sim.as_JSON());
		while (this._sim.get_time() < t) {
			this._sim.advance(dt);
			if (sv) view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}	
		sim_json.put("out", this._sim.as_JSON());

		PrintStream p = new PrintStream(out);
		p.println(sim_json.toString(2));
		
		if (sv) view.close();
	}

	
	private List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.get_genetic_code(), (int) a.get_position().getX(), (int) a.get_position().getY(), (int)Math.round(a.get_age())+2));
		return ol;
	}
	
	public void reset(int cols, int rows, int width, int height) {
		_sim.reset(cols, rows, width, height);
	}
	
	public void set_regions(JSONObject rs) {
		if (rs.has("regions")) {
			JSONArray jsarr = rs.getJSONArray("regions");
			for (int i = 0; i < jsarr.length(); i++) {
				_sim.set_region(0, 0, jsarr.getJSONObject(i));
			}
			
		}
	}
	
	public void advance(double dt) {
		_sim.advance(dt);
	}
	
	public void addObserver(EcoSysObserver o) {
		_sim.addObserver(o);
	}
	
	public void removeObserver(EcoSysObserver o) {
		_sim.removeObserver(o);
	}
}
