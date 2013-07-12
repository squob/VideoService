package uni.leipzig.rr.videosuche;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

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
					Toast.makeText(getApplicationContext(), ex.toString(), 1)
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		eText = new EditText[3];
		eText[0] = (EditText) findViewById(R.id.eTsavePath);
		eText[1] = (EditText) findViewById(R.id.editText2);
		eText[2] = (EditText) findViewById(R.id.editText3);

		// eText[0].setText("Suche nach Titel");
		// eText[1].setText("Suche nach Kommentar");
		// eText[2].setText("Maximale Groeße");
		for (EditText e : eText) {
			e.setSelectAllOnFocus(true);
		}
		jOkbject = new JSONObject();

		ada = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		ergebnisse = (ListView) findViewById(R.id.listView1);
		ergebnisse.setAdapter(ada);
		// Intent intent = new Intent(getApplicationContext(),
		// GestureActivity.class);
		// startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Thread sT;
	// GestureLibrary gestureLib;
	// String JSONString;
	// String resultString;
	// JSONObject jOkbject;
	// EditText eText;
	// CheckBox cBoxTitel;
	// CheckBox cBoxKommentar;

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// super.onCreate(savedInstanceState);
	//
	// eText = (EditText)findViewById(R.id.editText1);
	// eText.setText("Hier Suchbegriffe eingeben..");
	// eText.setSelectAllOnFocus(true);
	// cBoxTitel = (CheckBox)findViewById(R.id.suchTitel);
	// cBoxKommentar = (CheckBox)findViewById(R.id.suchKommentar);
	//
	// GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
	// View inflate = getLayoutInflater().inflate(R.layout.activity_main, null);
	// Toast.makeText(getApplicationContext(), "CREATED!", 0).show();
	// gestureOverlayView.setGestureColor(Color.rgb(0, 255, 0));
	// gestureOverlayView.setGestureVisible(false);
	// gestureOverlayView.setUncertainGestureColor(Color.rgb(0, 0, 255));
	//
	// gestureOverlayView.addView(inflate);
	// gestureOverlayView.addOnGesturePerformedListener(this);
	//
	// gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
	//
	// if(!gestureLib.load()) {
	// Log.i("debug", "failed");
	// }
	// else {
	// Log.i("debug","loaded");
	// }
	//
	// setContentView(gestureOverlayView);
	// Log.i("debug","onCreate");
	//
	// }

	// @Override
	// public void onGesturePerformed(GestureOverlayView arg0, Gesture g) {
	// // TODO Auto-generated method stub
	// ArrayList<Prediction> predictions = gestureLib.recognize(g);
	//
	// if (predictions.size() > 0) {
	// Prediction prediction= predictions.get(0);
	//
	// if (prediction.score > 1) {
	// String s = prediction.name;
	// if (s.equals("left")) {
	// Toast.makeText(getApplicationContext(), "links", 0).show();
	// }
	// else if (s.equals("right")) {
	// Toast.makeText(getApplicationContext(), "rechts", 0).show();
	// }
	// }
	//
	// }
	// }

}
