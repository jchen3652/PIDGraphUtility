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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class UniversalGraphingUtility extends Application {
	static NetworkTable PIDTunerTable;
	static Double[] clearArray = new Double[0];
	static Scene scene;
	static int xIndex = 0;
	public static ArrayList<Integer> yIndexes = new ArrayList<Integer>();

	public UniversalGraphingUtility() {
		
	}

	public static void setIndependentVariableIndex(int i) {
		xIndex = i;
	}

	public static void addDependentVariableIndex(int i) {

		yIndexes.add(i);
	}

	public static void runGraph() {

		NetworkTableInstance.getDefault().startClient("roboRIO-500-FRC.local");// localhost;
		PIDTunerTable = NetworkTableInstance.getDefault().getTable("PIDTuner"); // roboRIO-500-FRC.local;//
																				// roboRIO-502-FRC.local
		try {// Network tables access is slow you must delay 3 seconds to give it a chance to
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
		}

		// Dummy variables initialized
		String[] defaultAllKeyNames = new String[3];
		defaultAllKeyNames[0] = "x";
		defaultAllKeyNames[1] = "y";
		defaultAllKeyNames[2] = "y2";
		Double[] xValue = new Double[20];
		Double[] yValue = new Double[20];
		Double[] y2Value = new Double[20];
		for (int i = 0; i < 20; i++) {
			xValue[i] = i*1.0;
			yValue[i] = Math.pow(i, 0.333333) * 1.0;
			y2Value[i] = i/4.0;
		}
		ArrayList<Double[]> allDummyDataValues = new ArrayList<Double[]>();
		allDummyDataValues.add(xValue);
		allDummyDataValues.add(yValue);
		allDummyDataValues.add(y2Value);

		String[] allNTNames = new String[defaultAllKeyNames.length];
		ArrayList<Double[]> allDataValues = new ArrayList<Double[]>();

		allNTNames = PIDTunerTable.getEntry("All Key Names").getStringArray(defaultAllKeyNames);

		for (int i = 0; i < allNTNames.length; i++) {
			Double[] Array = PIDTunerTable.getEntry(allNTNames[i]).getDoubleArray(allDummyDataValues.get(i));
			allDataValues.add(Array);
		}

		/*
		 * Double[] PTMotor =
		 * PIDTunerTable.getEntry(allNTNames[0]).getDoubleArray(clearArray); Double[]
		 * PTTimestamp =
		 * PIDTunerTable.getEntry(allNTNames[1]).getDoubleArray(clearArray); Double[]
		 * PTEncoder = PIDTunerTable.getEntry(allNTNames[2]).getDoubleArray(clearArray);
		 */

		ArrayList<XYChart.Series> seriesObjectsArray = new ArrayList<XYChart.Series>();
		for (int i = 0; i < allNTNames.length; i++) {
			XYChart.Series output = new XYChart.Series();
			output.setName(allNTNames[i]);
			seriesObjectsArray.add(output);
		}

		// Set properties for graph window

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Time");
		final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

		lineChart.setTitle("FF503 PID Tuning");
		lineChart.setCreateSymbols(false);

		for (int n = 0; n < allDataValues.get(xIndex).length; n++) {
			String s = (allDataValues.get(xIndex)[n]).toString();
			for (int i : yIndexes) {
				seriesObjectsArray.get(i).getData().add(new XYChart.Data(s, allDataValues.get(i)[n]));
			}

		}

		for (XYChart.Series o : seriesObjectsArray) {
			lineChart.getData().add(o);
		}
		UniversalGraphingUtility.scene = new Scene(lineChart, 800, 600);
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