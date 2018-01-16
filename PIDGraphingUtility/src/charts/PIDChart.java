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

//import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class PIDChart extends Application {		
	int i;
	final int pointsToGraph = 100;
	
	@Override 
	public void start(Stage stage) {
		Double [] clearArray = new Double[pointsToGraph];
		for(i = 0; i < pointsToGraph;i++) {
			clearArray[i]=0.0;
		}
		
		//Sets up network tables connection. This stuff should be commented out when testing graph
		
		//NEW NETWORK TABLES CODE
		
		NetworkTableInstance.getDefault().startClient("roboRIO-500-FRC.local");//localhost //roboRIO-500-FRC.local //roboRIO-502-FRC.local		
		NetworkTable table = NetworkTableInstance.getDefault().getTable("PIDTuner");
			
		
		/*
		//OLD NETWORK TABLES CODE
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress("roboRIO-500-FRC.local");//localhost //roboRIO-500-FRC.local //roboRIO-502-FRC.local
		NetworkTable table = NetworkTable.getTable("PIDTuner");
		*/
		
		try {//Network tables access is slow you must delay 3 seconds to give it a chance to access your requested table 
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
		//do nothing for right now 
		}
		
		//These are the actual array definitions from network tables
		
		
		//NEW NETWORK TABLES CODE
		Double[] ptTimestamp =  (Double[]) table.getEntry("PTTimestamp").getNumberArray(clearArray);
		Double[] ptMotor = (Double[]) table.getEntry("PTMotor").getNumberArray(clearArray);
		Double[] ptEncoder = (Double[]) table.getEntry("PTEncoder").getNumberArray(clearArray);
		

		/*
		//OLD NETWORK TABLES CODE
		Double[] ptTimestamp = table.getNumberArray("PTTimestamp", clearArray);		
		Double[] ptMotor = table.getNumberArray("PTMotor", clearArray);  
		Double[] ptEncoder = table.getNumberArray("PTEncoder", clearArray);  
		*/
				
		//These are the dummy array d efinitions to test the graph WHEN THERE ARE NO NETWORK TABLES		
		/*	
		Double[] ptTimestamp = new Double[pointsToGraph];	
		Double[] ptMotor = new Double[pointsToGraph];
		Double[] ptEncoder = new Double[pointsToGraph];
		for(int i = 0; i<pointsToGraph; i+=1) {
			ptTimestamp[i] = (double) i;
			ptMotor[i] = (double) i;
			ptEncoder[i] = Math.pow(i, 2);
		}
		*/
		//Set properties for graph window
		stage.setTitle("FF503 PID Tuning | Written by James Chen and Areeb Rahim");
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Time");
		final LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis,yAxis);
		 
		lineChart.setTitle("FF503 PID Tuning");
		lineChart.setCreateSymbols(false);							   
		 
		XYChart.Series series1 = new XYChart.Series();
		series1.setName("Motor");		  
		
		XYChart.Series series2 = new XYChart.Series();
		series2.setName("Encoder");
		  		  		  
		for(i=0;i<pointsToGraph;i++){ //Graphs the points
			String s=Double.toString(ptTimestamp[i]);
			series1.getData().add(new XYChart.Data(s, ptMotor[i]));
			series2.getData().add(new XYChart.Data(s, ptEncoder[i]));
		}
		  
		Scene scene  = new Scene(lineChart,800,600);	   
		lineChart.getData().addAll(series1, series2);
		 
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}