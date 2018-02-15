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

	public UniversalGraphingUtility() {
		NetworkTableInstance.getDefault().startClient("roboRIO-500-FRC.local");// localhost;
		PIDTunerTable = NetworkTableInstance.getDefault().getTable("PIDTuner"); // roboRIO-500-FRC.local;//
																				// roboRIO-502-FRC.local
	}

	public static void runGraph() {
		try {// Network tables access is slow you must delay 3 seconds to give it a chance to
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
			// do nothing for right now
		}

		String[] dummyNameArray = new String[3];
		dummyNameArray[0] = "Motor Output";
		dummyNameArray[1] = "Timestamp";
		dummyNameArray[2] = "Position";

		String[] allNTNames = PIDTunerTable.getEntry("All Key Names").getStringArray(dummyNameArray);

		/*
		 * Double[] PTMotor =
		 * PIDTunerTable.getEntry(allNTNames[0]).getDoubleArray(clearArray); Double[]
		 * PTTimestamp =
		 * PIDTunerTable.getEntry(allNTNames[1]).getDoubleArray(clearArray); Double[]
		 * PTEncoder = PIDTunerTable.getEntry(allNTNames[2]).getDoubleArray(clearArray);
		 */

		Double[] PTMotor = new Double[100];
		Double[] PTTimestamp = new Double[100];
		Double[] PTEncoder = new Double[100];

		for (int i = 0; i < 100; i++) {
			PTMotor[i] = i * i * 1.0;
			PTTimestamp[i] = i * 0.1;
			PTEncoder[i] = 0.0;
		}

		// Set properties for graph window

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Time");
		final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

		lineChart.setTitle("FF503 PID Tuning");
		lineChart.setCreateSymbols(false);

		ArrayList<XYChart.Series> seriesObjectsArray = new ArrayList<XYChart.Series>();

		XYChart.Series output = new XYChart.Series();

		output.setName(allNTNames[0]);

		XYChart.Series position = new XYChart.Series();
		position.setName(allNTNames[2]);

		// there was a problem on this line
		for (int n = 0; n < PTTimestamp.length; n++) { // Graphs the points
			String s = (PTTimestamp[n]).toString();
			output.getData().add(new XYChart.Data(s, PTMotor[n]));
			position.getData().add(new XYChart.Data(s, PTEncoder[n]));
		}
		seriesObjectsArray.add(output);
		seriesObjectsArray.add(position);

		UniversalGraphingUtility.scene = new Scene(lineChart, 800, 600);

		lineChart.getData().add(seriesObjectsArray.get(0));
		lineChart.getData().add(seriesObjectsArray.get(1));

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