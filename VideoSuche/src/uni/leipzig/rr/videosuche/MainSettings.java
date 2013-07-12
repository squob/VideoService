package uni.leipzig.rr.videosuche;

import java.util.zip.Inflater;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainSettings extends Activity{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
	}
	
	
	public void saveAndBack(View view) {
		this.finish();
	}
}
