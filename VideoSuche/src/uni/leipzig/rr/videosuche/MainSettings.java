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
	
	// temporäre Variablen
	String SAVEPATH; 
	EditText et; 
	
	/**
	 * Beim Starten wird der Speicherort geladen.
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		et = (EditText)findViewById(R.id.eTsavePath);
		SAVEPATH = Environment.getExternalStorageDirectory().getPath();
		Log.v("ASSettings", "Speicherort: " + SAVEPATH);
	}
	
	/**
	 * Einzig interessante Funktion. Reagiert auf klick auf Speichern und Zurueck Button. 
	 * Es wird der gwünschte Speicherort erstellt und in der Pfad.txt gespeichert.
	 * 
	 * @param view Der aktuelle view
	 * @throws IOException Fehler beim abspeichern der Pfad.txt
	 */
	public void saveAndBack(View view) throws IOException {
		SAVEPATH = Environment.getExternalStorageDirectory().getPath();
		Log.v("ASSettings", "Speicherort: " + SAVEPATH+File.separator+et.getText() +"###########");
		File directory = new File(SAVEPATH+File.separator+et.getText() );
	    if(!directory.exists() && !directory.isDirectory()) 
	    {
	        if (directory.mkdirs())
	        {
	            Log.i("Erstelle Verzeichnis","Verzeichnis erstellt");
	        }
	        else
	        {
	            Log.w("Erstelle Verzeichnis","Verzeichnis konnte nicht erstellt werden");
	        }
	    }
	    else
	    {
	        Log.i("Erstelle Verzeichnis","Verzeichnis schon vorhanden");
	    }

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
