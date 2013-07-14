package uni.leipzig.rr.videosuche;

import java.io.*;
import java.net.*;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.LauncherActivity.ListItem;
import android.content.Intent;
import android.net.Uri;
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
	int filesize = 13348812;
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
	File song;
	FileOutputStream fw;

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
						zahl = zahl * 1048576;
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
				if(string !=""){
				aktualisiereAnzeige(string);
				}
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

//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		try {
//			BufferedReader buff = new BufferedReader(new FileReader(new File(
//					SAVEPATH + "/pfad.txt")));
//			SAVEPATH = buff.readLine();
//			buff.close();
//			Log.v("AUS","AUS DATEI:"+SAVEPATH);
//		} catch (Exception ex) {
//			Log.w("AudioSuche",
//					"Keine Datei am angegebenen Speicherort. (Beim ersten Lauf ist das normal)");
//			SAVEPATH = Environment.getExternalStorageDirectory().getPath();
//		}
//	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		if (arg0.getId() == R.id.listView1) {
			
			Log.v("AudioSuche", "Item wurde geklickt.");
					
			try {
				prepareDownload(arg2);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			startDownload(arg1);
			
		}
	}

	public void prepareDownload(int l) throws IOException {
		File Ordner = new File(SAVEPATH);
		Ordner.mkdir();
		ListView lv = (ListView) findViewById(R.id.listView1);
		ArrayAdapter<String> aa = (ArrayAdapter<String>) lv.getAdapter();
		downloadSong = aa.getItem(l);
		Log.v("Ausgelesen", downloadSong);
		downloadSongName = downloadSong
				.substring(downloadSong.lastIndexOf("/"));
		Log.v("Ausgelesen", "NAME TEY: " + downloadSongName);
		song = new File(SAVEPATH + downloadSongName);
	}

	public void startDownload(View view) {
		try {
			BufferedReader buff = new BufferedReader(new FileReader(new File(
					SAVEPATH + "/pfad.txt")));
			SAVEPATH = Environment.getExternalStorageDirectory().getPath()+File.separator;
			SAVEPATH += buff.readLine();
			buff.close();
			Log.v("AUS","AUS DATEI:"+SAVEPATH);
		} catch (Exception ex) {
			Log.v("AudioSuche",
					"Keine Datei am angegebenen Speicherort. (Beim ersten Lauf ist das normal)");
			SAVEPATH = Environment.getExternalStorageDirectory().getPath();
		
		}
		
		
		clientThread = new Thread(new Runnable() {
			/**
			 * Starten des Threads, hierbei wird aus Port 5000 des Localhost gelauscht
			 */
			
			public void run() {
				
				String str ="";
				try {
					clientSocket = new Socket("127.0.0.1", 5001);
					int filesize=6022386; // filesize temporary hardcoded

				    long start = System.currentTimeMillis();
				    int bytesRead;
				    int current = 0;
				    PrintStream raus = new PrintStream(
							clientSocket.getOutputStream());

					Log.v("Moechte ich", downloadSong);
					raus.println(downloadSong);
				    // localhost for testing
				    System.out.println("Connecting...");

				    // receive file
				    byte [] mybytearray  = new byte [filesize];
				    InputStream is = clientSocket.getInputStream();
				    str = SAVEPATH+downloadSongName;
				    Log.v("STR",str);
				    FileOutputStream fos = new FileOutputStream(str);
				    BufferedOutputStream bos = new BufferedOutputStream(fos);
				    bytesRead = is.read(mybytearray,0,mybytearray.length);
				    current = bytesRead;
				    do {
				       bytesRead =
				          is.read(mybytearray, current, (mybytearray.length-current));
				       if(bytesRead >= 0) current += bytesRead;
				    } while(bytesRead > -1);

				    bos.write(mybytearray, 0 , current);
				    bos.flush();
				    long end = System.currentTimeMillis();
				    System.out.println(end-start);
				    System.out.println("Fertig");
				    bos.close();
				    Log.v("FERTIG", "TADAAAA FERTIG");
				    clientSocket.close();

					 Log.v("PLAYER BEKOMMT:",str);
					 startPlayer(str);	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		clientThread.start();
		
	}
	
	public void startPlayer(String ort){
Uri intentUri = Uri.parse(ort);	
Intent intent = new Intent();
intent.setAction(Intent.ACTION_VIEW);
intent.setDataAndType(intentUri, "video/mp4");
startActivity(intent);
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
