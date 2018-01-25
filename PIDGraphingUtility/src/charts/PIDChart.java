package charts;

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
	int i;
	int pointsToGraph;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override 
	public void start(Stage stage) {
		Double [] clearArray = new Double[pointsToGraph];
		for(i = 0; i < pointsToGraph;i++) {
			clearArray[i]=0.0;
		}
		
		NetworkTableInstance.getDefault().startClient("roboRIO-500-FRC.local");//localhost //roboRIO-500-FRC.local //roboRIO-502-FRC.local		
		NetworkTable table = NetworkTableInstance.getDefault().getTable("PIDTuner");
		
		try {//Network tables access is slow you must delay 3 seconds to give it a chance to access your requested table 
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
		//do nothing for right now 
		}
		
		
		Double[] ptError = table.getEntry("PTError").getDoubleArray(clearArray);
		Double[] ptTimestamp = table.getEntry("PTTimestamp").getDoubleArray(clearArray);
		Double[] ptMotor = table.getEntry("PTMotor").getDoubleArray(clearArray);
		Double[] ptEncoder =table.getEntry("PTEncoder").getDoubleArray(clearArray);
		Number numberOfPoints = table.getEntry("NumberOfPoints").getNumber(0);
		pointsToGraph = numberOfPoints.intValue();
		Number ptSetpoint = table.getEntry("setpoint").getNumber(0);
		
		Number[] setPointArray = new Number[pointsToGraph];
		
		
		
		
		for(int i = 0; i<pointsToGraph; i+=1) {
			 setPointArray[i] = ptSetpoint;

		}
		
		
		//Set properties for graph window
		stage.setTitle("FF503 PID Tuning | Written by James Chen and Areeb Rahim");
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Time");
		final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
		 
		lineChart.setTitle("FF503 PID Tuning");
		lineChart.setCreateSymbols(false);							   
		

		
		XYChart.Series output = new XYChart.Series();
		output.setName("Motor Output");		  
		
		XYChart.Series angle = new XYChart.Series();
		angle.setName("Angle");
		
		XYChart.Series setpoint = new XYChart.Series();
		setpoint.setName("Setpoint");
		
		for(i=0;i<pointsToGraph;i++){ //Graphs the points
			String s=Double.toString(ptTimestamp[i]);
			output.getData().add(new XYChart.Data(s, ptMotor[i]));
			angle.getData().add(new XYChart.Data(s, ptEncoder[i]));
			setpoint.getData().add(new XYChart.Data(s, setPointArray[i]));
		}
		
		
		
		Scene scene  = new Scene(lineChart,800,600);	   
		lineChart.getData().addAll(output, angle, setpoint);
		 
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}