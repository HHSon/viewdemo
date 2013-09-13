package study.pac;

import study.pac.customview.PieChart;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;


public class PieChartActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pie_chart);
		
		float data1[] = { 1, 2, 1, 1 };
		float data2[] = { 1, 1, 1 };
		float data3[] = { 1, 1, 1, 1, 1 };
		float data4[] = { 1, 2 };
		
		PieChart pieChart1 = (PieChart) findViewById(R.id.pieChart1);
		PieChart pieChart2 = (PieChart) findViewById(R.id.pieChart2);
		PieChart pieChart3 = (PieChart) findViewById(R.id.pieChart3);
		PieChart pieChart4 = (PieChart) findViewById(R.id.pieChart4);
		
		pieChart1.setData(data1);
		pieChart2.setData(data2);
		pieChart3.setData(data3);
		
		int[] colors4 = { 
				Color.rgb(20, 169, 0),
				Color.rgb(231, 207, 0)};
		pieChart4.setColors(colors4);
		pieChart4.setData(data4);
	}
}