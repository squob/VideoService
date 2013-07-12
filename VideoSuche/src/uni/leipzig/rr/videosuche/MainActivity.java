package uni.leipzig.rr.videosuche;

import java.io.*;
import java.net.*;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.LauncherActivity.ListItem;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener {

	String SAVEPATH;
	Thread sT, sT2, clientThread;
	Socket clientSocket;
	int filesize;
	int bytesRead, current;
	String JSONString;
	String resultString;
	JsonReader jreader;
	JSONObject jOkbject, jso;
	JSONObject jOkbject2, jso2;
	JSONArray jsoARR;
	EditText[] eText;
	ListView ergebnisse;
	PrintStream irgendeinname, irgendeinname2;
	ArrayAdapter<String> ada;
	String downloadSong;
	String downloadSongName;

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

					jOkbject.put("Titel", eText[0].getText());
					jOkbject.put("Kommentar", eText[1].getText());

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
					Toast.makeText(MainActivity.this, ex.toString(), 1).show();
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
			BufferedReader buff = new BufferedReader(new FileReader( new File(SAVEPATH + "/pfad.txt")));
			SAVEPATH = buff.readLine();
			buff.close();
		} catch (Exception ex) {
			Log.w("AudioSuche",
					"Keine Datei am angegebenen Speicherort. (Beim ersten Lauf ist das normal)");
			SAVEPATH = Environment.getExternalStorageDirectory().getPath();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		if (arg0.getId() == R.id.listView1) {
			Log.v("AudioSuche", "Item wurde geklickt.");
			startDownload(arg1, arg2);
		}
	}

	public void startDownload(View view, int l) {

		ListView lv = (ListView) findViewById(R.id.listView1);
		ArrayAdapter<String> aa = (ArrayAdapter<String>) lv.getAdapter();
		downloadSong = aa.getItem(l);
		Log.v("Ausgelesen", downloadSong);
		downloadSongName = downloadSong
				.substring(downloadSong.lastIndexOf("/"));
		Log.v("Ausgelesen", "NAME: " + downloadSongName);

		File Ordner = new File(SAVEPATH);
		Ordner.mkdir();

		sT2 = new Thread(new Runnable() {
			@Override
			public void run() {
				FileWriter fw;
				
				Looper.prepare();
				// TODO Auto-generated method stub
				try {

					clientSocket = new Socket("127.0.0.1", 5001);

					irgendeinname2 = new PrintStream(clientSocket
							.getOutputStream());
					Log.i("AudioSucheD", "AN CLIENT " + downloadSong);
					irgendeinname2.println(downloadSong);
					Log.v("AudioSucheD", "Speicherort: " + SAVEPATH + downloadSongName);
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()));
					String str = bufferedReader.readLine();
					File song = new File(SAVEPATH + downloadSongName);
					fw = new FileWriter(song);
					if (str.equals("null")) {
						Toast.makeText(MainActivity.this,
								"Kein Ergebnis. Such anständig",
								Toast.LENGTH_SHORT).show();
					} else {
						while (!str.equals("EOF")) {
							Log.v("AudioSucheD", "VOM SERVER:" + str);
							fw.write(str);
							str = bufferedReader.readLine();
						}
						fw.close();
					}
					clientSocket.close();
					Log.v("AudioSucheD", "ClientSocket closed");
				} catch (Exception ex) {
					Log.e("AudioSucheD", "ERROR: " + ex.toString());
				}
			}
		});
		sT2.start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		eText = new EditText[3];
		eText[0] = (EditText) findViewById(R.id.eTsavePath);
		eText[1] = (EditText) findViewById(R.id.editText2);
		eText[2] = (EditText) findViewById(R.id.editText3);

		ListView lview = (ListView) findViewById(R.id.listView1);
		lview.setOnItemClickListener(this);

		SAVEPATH = Environment.getExternalStorageDirectory().getPath();
		Log.v("AudioSuche", "Speicherort: " + SAVEPATH);

		for (EditText e : eText) {
			e.setSelectAllOnFocus(true);
		}

		jOkbject = new JSONObject();
		jOkbject2 = new JSONObject();

		ada = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		ergebnisse = (ListView) findViewById(R.id.listView1);
		ergebnisse.setAdapter(ada);
		downloadSong = "";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
