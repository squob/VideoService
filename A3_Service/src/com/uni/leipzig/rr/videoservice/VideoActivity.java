package com.uni.leipzig.rr.videoservice;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
 
public class VideoActivity extends ListActivity {
	private static final String TAG = "VideoActivity";
	private ArrayList<String> liste = new ArrayList<String>();

	public ArrayList<String> getFiles() {
		return liste;
	}

	public void addFiles(String files) {
		this.liste.add(files);
	}
	
	String[] videoFileList;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent meinIntent = getIntent();
		String verzName = meinIntent.getStringExtra("path");
		
		ArrayList<File> files = suchePfade(new File(verzName),
				new ArrayList<File>());
		// Ausfiltern 
		mp4Filter(files);

		videoFileList = new String[this.getFiles().size()];
		for (int i = 0; i < this.getFiles().size(); i++) {
			videoFileList[i] = this.getFiles().get(i).toString();
		}
		// Setzen des Adapters
		setListAdapter(new VAdapter(this, videoFileList));
	}
	
	/**
	 * Pickt aus einer ArrayListe nur die MP4 Pfade raus 
	 * @param dateiListe 
	 */
	public void mp4Filter(ArrayList<File> dateiListe){
	if (dateiListe == null)
		return;
	try {

		for (int i = 0; i < dateiListe.size(); i++) {
			if (dateiListe.get(i).getCanonicalPath().endsWith(".MP4")) {
				 Log.v(TAG,"Auswertung: " +
				 dateiListe.get(i).getCanonicalPath());
				// bei einem Treffer wird das in die ArrayListe liste
				// gespeichert.
				this.addFiles(dateiListe.get(i).getCanonicalPath());
			}
		}
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
	/**
	 * Spielt die Datei im Media Player ab
	 * @param ort Pfad zur Datei
	 */
	public void startPlayer(String ort){
		 Uri intentUri = Uri.parse(ort);		    
	     Intent intent = new Intent();
	     intent.setAction(Intent.ACTION_VIEW);
	     intent.setDataAndType(intentUri, "video/mp4");
	     startActivity(intent);
	}
	
	/**
	 * Bei jedem Klick in die Liste wird der Player gestartet
	 * mit der Datei zudem wird ein Toast ausgerufen da in der VM
	 * der Player nicht immer funktoiniert
	 */	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
 
		//get selected items
		String selectedValue = (String) getListAdapter().getItem(position);
		startPlayer(selectedValue);
		Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
		
	}

	/**
	 * Rekursive Suche aller Files
	 * @param datei Datei zur Suche
	 * @param list mit den Dateien
	 * @return
	 */
	private static ArrayList<File> suchePfade(File datei, ArrayList<File> list) {
		if (datei == null || list == null || !datei.isDirectory())
			return null;
		File[] fileArr = datei.listFiles();
		for (File f : fileArr) {
			if (f.isDirectory()) {
				suchePfade(f, list);
			}
			list.add(f);
		}
		return list;
	}
}