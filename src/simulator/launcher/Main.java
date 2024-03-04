package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.model.SelectionStrategy;
import simulator.model.Region;
import simulator.factories.Factory;
import simulator.factories.SelectClosestBuilder;
import simulator.factories.SelectFirstBuilder;
import simulator.factories.SelectYoungestBuilder;
import simulator.factories.SheepBuilder;
import simulator.factories.WolfBuilder;
import simulator.factories.DefaultRegionBuilder;
import simulator.factories.DynamicSupplyRegionBuilder;
import simulator.control.Controller;


import java.util.List;
import java.util.ArrayList;
import simulator.misc.Utils;
import simulator.model.Animal;
import simulator.model.Simulator;


public class Main {
	

	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private String _tag;
		private String _desc;

		private ExecMode(String modeTag, String modeDesc) {
			_tag = modeTag;
			_desc = modeDesc;
		}

		public String get_tag() {
			return _tag;
		}

		public String get_desc() {
			return _desc;
		}
	}

	// default values for some parameters
	//
	private final static Double _default_time = 10.0; // in seconds

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _time = null;
	private static String _in_file = null;
	private static String _out_file = null;
	private static boolean sv = false;
	private static ExecMode _mode = ExecMode.BATCH;
	
	private static Factory<SelectionStrategy> selection_strategy_factory;
	private static Factory<Animal> animals_factory;
	private static Factory<Region> regions_factory;
	
/*	private static Simulator sim;
	private static Map<String, Factory> factorias = new HashMap<String, Factory>();
*/
	private static void parse_args(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = build_options();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parse_help_option(line, cmdLineOptions);
			parse_in_file_option(line);
			parse_out_file_option(line);
			parse_time_option(line);
			parse_delta_time_option(line);
			parse_simple_viewer_option(line);
			
			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options build_options() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ _default_time + ".")
				.build());

		return cmdLineOptions;
	}

	private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parse_in_file_option(CommandLine line) throws ParseException {
		_in_file = line.getOptionValue("i");
		if (_mode == ExecMode.BATCH && _in_file == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}
	
	private static void parse_out_file_option(CommandLine line) throws ParseException {
		_out_file = line.getOptionValue("o");
		if (_mode == ExecMode.BATCH && _out_file == null)
			throw new ParseException("In batch mode an output configuration file is required");
		
		
	}

	private static void parse_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", _default_time.toString());
		try {
			_time = Double.parseDouble(t);
			assert (_time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	} 

	private static void parse_delta_time_option(CommandLine line) throws ParseException{
		String t = line.getOptionValue("dt", _time.toString());
		try {
			_time = Double.parseDouble(t);
			assert (_time >= 0 && _time < _default_time);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}
	
	private static void parse_simple_viewer_option(CommandLine line) {
		if (line.hasOption("sv"))
			sv = true;
	}
	
	private static void init_factories() {
		
		List<Builder<SelectionStrategy>> selection_strategy_builders = new ArrayList<>();
		selection_strategy_builders.add(new SelectFirstBuilder());
		selection_strategy_builders.add(new SelectClosestBuilder());
		selection_strategy_builders.add(new SelectYoungestBuilder());
		selection_strategy_factory = new BuilderBasedFactory<SelectionStrategy>(selection_strategy_builders);
		
		List<Builder<Animal>> animal_builders = new ArrayList<>();
		animal_builders.add(new SheepBuilder(selection_strategy_factory));
		animal_builders.add(new WolfBuilder(selection_strategy_factory));
		animals_factory = new BuilderBasedFactory<Animal>(animal_builders);

		List<Builder<Region>> region_builders = new ArrayList<>();
		region_builders.add(new DefaultRegionBuilder());
		region_builders.add(new DynamicSupplyRegionBuilder());
		regions_factory = new BuilderBasedFactory<Region>(region_builders);
		
	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}


	private static void start_batch_mode() throws Exception {
		InputStream is = new FileInputStream(new File(_in_file));
		
		JSONObject jis = load_JSON_file(is);
		
		OutputStream os = new FileOutputStream(new File(_out_file));
		
		Simulator sim = new Simulator(jis.getInt("cols"),jis.getInt("rows"),
				jis.getInt("width"),jis.getInt("height"), animals_factory, regions_factory);
		
		Controller cont = new Controller(sim);
		
		cont.load_data(jis);
		
		cont.run(_default_time, _time, sv, os);
		
	}

	private static void start_GUI_mode() throws Exception {
		throw new UnsupportedOperationException("GUI mode is not ready yet ...");
	}

	private static void start(String[] args) throws Exception {
		init_factories();
		parse_args(args);
		switch (_mode) {
		case BATCH:
			start_batch_mode();
			break;
		case GUI:
			start_GUI_mode();
			break;
		}
	}

	public static void main(String[] args) {
		Utils._rand.setSeed(2147483647l);
		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
	
}
