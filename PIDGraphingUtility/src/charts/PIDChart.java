package charts;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class PIDChart extends Application {
	int i;
	
	ArrayList<XYChart.Series> seriesContainer = new ArrayList<XYChart.Series>();

	@Override
	public void start(Stage stage) {
		Double[] clearArray = new Double[0];

		NetworkTableInstance.getDefault().startClient("roboRIO-500-FRC.local");// localhost //roboRIO-500-FRC.local
																				// //roboRIO-502-FRC.local
		NetworkTable table = NetworkTableInstance.getDefault().getTable("PIDTuner");

		try {// Network tables access is slow you must delay 3 seconds to give it a chance to
				// access your requested table
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
			// do nothing for right now
		}

		// Set properties for graph window
		stage.setTitle("FF503 PID Tuning | Written by James Chen and Areeb Rahim");
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Time");
		final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

		lineChart.setTitle("FF503 PID Tuning");
		lineChart.setCreateSymbols(false);
		
		int numberOfRuns = table.getEntry("PTNumberOfRuns").getNumber(0).intValue();
		
		for (int i = 0; i <= numberOfRuns; i+=1) {
			int n = i + 1;
			
			Double[] ptTimestamp = null;
			Double[] ptMotor = null;
			Double[] ptEncoder = null;
			
			
			ptTimestamp = table.getEntry("PTTimestamp " + Integer.toString(i+1)).getDoubleArray(clearArray); //creates the arrays, which change every time
			ptMotor = table.getEntry("PTMotor " + Integer.toString(i+1)).getDoubleArray(clearArray);
			ptEncoder = table.getEntry("PTEncoder " + Integer.toString(i+1)).getDoubleArray(clearArray);

			int pointsToGraph = table.getEntry("NumberOfPoints " + Integer.toString(n)).getNumber(0).intValue();

			Number ptSetpoint = table.getEntry("Setpoint " + Integer.toString(n)).getNumber(0);
			Number[] setPointArray = new Number[pointsToGraph];
			
			
			
			XYChart.Series output = new XYChart.Series();
			output.setName("Motor Output " + Integer.toString(n));

			XYChart.Series angle = new XYChart.Series();
			angle.setName("Angle " + Integer.toString(n));

			XYChart.Series setpoint = new XYChart.Series();
			setpoint.setName("Setpoint " + Integer.toString(n));

			
			
			for (int w = 0; w < pointsToGraph; w += 1) {
				setPointArray[w] = ptSetpoint;
			}
			// temporary code
			for (int x = 0; x < ptTimestamp.length; x++) { // Graphs the points
				String s = Double.toString(ptTimestamp[i]);
				output.getData().add(new XYChart.Data(s, ptMotor[i]));
				angle.getData().add(new XYChart.Data(s, ptEncoder[i]));
				setpoint.getData().add(new XYChart.Data(s, setPointArray[i]));
			}

			seriesContainer.add(output);
			seriesContainer.add(angle);
			seriesContainer.add(setpoint);

		}
			
		System.out.println(seriesContainer);
		for (int i = 0; i < numberOfRuns; i++) {
			lineChart.getData().add(seriesContainer.get(i));
			lineChart.getData().add(seriesContainer.get(i + 1));
			lineChart.getData().add(seriesContainer.get(i + 2));

		}
		
		Scene scene = new Scene(lineChart, 800, 600);

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}