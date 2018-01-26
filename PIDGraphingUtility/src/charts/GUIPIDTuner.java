package charts;

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
//from   w w  w.j  av  a  2s  . c  o  m

@SuppressWarnings({ "rawtypes", "unchecked" })

public class GUIPIDTuner extends Application {
	int i;
	int pointsToGraph;
	XYChart.Series output;
	XYChart.Series distance;
	XYChart.Series setpoint;
	static NetworkTable table;

	public static void main(String[] args) {
		NetworkTableInstance.getDefault().startClient("roboRIO-500-FRC.local");// localhost
		// //roboRIO-500-FRC.local
		// //roboRIO-502-FRC.local
		table = NetworkTableInstance.getDefault().getTable("PIDTuner");

		launch(args);
	}

	@Override
	public void start(Stage mainWindows) {
		mainWindows.setTitle("FF503 PID Tuning | By James Chen and Areeb Rahim");
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setHgap(10);
		grid.setVgap(10);

		final TextField pInput = new TextField();
		pInput.setPromptText("P Value");
		pInput.setPrefColumnCount(10);
		pInput.getText();
		GridPane.setConstraints(pInput, 1, 0);
		grid.add(new Label("Enter the P Value: "), 0, 0);
		grid.getChildren().add(pInput);

		final TextField iInput = new TextField();
		iInput.setPromptText("I Value");
		iInput.setPrefColumnCount(10);
		iInput.getText();
		GridPane.setConstraints(iInput, 1, 1);
		grid.add(new Label("Enter the I Value: "), 0, 1);
		grid.getChildren().add(iInput);

		final TextField dInput = new TextField();
		dInput.setPromptText("D Value");
		dInput.setPrefColumnCount(10);
		dInput.getText();
		GridPane.setConstraints(dInput, 1, 2);
		grid.add(new Label("Enter the D Value: "), 0, 2);
		grid.getChildren().add(dInput);

		final TextField distanceInput = new TextField();
		distanceInput.setPromptText("Distance Value");
		distanceInput.setPrefColumnCount(10);
		distanceInput.getText();
		GridPane.setConstraints(distanceInput, 1, 3);
		grid.add(new Label("Enter the Distance in Inches: "), 0, 3);
		grid.getChildren().add(distanceInput);
		
		Button sendConfirm = new Button();
		sendConfirm.setText("Confirm values");
		sendConfirm.setOnAction(new EventHandler<ActionEvent>() {

		Label ErrorMessage = new Label();	
		Label SuccessMessage = new Label();
			@Override
			public void handle(ActionEvent event) {
				try {
					ErrorMessage.setText("");
					table.getEntry("P").forceSetNumber(Double.parseDouble(pInput.getText()));
					table.getEntry("I").forceSetNumber(Double.parseDouble(iInput.getText()));
					table.getEntry("D").forceSetNumber(Double.parseDouble(dInput.getText()));
					table.getEntry("Distance").forceSetNumber(Double.parseDouble(distanceInput.getText()));
					
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
		startGrapher.setText("Run the program");
		startGrapher.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				grid.add(new Label("Please wait..."), 1, 3);
				Stage graphWindow = new Stage();
				Double[] clearArray = new Double[pointsToGraph];
				for (int i = 0; i < pointsToGraph; i++) {
					clearArray[i] = 0.0;
				}

				try {// Network tables access is slow you must delay 3 seconds to give it a chance to
						// access your requested table
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
					// do nothing for right now
				}

				int numberOfRuns = table.getEntry("PTNumberOfRuns").getNumber(0).intValue();

				Number ptSetpoint = table.getEntry("Setpoint").getNumber(0);

				Number[] setPointArray = new Number[pointsToGraph];

				for (int i = 0; i < pointsToGraph; i += 1) {
					setPointArray[i] = ptSetpoint;

				}
				graphWindow.setTitle("FF503 PID Tuning | Written by James Chen and Areeb Rahim");
				final CategoryAxis xAxis = new CategoryAxis();
				final NumberAxis yAxis = new NumberAxis();
				xAxis.setLabel("Time");
				final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

				lineChart.setTitle("FF503 PID Tuning");
				lineChart.setCreateSymbols(false);

				for (int i = 0; i < numberOfRuns; i++) {
					Double[] ptTimestamp = table.getEntry("PTTimestamp " + Integer.toString(i))
							.getDoubleArray(clearArray);
					Double[] ptMotor = table.getEntry("PTMotor " + Integer.toString(i)).getDoubleArray(clearArray);
					Double[] ptEncoder = table.getEntry("PTEncoder " + Integer.toString(i)).getDoubleArray(clearArray);

					Number numberOfPoints = table.getEntry("NumberOfPoints " + Integer.toString(i)).getNumber(0);
					pointsToGraph = numberOfPoints.intValue();

					output = new XYChart.Series();
					output.setName("Motor Output " + Integer.toString(i));

					distance = new XYChart.Series();
					distance.setName("Angle " + Integer.toString(i));

					setpoint = new XYChart.Series();
					setpoint.setName("Setpoint " + Integer.toString(i));

					for (i = 0; i < pointsToGraph; i++) { // Graphs the points
						String s = Double.toString(ptTimestamp[i]);
						output.getData().add(new XYChart.Data(s, ptMotor[i]));
						distance.getData().add(new XYChart.Data(s, ptEncoder[i]));
						setpoint.getData().add(new XYChart.Data(s, setPointArray[i]));
					}

					lineChart.getData().addAll(output, distance, setpoint);

				}

				Scene scene = new Scene(lineChart, 800, 600);

				graphWindow.setScene(scene);
				graphWindow.show();

			}
		});

		HBox hbButtons = new HBox();
		hbButtons.setSpacing(10.0);
		hbButtons.getChildren().addAll(startGrapher, sendConfirm);

		grid.add(startGrapher, 0, 5);
		grid.add(sendConfirm, 0, 4);
		mainWindows.setScene(new Scene(grid, 600, 400));
		mainWindows.show();
	}
}