package charts;
import com.sun.istack.internal.logging.Logger;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
 
public class PIDChart extends Application {		
	int i;
	final int pointsToGraph = 500;
	
	@Override 
	public void start(Stage stage) {
		double [] clearArray = new double[pointsToGraph];
		for(i = 0; i < pointsToGraph;i++) {
			clearArray[i]=0.0;
		}
		
		//Sets up network tables connection. This stuff should be commented out when testing graph
		/*
		//NetworkTable.setClientMode();
		//NetworkTable.setIPAddress("roboRIO-500-FRC.local");//localhost //roboRIO-500-FRC.local //roboRIO-502-FRC.local
		//NetworkTable table = NetworkTable.getTable("PIDTuner");
		*/	
		
		
		
		//Network tables access is slow you must delay 3 seconds to give it a chance to access 
		// your requested table - without it you get zero values 
		try {
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
		//do nothing for right now 
		}
		
		
		
		//These are the actual array definitions from network tables
		/*
		double[] ptTimestamp = table.getNumberArray("PTTimestamp", clearArray);		
		double[] ptMotor = table.getNumberArray("PTMotor", clearArray);  
		double[] ptEncoder = table.getNumberArray("PTEncoder", clearArray);  
		
		*/
		
		
		//These are the dummy array definitions to test the graph
		double[] ptTimestamp = new double[pointsToGraph];	
		double[] ptMotor = new double[pointsToGraph];
		double[] ptEncoder = new double[pointsToGraph];

		
		
		
		for(int i = 0; i<pointsToGraph; i+=1) {
			ptTimestamp[i] = i;
			ptMotor[i] = 1.1*Math.pow(i, 2);
			ptEncoder[i] = Math.pow(i, 2);
		}
		
		
		
		stage.setTitle("Line Chart Sample");
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
		  	      
	      
		for(i=0;i<pointsToGraph;i++){ //i<100
			String s=Double.toString(ptTimestamp[i]);
			series1.getData().add(new XYChart.Data(s, ptMotor[i]));
			series2.getData().add(new XYChart.Data(s, ptEncoder[i]));
		}
	      
	   //   series1.getData().add(new XYChart.Data("Jan", 23));
	   //   series1.getData().add(new XYChart.Data("Feb", 14));
	   //   series1.getData().add(new XYChart.Data("Mar", 15));
	   //   series1.getData().add(new XYChart.Data("Apr", 24));
	   //   series1.getData().add(new XYChart.Data("May", 34));
	   //   series1.getData().add(new XYChart.Data("Jun", 36));
	   //   series1.getData().add(new XYChart.Data("Jul", 22));
	   //   series1.getData().add(new XYChart.Data("Aug", 45));
	   //   series1.getData().add(new XYChart.Data("Sep", 43));
	   //   series1.getData().add(new XYChart.Data("Oct", 17));
	   //   series1.getData().add(new XYChart.Data("Nov", 29));
	   //   series1.getData().add(new XYChart.Data("Dec", 25));
	      
	  //    XYChart.Series series2 = new XYChart.Series();
	  //    series2.setName("Portfolio 2");
	  //    series2.getData().add(new XYChart.Data("Jan", 33));
	  //    series2.getData().add(new XYChart.Data("Feb", 34));
	  //    series2.getData().add(new XYChart.Data("Mar", 25));
	  //    series2.getData().add(new XYChart.Data("Apr", 44));
	  //    series2.getData().add(new XYChart.Data("May", 39));
	  //    series2.getData().add(new XYChart.Data("Jun", 16));
	  //    series2.getData().add(new XYChart.Data("Jul", 55));
	  //    series2.getData().add(new XYChart.Data("Aug", 54));
	  //    series2.getData().add(new XYChart.Data("Sep", 48));
	  //    series2.getData().add(new XYChart.Data("Oct", 27));
	  //    series2.getData().add(new XYChart.Data("Nov", 37));
	  //    series2.getData().add(new XYChart.Data("Dec", 29));
	      
		Scene scene  = new Scene(lineChart,800,600);       
		lineChart.getData().addAll(series1, series2);
	     
		stage.setScene(scene);
		stage.show();
	}


	public static void main(String[] args) {
		launch(args);
	}

}