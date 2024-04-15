package simulator.view;

import java.awt.BorderLayout;

import java.io.File;
import java.io.FileReader;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.misc.Utils;

public class ControlPanel extends JPanel {
	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;
	private JToolBar _toolaBar;
	private JFileChooser _fc;
	private boolean _stopped = true; // utilizado en los botones de run/stop

	private JButton _openButton;
	private JButton _viewerButton;
	private JButton _regionsButton;
	private JButton _runButton;
	private JButton _stopButton;
	private JSpinner _stepsSpinner;
	private JTextField _deltaTimeTextField;
	private JButton _quitButton;

	// TODO añade más atributos aquí …
	ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		_changeRegionsDialog = new ChangeRegionsDialog(ctrl);
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		_toolaBar = new JToolBar();
		add(_toolaBar, BorderLayout.PAGE_START);
		// TODO crear los diferentes botones/atributos y añadirlos a _toolaBar.
		// Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
		// _toolaBar.addSeparator() para añadir la línea de separación vertical
		// entre las componentes que lo necesiten.
		
		// Open button
		_openButton = new JButton();
		_openButton.setToolTipText("Open");
		_openButton.setIcon(new ImageIcon("resources/icons/open.png"));
		_openButton.addActionListener((e) -> handleOpenFileButton());
		_toolaBar.add(_openButton);
		
		// Viewer Button
		_viewerButton = new JButton();
		_viewerButton.setToolTipText("Viewer");
		_viewerButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
		_viewerButton.addActionListener((e) -> handleViewerButton());
		_toolaBar.add(_viewerButton);
		
		// Regions Button
		_regionsButton = new JButton();
		_regionsButton.setToolTipText("Regions");
		_regionsButton.setIcon(new ImageIcon("resources/icons/regions.png"));
		_regionsButton.addActionListener((e) -> handleRegionsButton());
		_toolaBar.add(_regionsButton);
		
		// Run Button
		_runButton = new JButton();
		_runButton.setToolTipText("Run");
		_runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		_runButton.addActionListener((e) -> handleRunButton());
		_toolaBar.add(_runButton);
		
		// Stop Button
		_stopButton = new JButton();
		_stopButton.setToolTipText("Stop");
		_stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		_stopButton.addActionListener((e) -> handleStopButton());
		_toolaBar.add(_stopButton);
		
		// Steps TextField
		int value = 3;
		int min = 0;
		int max = 100;
		int steps = 1;
		SpinnerNumberModel nModel = new SpinnerNumberModel(value, min, max, steps);
		_stepsSpinner = new JSpinner(nModel);
		_stepsSpinner.setToolTipText("Choose steps");
		
		// Delta-Time TextField
		_deltaTimeTextField = new JTextField();
		_deltaTimeTextField.setToolTipText("Choose delta time");
		
		// Quit Button
		_toolaBar.add(Box.createGlue()); // this aligns the button to the right
		_toolaBar.addSeparator();
		_quitButton = new JButton();
		_quitButton.setToolTipText("Quit");
		_quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		_quitButton.addActionListener((e) -> ViewUtils.quit(this));
		_toolaBar.add(_quitButton);
		
		// TODO Inicializar _fc con una instancia de JFileChooser. Para que siempre
		// abre en la carpeta de ejemplos puedes usar:
		_fc = new JFileChooser();
		_fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
		// TODO Inicializar _changeRegionsDialog con instancias del diálogo de cambio
		// de regiones
	}
	// TODO el resto de métodos van aquí…
	
	private void run_sim(int n, int dt) {
		if (n > 0 && !_stopped) {
			try {
				_ctrl.advance(dt);
				SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
			} catch (Exception e) {
				// TODO llamar a ViewUtils.showErrorMsg con el mensaje de error
				// que corresponda
				// TODO activar todos los botones
				this.activateAllButtons();
				_stopped = true;
			}
		} else {
		// TODO activar todos los botones
			this.activateAllButtons();
		_stopped = true;
		}
	}
	
	private  void handleOpenFileButton() {
		 int retVal =  _fc.showOpenDialog(ViewUtils.getWindow(this));
		 if (retVal == JFileChooser.APPROVE_OPTION) {
			 File selectedFile = _fc.getSelectedFile();
	            try {
	            	FileReader reader = new FileReader(selectedFile);
	                JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
	                int cols = jsonObject.getInt("cols");
	                int rows = jsonObject.getInt("rows");
	                int width = jsonObject.getInt("width");
	                int height = jsonObject.getInt("height");
	                
	                _ctrl.reset(cols, rows, width, height);
	                _ctrl.load_data(jsonObject);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
		 }
	}
	
	
	private void handleViewerButton() {
		MapWindow mw = new MapWindow();
		/* TODO */
	}
	
	private void handleRegionsButton() {
		_changeRegionsDialog.open(ViewUtils.getWindow(this));
		/* TODO */
	}
	
	private void handleRunButton() {
		try {
			_openButton.setEnabled(false);
			_viewerButton.setEnabled(false);
			_regionsButton.setEnabled(false);
			_runButton.setEnabled(false);
			_stopped = false;
			int steps = (Integer) _stepsSpinner.getValue();
			int deltaTime = Integer.parseInt(_deltaTimeTextField.getText());
			
		} catch(NullPointerException e) {
			ViewUtils.showErrorMsg("Selecciona ...");
		}		
	}
	
	private void handleStopButton() {
		_stopped = true;
	}
	
	private void activateAllButtons() {
		_openButton.setEnabled(true);
		_viewerButton.setEnabled(true);
		_regionsButton.setEnabled(true);
		_runButton.setEnabled(true);
		_stopButton.setEnabled(true);
	}
}