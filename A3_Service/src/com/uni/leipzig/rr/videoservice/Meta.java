package com.uni.leipzig.rr.videoservice;

import java.util.ArrayList;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Meta ist eine kleine Bearbeitungsseite für die SQLite Datenbank
 * 
 */
public class Meta extends Activity implements OnItemSelectedListener {

	private static final String TAG = "Meta";
	private Spinner dynSpinner;
	private String aktuell = "";

	DBFunktionen db;
	EditText etGroesse;
	EditText etLeange;
	EditText etTitel;
	EditText etDimension;
	EditText etKomponisten;
	EditText etHash;
	
	/**
	 * Getter Methode für die aktuelle Datei
	 * @return Gibt aktuelle Datei zurück
	 */
	public String getAktuell() {
		return aktuell;
	}

	/**
	 * Setter Methode für den aktuell ausgewählte Datei
	 * @param aktuell setzt die aktuelle Datei
	 */
	public void setAktuell(String aktuell) {
		this.aktuell = aktuell;
	}

	

	/**
	 * Beim Erstellen der Activity wird die Spinner und der Button geladen
	 * anschliessend wird aus der Datei gelesen und die Informationen in die
	 * Spinner geladen
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meta);

		Intent service = new Intent(this, VideoService.class);
		this.startService(service);
		
		db = new DBFunktionen(this);  

		db.erstelleDB();
		db.schreibeMP4();
		// Liest alles Titel aus der DB
		
		schreibeFelder(db.leseTitel());
		Intent intent = new Intent(Meta.this,
				com.uni.leipzig.rr.videoservice.VideoService.class);
		// Startet den Service
		Meta.this.startService(intent);
	}

	
	/**
	 * Liest den Datensatz aus der Datei aus und speichert
	 * dessen Werte in die ETs
	 * @param ort wo die Datei liegt
	 */
	public void insertFelder(ArrayList<String> liste) {

	

						etGroesse = (EditText) findViewById(R.id.etGroesse);
						etLeange = (EditText) findViewById(R.id.etLaenge);
						etTitel = (EditText) findViewById(R.id.etTitel);
						etDimension = (EditText) findViewById(R.id.etDimension);
						etKomponisten = (EditText) findViewById(R.id.etKomponisten);
						etHash = (EditText) findViewById(R.id.etHash);

//						Log.v(TAG, "LISTE:"+liste.toString());
						
						etTitel.setText(liste.get(0));
						etDimension.setText(liste.get(1));
						etHash.setText(liste.get(2));
						etLeange.setText(liste.get(3));
						etGroesse.setText(liste.get(4));
						etKomponisten.setText(liste.get(5));
						etDimension.setEnabled(false);
						etLeange.setEnabled(false);
						etGroesse.setEnabled(false);
						etHash.setEnabled(false);


	}
	
	
	/**
	 * Liest den Datensatz aus der Datei aus und speichert
	 * dessen Werte in die ETs
	 * @param ort wo die Datei liegt
	 */
	public void schreibeFelder(ArrayList<String> felder) {
		//Pruefen ob der Wert nicht leer ist falls doch wird 
//		Log.v("#####",felder.toString());
		// Hinzufügen der Spinner
				dynSpinner = (Spinner) findViewById(R.id.spAnzeige);
				
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, felder);
				dataAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				dynSpinner.setAdapter(dataAdapter);
				dynSpinner.setOnItemSelectedListener(this);
				insertFelder(db.leseDatensatz(felder.get(felder.size()-1)));		
	
	}


	public void zurueck(View view) {
		db.beendeVerbindung();
		finish(); // Zurück zur Main Seite

	}


	/**
	 * Speichert den aktuellen aktiven Eintrag in der DB
	 * 
	 * @param view
	 *            aktuelle View
	 */
	public void speicher(View view) {
		
		db.speicher(getAktuell(), etTitel.getText().toString(), etKomponisten.getText().toString());

	}


	/**
	 * Löscht den aktuell Ausgewählten Eintrag aus der Datenbank und liest
	 * danach den Spinner neu ein
	 * 
	 * @param view aktuelle View
	 */

	public void loesche(View view) {

//		Log.v("-----",getAktuell() + "wird gelöscht");
		if (getAktuell() != "") {
			db.deleteDB(getAktuell());
			// Log.v(TAG, getAktuell() + " wird gelöscht");
			schreibeFelder(db.leseTitel());
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, db.leseTitel());
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			dynSpinner.setAdapter(dataAdapter);
			dynSpinner.setOnItemSelectedListener(this);
			
		} else {
			// Log.v(TAG, "spinner ist leer");
		}
	}

	
	/**
	 * OnItemSelected aktualisiert den aktuellen ausgewählten Eintrag zum
	 * Editieren
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// Setzen der Aktuellen Position des ausgewählten Elements
		setAktuell(parent.getItemAtPosition(pos).toString());
//		 Log.v(TAG,"AKTUELL:>>>>>"+getAktuell());
		insertFelder(db.leseDatensatz(getAktuell()));
			
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

}
