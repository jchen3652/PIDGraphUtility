package oldpidtuner;

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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class PIDChart extends Application {
	static NetworkTable PIDTunerTable;
	static Double[] clearArray = new Double[0];
	static Scene scene;
	public PIDChart() {
		NetworkTableInstance.getDefault().startClient("roboRIO-500-FRC.local");// localhost;
		PIDTunerTable = NetworkTableInstance.getDefault().getTable("PIDTuner"); // roboRIO-500-FRC.local;// roboRIO-502-FRC.local
	}
	
	public static void runGraph() {
		try {// Network tables access is slow you must delay 3 seconds to give it a chance to
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
			// do nothing for right now
		}

		int numberOfRuns = PIDTunerTable.getEntry("PTNumberOfRuns").getNumber(0).intValue();

		ArrayList<Double[]> allTimestamps = new ArrayList<Double[]>();
		ArrayList<double[]> allMotors = new ArrayList<double[]>();
		ArrayList<double[]> allEncoders = new ArrayList<double[]>();
		ArrayList<double[]> allSetpoints = new ArrayList<double[]>();
		ArrayList<Integer> allNumberOfPoints = new ArrayList<Integer>();

		for (int i = 0; i < numberOfRuns; i += 1) {
			Double[] PTTimestamp = PIDTunerTable.getEntry("PTTimestamp " + Integer.toString(i + 1))
					.getDoubleArray(PIDChart.clearArray);
			double[] PTMotor = Util.toRealDoubleArray(
					PIDTunerTable.getEntry("PTMotor " + Integer.toString(i + 1)).getDoubleArray(PIDChart.clearArray));
			double[] PTEncoder = Util.toRealDoubleArray(
					PIDTunerTable.getEntry("PTEncoder " + Integer.toString(i + 1)).getDoubleArray(PIDChart.clearArray));
			int PTNumberOfPoints = PIDTunerTable.getEntry("NumberOfPoints " + Integer.toString(i + 1)).getNumber(0)
					.intValue();
			double[] setPointArray = new double[PTNumberOfPoints];
			double ptSetpoint = PIDTunerTable.getEntry("Setpoint " + Integer.toString(i + 1)).getNumber(0)
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

			XYChart.Series position = new XYChart.Series();
			position.setName("Distance " + Integer.toString(i + 1));

			XYChart.Series setpoint = new XYChart.Series();
			setpoint.setName("Setpoint " + Integer.toString(i + 1));

			// there was a problem on this line
			for (int n = 0; n < allTimestamps.get(i).length; n++) { // Graphs the points
				String s = (allTimestamps.get(i)[n]).toString();
				output.getData().add(new XYChart.Data(s, allMotors.get(i)[n]));
				position.getData().add(new XYChart.Data(s, allEncoders.get(i)[n]));
				setpoint.getData().add(new XYChart.Data(s, allSetpoints.get(i)[n]));
			}
			seriesObjectsArray.add(output);
			seriesObjectsArray.add(position);
			seriesObjectsArray.add(setpoint);
		}

		PIDChart.scene = new Scene(lineChart, 800, 600);
		for (int i = 0; i < numberOfRuns; i += 1) {
			lineChart.getData().add(seriesObjectsArray.get(3 * i));
			lineChart.getData().add(seriesObjectsArray.get(3 * i + 1));
			lineChart.getData().add(seriesObjectsArray.get(3 * i + 2));
		}
	}
	
	
	@Override
	public void start(Stage graphWindow) {
		runGraph();
		graphWindow.setTitle("FF503 PID Tuning | Written by James Chen and Areeb Rahim");
		graphWindow.setScene(scene);
		graphWindow.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}