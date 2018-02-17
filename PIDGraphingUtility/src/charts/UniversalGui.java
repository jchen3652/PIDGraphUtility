package charts;

import java.util.ArrayList;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class UniversalGui extends Application {
	int i;
	int pointsToGraph;
	static NetworkTable PIDTunerTable;
	Stage graphWindow = new Stage();
	static ArrayList<CheckBox> checkBoxArray;

	public static void main(String[] args) {
		NetworkTableInstance.getDefault().startClient("roboRIO-500-FRC.local");// localhost roboRIO-500-FRC.local
																				// roboRIO-502-FRC.local
		PIDTunerTable = NetworkTableInstance.getDefault().getTable("PIDTuner");
		launch(args);
	}

	@Override
	public void start(Stage mainWindow) throws InterruptedException {

		mainWindow.setTitle("FF503 PID Tuning | By James Chen and Areeb Rahim");
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setHgap(10);
		grid.setVgap(10);
		String[] defaultd = new String[2];
		defaultd[0] = "test0";
		defaultd[1] = "test1";
		ObservableList<String> tunerOptions = FXCollections.observableArrayList();

		String[] allKeyNames = PIDTunerTable.getEntry("All Key Names").getStringArray(defaultd);
		for (String o : PIDTunerTable.getEntry("All Key Names").getStringArray(defaultd)) {
			tunerOptions.add(o);
		}

		checkBoxArray = new ArrayList<CheckBox>();
		for (String o : allKeyNames) {
			CheckBox cb = new CheckBox();
			cb.setText(o);
			checkBoxArray.add(cb);
		}
		
		Button graphThings = new Button();
		graphThings.setText("Graph");
		graphThings.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				for (int i = 0; i < checkBoxArray.size(); i++) {
					if (checkBoxArray.get(i).isSelected()) {
						UniversalGraphingUtility.yIndexes.add(i);
						// UniversalGraphingUtility.addDependentVariableIndex(i);
					}
				}

				UniversalGraphingUtility.runGraph();
				graphWindow.setTitle("FF503 PID Tuning | Written by James Chen and Areeb Rahim");
				graphWindow.setScene(UniversalGraphingUtility.scene);
				graphWindow.show();
			}
		});

		final ComboBox<String> tunerBox = new ComboBox<String>(tunerOptions);
		tunerBox.setPromptText("X Variable");

		grid.add(new Label("What Y variables should be graphed?"), 0, 1);
		for (int i = 0; i < allKeyNames.length; i++) {
			grid.add(checkBoxArray.get(i), 0, i + 2);
		}
		grid.add(new Label("What should be x variable? "), 0, 0);
		grid.add(tunerBox, 1, 0);

		grid.add(graphThings, 0, allKeyNames.length + 3);

		mainWindow.setScene(new Scene(grid, 600, 400));
		mainWindow.show();

		tunerBox.valueProperty().addListener((obs, oldItem, newItem) -> {
			for (int i = 0; i < allKeyNames.length; i++) {
				if (newItem == allKeyNames[i]) {
					UniversalGraphingUtility.setIndependentVariableIndex(i);

				}
			}
		});
	}
}