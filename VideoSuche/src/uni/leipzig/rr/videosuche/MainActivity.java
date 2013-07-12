package uni.leipzig.rr.videosuche;

import java.io.*;
import java.net.*;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	String SAVEPATH; 
	Thread sT, clientThread;
	Socket clientSocket;
	int filesize;
	int bytesRead, current;
	String JSONString;
	String resultString;
	JsonReader jreader;
	JSONObject jOkbject, jso;
	JSONArray jsoARR;
	EditText[] eText;
	ListView ergebnisse;
	PrintStream irgendeinname;
	ArrayAdapter<String> ada;
	
	public void suchen(View view) {

		sT = new Thread(new Runnable() {
			@Override
			public void run() {
				Log.v("AudioSuche", "Speicherort: " + SAVEPATH);
				Looper.prepare();
				// TODO Auto-generated method stub
				try {
					Long zahl = Long.MAX_VALUE;
					clientSocket = new Socket("127.0.0.1", 5000);
					Log.i("AudioSuche", "1");
					jOkbject.put("Titel", eText[0].getText());
					Log.i("AudioSuche", "2");
					jOkbject.put("Kommentar", eText[1].getText());
					Log.i("AudioSuche", "3");

					if (!eText[2].getText().toString().isEmpty()) {
						zahl = Long.parseLong(eText[2].getText().toString());
						zahl = zahl * 8388608;
					}
					Log.i("AudioSuche", "Blödsinn" + zahl.toString());
					jOkbject.put("Groesse", zahl.toString());
					Log.i("AudioSuche", "4");

					irgendeinname = new PrintStream(clientSocket
							.getOutputStream());
					Log.i("AudioSuche", "AN CLIENT " + jOkbject.toString());
					irgendeinname.println(jOkbject.toString());

					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()));
					String str = bufferedReader.readLine();
					Log.v("AudioSuche", "VOM SERVER:" + str);

					if (str.equals("null")) {
						Toast.makeText(MainActivity.this,
								"Kein Ergebnis. Such anständig",
								Toast.LENGTH_SHORT).show();
					} else {
						Message nachr = Message.obtain();
						jso = new JSONObject(str);
						String s = jso.getString("Songs");
						s = s.substring(2, s.length());
						s = s.substring(0, s.length() - 2);
						nachr.obj = s.toString();
						mHandler.sendMessage(nachr);
					}
					clientSocket.close();
					Log.v("AudioSuche", "ClientSocket closed");
				} catch (Exception ex) {
					Log.e("AudioSuche", "ERROR: " + ex.toString());
					Toast.makeText(MainActivity.this, ex.toString(), 1)
							.show();
				}
			}
		});
		sT.start();
	}

	// Uebergibt die Nachricht
	Handler mHandler = new Handler() {
		public void handleMessage(Message nachricht) {
			Log.v("AusgabeSongs", nachricht.obj.toString());
			String[] data = nachricht.obj.toString().split("\",\"");
			loescheAnzeige();
			for (String string : data) {
				aktualisiereAnzeige(string);
			}

		}
	};

	/**
	 * Aktualisiert den Spinner und nach jedem Hinzufuegen wird diese in der
	 * Datei gespeichert somit wuerde ein Systemabsturz noch wenigstens noch ein
	 * paar Daten besitzen
	 */
	public void aktualisiereAnzeige(String serverNachricht) {
		// Speichert den String als erstes Element des Spinners
		Log.v("AudioSuche", "Servernachricht:" + serverNachricht);
		String s = serverNachricht;
		s = s.replace("\\", "");
		Log.v("AudioSuche", "FILE PATH:" + s);
		ada.insert(s, 0);
		// Speichert Spinnerdaten
		// speichereDatei();
	}

	/**
	 * Aktualisiert den Spinner und nach jedem Hinzufuegen wird diese in der
	 * Datei gespeichert somit wuerde ein Systemabsturz noch wenigstens noch ein
	 * paar Daten besitzen
	 */
	public void loescheAnzeige() {
		// Speichert den String als erstes Element des Spinners
		ada.clear();
		// Speichert Spinnerdaten
		// speichereDatei();
	}

	public void whereAreMySettings(View view) {
		Intent newIntent = new Intent(this, MainSettings.class);
		startActivity(newIntent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			File f = new File(SAVEPATH + "/pfad.txt");
			BufferedReader buff = new BufferedReader(new FileReader(f));
			SAVEPATH = buff.readLine();
			buff.close();
		} catch (Exception ex) {
			Log.w("AudioSuche", "Keine Datei am angegebenen Speicherort. (Beim ersten Lauf normal)");
			SAVEPATH = Environment.getExternalStorageDirectory().getPath();
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		eText = new EditText[3];
		eText[0] = (EditText) findViewById(R.id.eTsavePath);
		eText[1] = (EditText) findViewById(R.id.editText2);
		eText[2] = (EditText) findViewById(R.id.editText3);
		
		SAVEPATH = Environment.getExternalStorageDirectory().getPath();
		Log.v("AudioSuche", "Speicherort: " + SAVEPATH);
	
		
		for (EditText e : eText) {
			e.setSelectAllOnFocus(true);
		}
		
		jOkbject = new JSONObject();
		
		ada = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		ergebnisse = (ListView) findViewById(R.id.listView1);
		ergebnisse.setAdapter(ada);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
