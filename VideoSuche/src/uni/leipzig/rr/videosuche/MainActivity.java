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
					clientSocket = new Socket("127.0.0.1", 5000);
					Log.i("debug2", "1");
					jOkbject.put("Titel", eText[0].getText());
					Log.i("debug2", "2");
					jOkbject.put("Kommentar", eText[1].getText());
					Log.i("debug2", "3");
					
					Long zahl = Long.parseLong(eText[2].getText().toString());
					zahl = zahl * 8388608;
					Log.i("Blˆdsinn", zahl.toString());
					jOkbject.put("Groesse", zahl);
					Log.i("debug2", "4");
					
					irgendeinname = new PrintStream(clientSocket
							.getOutputStream());
					Log.i("debug2", jOkbject.toString());
					irgendeinname.println(jOkbject.toString());

					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()));
					String str = bufferedReader.readLine();
					Log.v("AusgabeSongs", "VOM SERVER:" + str);
					
					if (str.equals("null")) {
						Toast.makeText(MainActivity.this, "Kein Ergebnis. Such anst‰ndig", Toast.LENGTH_SHORT).show();
					}
					else {
						Message nachr = Message.obtain();
						jso = new JSONObject(str);
						String s = jso.getString("Songs");
						s = s.substring(2, s.length());
						s = s.substring(0, s.length() -2);
						String[] sa = s.split("\",\"");
						
						Log.v("AusgabeSongs", s);
	
						nachr.obj = s.toString();
						mHandler.sendMessage(nachr);
					}
					clientSocket.close();
					Log.v("FINISH", "ClientSocket closed");
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
				Log.v("AusgabeSongs",nachricht.obj.toString());
				String[] data = nachricht.obj.toString().split("\",\"");
				loescheAnzeige();
				for (String string : data) {
					aktualisiereAnzeige(string);					
				}
				
				
			}
		};
		/**
		 * Aktualisiert den Spinner und nach jedem Hinzufuegen wird diese in der Datei gespeichert 
		 * somit wuerde ein Systemabsturz noch wenigstens noch ein paar Daten besitzen
		 */
		public void aktualisiereAnzeige(String serverNachricht) {
			// Speichert den String als erstes Element des Spinners 
			ada.insert(serverNachricht, 0);
			// Speichert Spinnerdaten
			//speichereDatei();
		}	
		/**
		 * Aktualisiert den Spinner und nach jedem Hinzufuegen wird diese in der Datei gespeichert 
		 * somit wuerde ein Systemabsturz noch wenigstens noch ein paar Daten besitzen
		 */
		public void loescheAnzeige() {
			// Speichert den String als erstes Element des Spinners 
			ada.clear();
			// Speichert Spinnerdaten
			//speichereDatei();
		}

	/*
	 * Mit jedem Buttonklick wird die Verbindung zum Server aufgebaut und dem
	 * Client wird eine Zufallszahl uebergeben anschliessend wird die Zahl lokal
	 * in der Datei hinzugefuegt
	 * 
	 * @param view
	 */
//	public void Start(View view) {
//		clientThread = new Thread(new Runnable() {
//			/**
//			 * Starten des Threads, hierbei wird aus Port 5000 des Localhost
//			 * gelauscht
//			 */
//			public void run() {
//				try {
//					clientSocket = new Socket("127.0.0.1", 5001);
//					Message serverMessage = Message.obtain();
//					PrintStream raus = new PrintStream(
//							clientSocket.getOutputStream());
//					raus.println(""); // Gewuenschtes Objekt
//
//					// receive file
//					byte[] bytearray = new byte[filesize];
//					InputStream is = clientSocket.getInputStream();
//					FileOutputStream fos = new FileOutputStream(
//							"/mnt/sdcard/Pictures+/Troja-Tr.MP4");
//					BufferedOutputStream bos = new BufferedOutputStream(fos);
//					bytesRead = is.read(bytearray, 0, bytearray.length);
//					current = bytesRead;
//
//					do {
//						bytesRead = is.read(bytearray, current,
//								(bytearray.length - current));
//						if (bytesRead >= 0)
//							current += bytesRead;
//					} while (bytesRead > -1);
//
//					bos.write(bytearray, 0, current);
//					bos.flush();
//					bos.close();
//					clientSocket.close();
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		clientThread.start();
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		eText = new EditText[3];
		eText[0] = (EditText) findViewById(R.id.editText1);
		eText[1] = (EditText) findViewById(R.id.editText2);
		eText[2] = (EditText) findViewById(R.id.editText3);

		eText[0].setText("Suche nach Titel");
		eText[1].setText("Suche nach Kommentar");
		eText[2].setText("Maximale Groeﬂe");
		for (EditText e : eText) {
			e.setSelectAllOnFocus(true);
		}
		jOkbject = new JSONObject();
		
		ada = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1);
		ergebnisse = (ListView)findViewById(R.id.listView1);
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
