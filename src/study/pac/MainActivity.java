package study.pac;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends Activity implements OnItemClickListener {
	private ListView listView;
	private String[] items;
	private static final int LABEL_DEMO = 0;
	private static final int PIE_CHART_DEMO = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initScreen();
	}
	
	private void initScreen() {
		items = new String[2];   // TODO: hard code
		items[PIE_CHART_DEMO] = "Pie chart";
		items[LABEL_DEMO] = "Label";
		
		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, items));
		listView.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
		case LABEL_DEMO:
			startActivity(new Intent(this, LabelDemoActivity.class));
			break;
			
		case PIE_CHART_DEMO:
			startActivity(new Intent(this, PieChartActivity.class));
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}