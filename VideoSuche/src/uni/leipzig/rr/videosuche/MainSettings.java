package uni.leipzig.rr.videosuche;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainSettings extends Activity{
	
	String SAVEPATH;
	EditText et;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		et = (EditText)findViewById(R.id.eTsavePath);
		SAVEPATH = Environment.getExternalStorageDirectory().getPath();
		Log.v("ASSettings", "Speicherort: " + SAVEPATH);
	}
	
	
	public void saveAndBack(View view) throws IOException {
		
		File f = new File(SAVEPATH + "/pfad.txt");
		if (!f.createNewFile()) {
			f.delete();
			f.createNewFile();
		}
		FileWriter fw = new FileWriter(f);
		fw.write(et.getText().toString());
		fw.close();
		this.finish();
	}
}
