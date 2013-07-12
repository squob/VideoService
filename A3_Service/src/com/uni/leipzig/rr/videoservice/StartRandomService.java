package com.uni.leipzig.rr.videoservice;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StartRandomService extends ListActivity {
	//Deklaration der TAG Variable für Log.Cat
	private static final String TAG = "MainActivity";
	// Liste mit allen Musikdati Elementen
	private List<String> item = null;
	// Liste des Suchpfades
	private List<String> pfad = null;
	// Variable zum vergleich des Rootverzeichnis
	private String aktuellesVerzeichnis;
	// Listeview zur Anzeige
	private TextView tvListe;
	// Iterator Variable der Liste
	private int pos = 0;
	// Klassenvariable für den Pfad
	private String setPath = "";

	/**
	 * Getter-Methode für den Pfad
	 * @return gibt den Aktuellen Pfad zurueck
	 */
	public String getSetPath() {
		return setPath;	
	}

	/**
	 * Setter-Methode für den Pfad
	 * @param setPath
	 */
	public void setSetPath(String setPath) {
		this.setPath = setPath;
	}
	/**
	 * Getter Methode für die Listenposition
	 * @return gibt die Positon der Liste zurück
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * Setter-Methode der Position
	 * @param pos setzt die Position der Liste
	 */
	public void setPos(int pos) {
		this.pos = pos;
	}

	@Override
	/**
	 * Beim erstellen der Activity 
	 * wird die Liste mit dem ROOT Verzeichnis der SD Carde ausgegeben und
	 * das Verzeichnis der SD Card ausgelesen
	 */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);


		 startService(new Intent(this, VideoService.class));
		 startService(new Intent(this, DownloadService.class));
		
		// Erstellen der Listview
		tvListe = (TextView) findViewById(R.id.path);
		try {
			// List das Grundverzeichnis der SD Card als Start Verzeichnis
			aktuellesVerzeichnis = Environment.getExternalStorageDirectory().getPath();
			// Verzecihnis auslesne
			leseVerzeichnis(aktuellesVerzeichnis);

		} catch (Exception e) {
			// TODO: handle exception
		}


	
		
	}

	/**
	 * Sucht das Verzeichnis nach Files und Dateien und Zeigt diese an bzw. 
	 * gibt sie an die List
	 * @param verzeichnis liest das Verzeichnis aus auf dem Gesucht werden soll
	 */
	private void leseVerzeichnis(String verzeichnis) {
		// Zeigt in der tvListe 
		tvListe.setText(verzeichnis);
		// setzen des Pfades 
		this.setSetPath(verzeichnis);
		// erstellen der ArrayListen
		item = new ArrayList<String>();
		pfad = new ArrayList<String>();
		// Einlesen des Verzeichnisses 
		// Und pruefen des Verzeichnisses
		File f = new File(verzeichnis);
		File[] files = f.listFiles();

		if (!verzeichnis.equals(aktuellesVerzeichnis)) {
			item.add(aktuellesVerzeichnis);
			pfad.add(aktuellesVerzeichnis);
			item.add("../");
			pfad.add(f.getParent());
		}
		// Alphabethische Sortierung der Liste
		Arrays.sort(files, filecomparator);

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			// Pruefen der Datei auszulesen
			if (!file.isHidden() && file.canRead()) {
				pfad.add(file.getPath());
				if (file.isDirectory()) {
					item.add(file.getName() + "/");
				} else {
					item.add(file.getName());
				}
			}
		}
		// setzen der Liste
		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
				R.layout.row, item);
		setListAdapter(fileList);
	}
	// Pruefen auf FILE Ordner und Dateien
	Comparator<? super File> filecomparator = new Comparator<File>() {
		@SuppressLint("DefaultLocale")
		public int compare(File file1, File file2) {

			if (file1.isDirectory()) {
				if (file2.isDirectory()) {
					return String.valueOf(file1.getName().toLowerCase())
							.compareTo(file2.getName().toLowerCase());
				} else {
					return -1;
				}
			} else {
				if (file2.isDirectory()) {
					return 1;
				} else {
					return String.valueOf(file1.getName().toLowerCase())
							.compareTo(file2.getName().toLowerCase());
				}
			}

		}
	};
	/**
	* @param  view auf welche view gearbeitet wird
	*/
	public void onClickButton(View view) {
		Intent nextScreen = new Intent(getApplicationContext(),
				VideoActivity.class);

		// Intent mit den Daten füllen
		String str = this.getSetPath();
		nextScreen.putExtra("path", str);
		// Log schreiben für Logausgabe
		//Log.e("n", pfad.toString());

		// Intent starten und zur zweiten Activity wechseln
		startActivity(nextScreen);

		// inish(); 
	}
	
	/**
	* Startet die Metaverwaltung, die aktuelle View bleibt offen
	* @param view uebergibt die aktuelle View
	*/
	public void MetaVerwaltung(View view) {
		Intent nextScreen = new Intent(getApplicationContext(),
				Meta.class);
		startActivity(nextScreen);
	}

	@Override
	/**
	* Neuer onclick Listener für die ListView
	* Nach Klicken der auf einen Eintrag erscheint ein AlterDialog und 
	* über einen Toast wird der Pfad bzw. ein Video gestartet
	*/
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		File file = new File(pfad.get(position));
		if (file.isDirectory()) {
			if (file.canRead()) {
				this.setSetPath(pfad.get(position));
				this.setPos(position);
				leseVerzeichnis(pfad.get(position));
				// Testen bzgl. Übergabe
				// new AlertDialog.Builder(this)
				// .setIcon(R.drawable.ic_launcher)
				// .setTitle("[" +str + "]")
				// .setPositiveButton("OK", null).show();
			} else {
				new AlertDialog.Builder(this)
						.setIcon(R.drawable.ic_launcher)
						.setTitle(
								"[" + file.getName()
										+ "] folder can't be read!")
						.setPositiveButton("OK", null).show();
			}
		}
	}
}
