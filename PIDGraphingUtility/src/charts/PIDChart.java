package charts;

import java.util.ArrayList;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class PIDChart extends Application {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start(Stage stage) {
		Double[] clearArray = new Double[0];
		NetworkTableInstance.getDefault().startClient("roboRIO-500-FRC.local");// localhost; roboRIO-500-FRC.local;
																				// roboRIO-502-FRC.local
		NetworkTable table = NetworkTableInstance.getDefault().getTable("PIDTuner");

		try {// Network tables access is slow you must delay 3 seconds to give it a chance to
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
			// do nothing for right now
		}

		int numberOfRuns = table.getEntry("PTNumberOfRuns").getNumber(0).intValue();

		ArrayList<Double[]> allTimestamps = new ArrayList<Double[]>();
		ArrayList<Double[]> allMotors = new ArrayList<Double[]>();
		ArrayList<Double[]> allEncoders = new ArrayList<Double[]>();
		ArrayList<Integer> allNumberOfPoints = new ArrayList<Integer>();

		Number ptSetpoint = table.getEntry("Setpoint").getNumber(0);

		for (int i = 0; i < numberOfRuns; i += 1) {
			Double[] PTTimestamp = table.getEntry("PTTimestamp " + Integer.toString(i + 1)).getDoubleArray(clearArray);
			Double[] PTMotor = table.getEntry("PTMotor " + Integer.toString(i + 1)).getDoubleArray(clearArray);
			Double[] PTEncoder = table.getEntry("PTEncoder " + Integer.toString(i + 1)).getDoubleArray(clearArray);
			Integer PTNumberOfPoints = table.getEntry("NumberOfPoints " + Integer.toString(i + 1)).getNumber(0).intValue();
			
			System.out.println("ptnumberofpoints" + PTNumberOfPoints);
			Number[] setPointArray = new Number[PTNumberOfPoints];

			for (int n = 0; n < PTNumberOfPoints; n += 1) {
				setPointArray[n] = ptSetpoint;

			}

			allTimestamps.add(PTTimestamp); // 0
			allMotors.add(PTMotor);// 1
			allEncoders.add(PTEncoder); // 2
			allNumberOfPoints.add(PTNumberOfPoints); // 3 //problem happening here
		}

		// Set properties for graph window
		stage.setTitle("FF503 PID Tuning | Written by James Chen and Areeb Rahim");
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Time");
		final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

		lineChart.setTitle("FF503 PID Tuning");
		lineChart.setCreateSymbols(false);

		ArrayList<XYChart.Series> seriesObjectsArray = new ArrayList<XYChart.Series>();

		for (int i = 0; i < numberOfRuns; i++) {
			XYChart.Series output = new XYChart.Series();
			output.setName("Motor Output" + Integer.toString(i + 1));

			XYChart.Series angle = new XYChart.Series();
			angle.setName("Angle" + Integer.toString(i + 1));

			XYChart.Series setpoint = new XYChart.Series();
			setpoint.setName("Setpoint" + Integer.toString(i + 1));

			//there was a problem on this line
			System.out.println(allNumberOfPoints.get(i).intValue());
			for (int n = 0; n < allNumberOfPoints.get(i).intValue(); n+=1) { // Graphs the points
				
		
				String s = Double.toString(allTimestamps.get(i)[n]);
				output.getData().add(new XYChart.Data(s, allMotors.get(i)[n]));
				angle.getData().add(new XYChart.Data(s, allEncoders.get(i)[n]));
				setpoint.getData().add(new XYChart.Data(s, allEncoders.get(i)[n]));
			}
			seriesObjectsArray.add(output);
			seriesObjectsArray.add(angle);
			seriesObjectsArray.add(setpoint);
		}

		Scene scene = new Scene(lineChart, 800, 600);
		for (int i = 0; i < numberOfRuns; i++) {
			lineChart.getData().add(seriesObjectsArray.get(i));
			lineChart.getData().add(seriesObjectsArray.get(i + 1));
			lineChart.getData().add(seriesObjectsArray.get(i + 2));
		}
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}