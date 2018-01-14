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
	@Override public void start(Stage stage) {
		double [] clearArray = new double[100];
		for(i=0;i<100;i++) {
			clearArray[i]=0.0;
		}
		//Set up Network tables Connection 
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress("roboRIO-500-FRC.local");//localhost //roboRIO-500-FRC.local //roboRIO-502-FRC.local
		NetworkTable table = NetworkTable.getTable("PIDTuner");
			
		//Network tables access is slow you must delay 3 seconds to give it a chance to access 
		// your requested table - without it you get zero values 
		try {
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
		//do nothing for right now 
		}
			
		double[] ptTimestamp = table.getNumberArray("PTTimestamp", clearArray);
		double[] ptMotor = table.getNumberArray("PTMotor", clearArray);  
		double[] ptEncoder = table.getNumberArray("PTEncoder", clearArray);  

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
		  	      
	      
		for(i=0;i<100;i++){
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