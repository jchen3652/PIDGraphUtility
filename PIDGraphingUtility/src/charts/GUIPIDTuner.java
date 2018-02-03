package charts;

import java.util.ArrayList;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GUIPIDTuner extends Application {
	int i;
	int pointsToGraph;
	XYChart.Series output;
	XYChart.Series distance;
	XYChart.Series setpoint;
	static NetworkTable PIDTunerTable;
	static NetworkTable SmartDashboardTable;
	private static final double defaultP = 0.15;
	private static final double defaultI = 0.0;
	private static final double defaultD = 0.02;
	private static final double defaultTolerance = 0.1;
	private static final double defaultDistance = 24;

	public static void main(String[] args) {
		NetworkTableInstance.getDefault().startClient("roboRIO-500-FRC.local");// localhost roboRIO-500-FRC.local
																				// roboRIO-502-FRC.local
		PIDTunerTable = NetworkTableInstance.getDefault().getTable("PIDTuner");
		SmartDashboardTable = NetworkTableInstance.getDefault().getTable("SmartDashboard");
		launch(args);
	}

	@Override
	public void start(Stage mainWindow) {
		mainWindow.setTitle("FF503 PID Tuning | By James Chen and Areeb Rahim");
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setHgap(10);
		grid.setVgap(10);

		StackPane sp = new StackPane();
		Image img = new Image("https://pbs.twimg.com/profile_images/874276197357596672/kUuht00m.jpg");
		ImageView imgView = new ImageView(img);
		// sp.getChildren().add(imgView);

		final TextField pInput = new TextField();
		pInput.setPromptText("P Value");
		pInput.setPrefColumnCount(10);
		GridPane.setConstraints(pInput, 1, 0);
		grid.add(new Label("Enter the P Value: "), 0, 0);
		grid.getChildren().add(pInput);

		final TextField iInput = new TextField();
		iInput.setPromptText("I Value");
		iInput.setPrefColumnCount(10);
		GridPane.setConstraints(iInput, 1, 1);
		grid.add(new Label("Enter the I Value: "), 0, 1);
		grid.getChildren().add(iInput);

		final TextField dInput = new TextField();
		dInput.setPromptText("D Value");
		dInput.setPrefColumnCount(10);
		GridPane.setConstraints(dInput, 1, 2);
		grid.add(new Label("Enter the D Value: "), 0, 2);
		grid.getChildren().add(dInput);

		final TextField distanceInput = new TextField();
		distanceInput.setPromptText("Distance Value");
		distanceInput.setPrefColumnCount(10);
		GridPane.setConstraints(distanceInput, 1, 3);
		grid.add(new Label("Enter the Distance in Inches: "), 0, 3);
		grid.getChildren().add(distanceInput);

		final TextField tolerance = new TextField();
		tolerance.setPromptText("Tolerance");
		tolerance.setPrefColumnCount(10);
		GridPane.setConstraints(tolerance, 1, 4);
		grid.add(new Label("Enter the Tolerance in Inches: "), 0, 4);
		grid.getChildren().add(tolerance);

		Button loadDefaultValues = new Button();
		loadDefaultValues.setText("Load Default Values");
		loadDefaultValues.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				pInput.setText(Double.toString(defaultP));
				iInput.setText(Double.toString(defaultI));
				dInput.setText(Double.toString(defaultD));
				distanceInput.setText(Double.toString(defaultDistance));
				tolerance.setText(Double.toString(defaultTolerance));
			}
		});

		Button stopRobot = new Button();
		stopRobot.setText("STOP THE FRIGGIN ROBOT");
		stopRobot.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				SmartDashboardTable.getEntry("robotStop").forceSetBoolean(true);
			}
		});

		Button startRobot = new Button();
		startRobot.setText("Run the robot program (SAY CLEAR)");
		startRobot.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				SmartDashboardTable.getEntry("P").forceSetNumber(Double.parseDouble(pInput.getText()));
				SmartDashboardTable.getEntry("I").forceSetNumber(Double.parseDouble(iInput.getText()));
				SmartDashboardTable.getEntry("D").forceSetNumber(Double.parseDouble(dInput.getText()));
				SmartDashboardTable.getEntry("Target (inches)")
						.forceSetNumber(Double.parseDouble(distanceInput.getText()));
				SmartDashboardTable.getEntry("Tolerance").forceSetNumber(Double.parseDouble(tolerance.getText()));
				System.out.println("dang it" + Double.parseDouble(pInput.getText()));

				System.out.println("P is " + pInput.getText());
				System.out.println("I is " + iInput.getText());
				System.out.println("D is " + dInput.getText());

				SmartDashboardTable.getEntry("Confirm").forceSetBoolean(true);
				Stage graphWindow = new Stage();

				while (SmartDashboardTable.getEntry("Finished").getBoolean(false) == false) {
				}

				PIDChart.runGraph();

				graphWindow.setTitle("FF503 PID Tuning | Written by James Chen and Areeb Rahim");
				graphWindow.setScene(PIDChart.scene);
				graphWindow.show();

			}

		});

		HBox hbButtons = new HBox();
		hbButtons.setSpacing(10.0);
		hbButtons.getChildren().addAll(startRobot);
		grid.add(startRobot, 0, 6);
		grid.add(loadDefaultValues, 0, 7);
		grid.add(stopRobot, 0, 8);
		grid.add(sp, 3, 0);
		mainWindow.setScene(new Scene(grid, 600, 400));
		mainWindow.show();
	}
}