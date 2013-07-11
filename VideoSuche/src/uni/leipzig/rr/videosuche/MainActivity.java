package uni.leipzig.rr.videosuche;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SecondThread sT = new SecondThread();
		sT.run();
	}
	
	public class SecondThread extends Thread {
		public void run() {
			Intent intent = new Intent(getApplicationContext(), GestureActivity.class);
			startActivity(intent);	
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
