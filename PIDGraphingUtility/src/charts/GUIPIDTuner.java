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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

	double defaultP = 0.15;
	double defaultI = 0.0;
	double defaultD = 0.02;
	double defaultTolerance = 0.1;
	double defaultDistance = 24;

	public static void main(String[] args) {
		NetworkTableInstance.getDefault().startClient("roboRIO-500-FRC.local");// localhost
		// //roboRIO-500-FRC.local
		// //roboRIO-502-FRC.local
		PIDTunerTable = NetworkTableInstance.getDefault().getTable("PIDTuner");
		SmartDashboardTable = NetworkTableInstance.getDefault().getTable("Smart Dashboard");
		launch(args);
	}

	@Override
	public void start(Stage mainWindow) {
		mainWindow.setTitle("FF503 PID Tuning | By James Chen and Areeb Rahim");
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setHgap(10);
		grid.setVgap(10);

		final TextField pInput = new TextField();
		pInput.setPromptText("P Value");
		pInput.setPrefColumnCount(10);
		///////////////////////////////////////////////
		GridPane.setConstraints(pInput, 1, 0);
		grid.add(new Label("Enter the P Value: "), 0, 0);
		grid.getChildren().add(pInput);

		final TextField iInput = new TextField();
		iInput.setPromptText("I Value");
		iInput.setPrefColumnCount(10);
		//////////////////////////////////////////////////
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

		Button sendConfirm = new Button();
		sendConfirm.setText("Confirm values");
		sendConfirm.setOnAction(new EventHandler<ActionEvent>() {

			Label ErrorMessage = new Label();
			Label SuccessMessage = new Label();

			@Override
			public void handle(ActionEvent event) {
				try {
					ErrorMessage.setText("");
					SmartDashboardTable.getEntry("P").forceSetNumber(Double.parseDouble(pInput.getText()));
					SmartDashboardTable.getEntry("I").forceSetNumber(Double.parseDouble(iInput.getText()));
					SmartDashboardTable.getEntry("D").forceSetNumber(Double.parseDouble(dInput.getText()));
					SmartDashboardTable.getEntry("Target (inches)")
							.forceSetNumber(Double.parseDouble(distanceInput.getText()));
					SmartDashboardTable.getEntry("Tolerance")
							.forceSetNumber(Double.parseDouble(distanceInput.getText()));
					SuccessMessage.setText("Values have been sent to network tables");
					grid.add(SuccessMessage, 3, 0);

					System.out.println("P is " + pInput.getText());
					System.out.println("I is " + iInput.getText());
					System.out.println("D is " + dInput.getText());
				} catch (Exception e) {
					SuccessMessage.setText("");
					ErrorMessage.setText("Check that your inputs are doubles");
					grid.add(ErrorMessage, 3, 0);
				}
			}
		});

		Button startGrapher = new Button();
		startGrapher.setText("Run the robot program (SAY CLEAR)");
		startGrapher.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				SmartDashboardTable.getEntry("Confirm").forceSetBoolean(true);
				grid.add(new Label("Please wait..."), 1, 5);
				Stage graphWindow = new Stage();
				Double[] clearArray = new Double[0];
				NetworkTableInstance.getDefault().startClient("roboRIO-500-FRC.local");// localhost;
																						// roboRIO-500-FRC.local;
																						// roboRIO-502-FRC.local
				NetworkTable table = NetworkTableInstance.getDefault().getTable("PIDTuner");

				while (PIDTunerTable.getEntry("Program Finished").getBoolean(false) != true) {
				}

				try {// Network tables access is slow you must delay 3 seconds to give it a chance to
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
					// do nothing for right now
				}

				int numberOfRuns = table.getEntry("PTNumberOfRuns").getNumber(0).intValue();

				ArrayList<Double[]> allTimestamps = new ArrayList<Double[]>();
				ArrayList<double[]> allMotors = new ArrayList<double[]>();
				ArrayList<double[]> allEncoders = new ArrayList<double[]>();
				ArrayList<double[]> allSetpoints = new ArrayList<double[]>();
				ArrayList<Integer> allNumberOfPoints = new ArrayList<Integer>();

				for (int i = 0; i < numberOfRuns; i += 1) {
					Double[] PTTimestamp = table.getEntry("PTTimestamp " + Integer.toString(i + 1))
							.getDoubleArray(clearArray);
					double[] PTMotor = Util.toRealDoubleArray(
							table.getEntry("PTMotor " + Integer.toString(i + 1)).getDoubleArray(clearArray));
					double[] PTEncoder = Util.toRealDoubleArray(
							table.getEntry("PTEncoder " + Integer.toString(i + 1)).getDoubleArray(clearArray));
					int PTNumberOfPoints = table.getEntry("NumberOfPoints " + Integer.toString(i + 1)).getNumber(0)
							.intValue();
					double[] setPointArray = new double[PTNumberOfPoints];
					double ptSetpoint = table.getEntry("Setpoint " + Integer.toString(i + 1)).getNumber(0)
							.doubleValue();
					for (int n = 0; n < PTNumberOfPoints; n += 1) {
						setPointArray[n] = ptSetpoint;

					}

					allTimestamps.add(PTTimestamp); // 0
					allMotors.add(PTMotor);// 1
					allEncoders.add(PTEncoder); // 2
					allNumberOfPoints.add(PTNumberOfPoints); // 3 //problem happening here
					allSetpoints.add(setPointArray);
				}

				// Set properties for graph window
				graphWindow.setTitle("FF503 PID Tuning | Written by James Chen and Areeb Rahim");
				final CategoryAxis xAxis = new CategoryAxis();
				final NumberAxis yAxis = new NumberAxis();
				xAxis.setLabel("Time");
				final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

				lineChart.setTitle("FF503 PID Tuning");
				lineChart.setCreateSymbols(false);

				ArrayList<XYChart.Series> seriesObjectsArray = new ArrayList<XYChart.Series>();

				for (int i = 0; i < numberOfRuns; i++) {
					XYChart.Series output = new XYChart.Series();
					output.setName("Motor Output " + Integer.toString(i + 1));
					System.out.println("The name of the output is" + output.getName());

					XYChart.Series angle = new XYChart.Series();
					angle.setName("Distance " + Integer.toString(i + 1));
					System.out.println("The name of the angle is" + angle.getName());

					XYChart.Series setpoint = new XYChart.Series();
					setpoint.setName("Setpoint " + Integer.toString(i + 1));
					System.out.println("The name of the setpointe is" + setpoint.getName());

					for (int n = 0; n < allNumberOfPoints.get(i); n++) { // Graphs the points
						String s = (allTimestamps.get(i)[n]).toString();
						output.getData().add(new XYChart.Data(s, allMotors.get(i)[n]));
						angle.getData().add(new XYChart.Data(s, allEncoders.get(i)[n]));
						setpoint.getData().add(new XYChart.Data(s, allSetpoints.get(i)[n]));
					}
					seriesObjectsArray.add(output);
					seriesObjectsArray.add(angle);
					seriesObjectsArray.add(setpoint);
				}
				Scene scene = new Scene(lineChart, 800, 600);
				for (int i = 0; i < numberOfRuns; i += 1) {
					lineChart.getData().add(seriesObjectsArray.get(3 * i));
					lineChart.getData().add(seriesObjectsArray.get(3 * i + 1));
					lineChart.getData().add(seriesObjectsArray.get(3 * i + 2));
				}
				graphWindow.setScene(scene);
				graphWindow.show();
			}
		});
		HBox hbButtons = new HBox();
		hbButtons.setSpacing(10.0);
		hbButtons.getChildren().addAll(startGrapher, sendConfirm);
		grid.add(startGrapher, 0, 6);
		grid.add(sendConfirm, 0, 5);
		grid.add(loadDefaultValues, 0, 7);
		mainWindow.setScene(new Scene(grid, 600, 400));
		mainWindow.show();
	}
}