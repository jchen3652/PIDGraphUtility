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
	public void start(Stage graphWindow) {
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
		ArrayList<double[]> allMotors = new ArrayList<double[]>();
		ArrayList<double[]> allEncoders = new ArrayList<double[]>();
		ArrayList<double[]> allSetpoints = new ArrayList<double[]>();
		ArrayList<Integer> allNumberOfPoints = new ArrayList<Integer>();
		
		
		

		for (int i = 0; i < numberOfRuns; i += 1) {
			Double[] PTTimestamp = table.getEntry("PTTimestamp " + Integer.toString(i + 1)).getDoubleArray(clearArray);
			double[] PTMotor = Util
					.toRealDoubleArray(table.getEntry("PTMotor " + Integer.toString(i + 1)).getDoubleArray(clearArray));
			double[] PTEncoder = Util.toRealDoubleArray(
					table.getEntry("PTEncoder " + Integer.toString(i + 1)).getDoubleArray(clearArray));
			int PTNumberOfPoints = table.getEntry("NumberOfPoints " + Integer.toString(i + 1)).getNumber(0).intValue();
			double[] setPointArray = new double[PTNumberOfPoints];
			double ptSetpoint = table.getEntry("Setpoint " + Integer.toString(i + 1)).getNumber(0).doubleValue();
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
			
			// there was a problem on this line
			for (int n = 0; n < allNumberOfPoints.get(i); n++) { // Graphs the points
			
				 String s = (allTimestamps.get(i)[n]).toString(); 
				 output.getData().add(new XYChart.Data(s, allMotors.get(i)[n])); 
				 angle.getData().add (new XYChart.Data(s, allEncoders.get(i)[n]));
				 setpoint.getData().add (new XYChart.Data(s, allSetpoints.get(i)[n]));
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
	public static void main(String[] args) {
		launch(args);
	}
}