package com.uni.leipzig.rr.videoservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Paint.Join;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
@SuppressLint("NewApi")
public class DBFunktionen extends SQLiteOpenHelper {

	// Define the version and database file name
	private static final int DB_VERSION = 1;
	private static final String TAG = "DBHELPER";
	private final static String DATENBANK = "TrailerDatenbank";
	private final static String TableName = "MP4DB5";


	private SQLiteDatabase db;

	// Constructor to simplify Business logic access to the repository
	public DBFunktionen(Context context) {

		super(context, DATENBANK, null, DB_VERSION);
		// Android will look for the database defined by DB_NAME
		// And if not found will invoke your onCreate method
		this.db = this.getWritableDatabase();
		onCreate(db);

	}

	public void insertDB(String ort, String titel, String dimension,
			String hash, String laenge, String groesse, String details) {
		try {

			/* Insert data to a Table */
			db.execSQL("INSERT INTO "
					+ TableName
					+ " (Ort, Titel ,Dimension , Hash , Laenge , Groesse, Details )"
					+ " VALUES ('" + ort + "','" + titel + "','" + dimension
					+ "','" + hash + "','" + laenge + "','" + groesse + "','"
					+ details + "');");
		} catch (Exception e) {
			// Log.e("Error", "Error", e);
		}
	}

	/**
	 * Schreibt alle Metadaten der MP4 Dateien
	 */
	public void schreibeMP4() {
		String verzName = Environment.getExternalStorageDirectory().getPath();

		ArrayList<File> files = suchePfade(new File(verzName),
				new ArrayList<File>());
		if (files == null)
			return;
		try {

			for (int i = 0; i < files.size(); i++) {
				if (files.get(i).getCanonicalPath().endsWith(".MP4")) {
					metaDaten(files.get(i).getCanonicalPath());
				}
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Erstellt die MetaDaten und speichert diese in die DB
	 * 
	 * @param ort
	 *            Pfad zur Datei von der die Meta Daten ausgeselesen werden
	 *            sollen
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	@SuppressLint("NewApi")
	public void metaDaten(String ort) {
		// Bsp Werte :"/mnt/sdcard/Movies/Troja-Tr.MP4"
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(ort);
		String albumName = mmr
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

		String height = mmr
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
		String width = mmr
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);

		String sDauer = mmr
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		long dauer = Integer.parseInt(sDauer);
		String sekunden = String.valueOf(dauer % 60);
		String minuten = String.valueOf(dauer / 60000);
		File file = new File(ort);
		long groesse = file.length();
		String kommentar = mmr
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);
//		Log.v("EINFÜGEN", ort + " " + albumName + " " + groesse + " byte" + " "
//				+ kommentar);
		insertDB(ort, albumName, height + "x" + width, md5Hash(albumName),
				minuten + "min" + sekunden + "sec", groesse + " byte",
				kommentar);

	}

	/**
	 * Berechnet den Hashwert einer Datei
	 * 
	 * @param ort
	 *            Pfad zur Datei von der der Hashwert berechnet werden soll
	 * @return gibt Hashwert zurueck
	 */
	public String md5Hash(String ort) {
		// Erstellen eines FileInputStreams für die Datei
		File file = new File(ort);
		byte[] buffer = new byte[(int) file.length()];
		try {
			FileInputStream fis = new FileInputStream(file);
			fis.read(buffer);
			fis.close();

		} catch (IOException e1) {

		}
		// Java MessageDigest berechnet den MD5
		MessageDigest m = null;

		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
		}
		// Formatiert den Hashwert mit 16
		m.update(buffer);
		String hash = new BigInteger(1, m.digest()).toString(16);
		return hash;
	}

	/**
	 * Rekursive suche nach allen Dateien auf der SD Card
	 * 
	 * @param file
	 *            Pfad auf dem gesucht wird
	 * @param list
	 *            Speichert die Zwischenfiles aus den Verzeichnissen
	 * @return gibt eine Arrayliste mit allen Dateienzurück
	 */
	private ArrayList<File> suchePfade(File file, ArrayList<File> list) {
		try {
			if (file == null || list == null || !file.isDirectory())
				return null;
			File[] fileArr = file.listFiles();
			for (File f : fileArr) {
				if (f.isDirectory()) {
					suchePfade(f, list);
				}
				list.add(f);
			}
		} catch (Exception e) {
		}

		return list;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TableName
				+ " (Ort VARCHAR,Titel VARCHAR,Dimension VARCHAR , Hash VARCHAR, Laenge VARCHAR, Groesse VARCHAR, Details VARCHAR);");
		Log.v(TAG, "WURDE ERSTELLT DIE DATENBNAK");

		schreibeMP4();

	}

	/**
	 * liest alle Speicherorte aus der DB aus und speichert diese im Spinner
	 */
	public JSONObject leseAlleMP4() {
		// Hinzufügen der Spinner
		ArrayList<String> list = new ArrayList<String>();
		String titel = "";
		try {
			// Lesen aller Datenbankeinträge
			Cursor c = db.rawQuery("SELECT DISTINCT Titel FROM " + TableName,
					null);
			// Speichert die Speicherungsorte in die ArrayList
			if (c != null) {
				int Column1 = c.getColumnIndex("Titel");
				c.moveToFirst();
				do {
					titel = c.getString(Column1);
					// Log.v(TAG, "AUS DB:" + ort);
					list.add(titel);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			// Log.e("Error", "Error", e);

		}

		return schreibeJSON(list);

	}

	/**
	 * liest alle Speicherorte aus der DB aus und speichert diese im Spinner
	 */
	public JSONObject pruefeAnfrage() {
		// Hinzufügen der Spinner
		ArrayList<String> list = new ArrayList<String>();
		String titel = "";
		try {
			// Lesen aller Datenbankeinträge
			Cursor c = db.rawQuery("SELECT DISTINCT Titel FROM " + TableName,
					null);
			// Speichert die Speicherungsorte in die ArrayList
			if (c != null) {
				int Column1 = c.getColumnIndex("Titel");
				c.moveToFirst();
				do {
					titel = c.getString(Column1);
					// Log.v(TAG, "AUS DB:" + ort);
					list.add(titel);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			// Log.e("Error", "Error", e);

		}

		return schreibeJSON(list);
	}

	public JSONObject dbAbfrageJSON(JSONObject jso) {
		ArrayList<String> list = new ArrayList<String>();
		String sWhereClausel = "";
		String titel = "";
		String details = "";
		String groesse = "";

		JSONObject jObject = jso;

		try {
			titel = jObject.getString("Titel");
			details = jObject.getString("Kommentar");
			groesse = jObject.getString("Groesse");
			Log.v("JSONOBJEKT IST", "OBJEKT:" + titel + " " + details + " "
					+ groesse);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String sGroesse = "";
		String sTitel = "";
		String sDetails = "";
		if (groesse != "" || titel != "" && details != "") {
			sWhereClausel += " WHERE ";
			// Log.v(TAG, "WHERE WIRD BENÖTIGT");
		}

		if (groesse == "") {
			// Log.v(TAG, "GROESSE:"+groesse);
		} else {
			sGroesse += " Groesse < " + groesse;
		}

		if (titel == "") {
			// Log.v(TAG, "TITEL:"+titel);
		} else {
			if (sGroesse != "") {
				sTitel += " AND ";
			}

			sTitel += " Titel LIKE '%" + titel + "%'";

			// Log.v(TAG, "TITEL:"+sTitel);
		}
		if (details == "") {
			// Log.v(TAG, "Details:"+sDetails);
		} else {
			if (sGroesse != "" || sTitel != "") {
				sGroesse += " AND ";
			}

			sGroesse += " Details LIKE '%" + details + "%'";

			// Log.v(TAG, "DEtails:"+sDetails);
		}

		Log.v(TAG, sWhereClausel + sGroesse + sTitel);
		try {
			Cursor c = db.rawQuery("SELECT DISTINCT Titel FROM " + TableName
					+ sWhereClausel + sGroesse + sTitel + ";", null);
			// Check if our result was valid.
			Log.v(TAG, "" + c.getColumnCount());
			if (c.getColumnCount() != 0) {
				Log.v(TAG, "KEIN TITEL GEFUNDEN IN DER DBS");
				list = null;
			} else if (c != null) {
				int Column1 = c.getColumnIndex("Titel");
				c.moveToFirst();
				do {
					titel = c.getString(Column1);
					list.add(titel);
					Log.v(TAG, "IST DA: " + "|" + titel);
					
					
				} while (c.moveToNext());
			}

		} catch (Exception e) {
			Log.e("Error", "Error", e);
		}
		return schreibeJSON(list);
	}


	public JSONObject schreibeJSON(ArrayList<String> list) {
		JSONArray jsa;
		JSONObject jso;
		if(list == null){
			jso = null;
		}else{
			
			jsa = new JSONArray();
			jso = new JSONObject();
			try {
				for (int i = 0; i < list.size(); i++) {
					jsa.put(list.get(i));
					// Log.v(TAG,list.get(i) + " eingefügt");
				}
				jso.put("Songs", jsa);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			// Log.v(TAG, j.toString() + " tadaaa");
		}
		
		return jso;
	}

	
	/**
	 * Erstellen der Datenbank
	 * falls sie noch nicht Existiert
	 */
	public void erstelleDB() {

		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TableName
				+ " (Ort VARCHAR,Titel VARCHAR,Dimension VARCHAR , Hash VARCHAR, Laenge VARCHAR, Groesse VARCHAR, Details VARCHAR);");

	}
	
	/**
	 * Löscht den aktuell Ausgewählten Eintrag aus der Datenbank und liest
	 * danach den Spinner neu ein
	 * 
	 * @param view aktuelle View
	 */

	
	
	public void loesche(String titel) {

		if (titel != "") {
			deleteDB(titel);
			// Log.v(TAG, getAktuell() + " wird gelöscht");
			leseTitel();
		} else {
			// Log.v(TAG, "spinner ist leer");
		}
	}

	/**
	 * Speichert den aktuellen aktiven Eintrag in der DB
	 * 
	 * @param view
	 *            aktuelle View
	 */
	public void speicher(String ort, String titel, String details) {
		try {
			ContentValues value;
			value = new ContentValues();
			value.put("Titel", titel);
			value.put("Details", details);
			db.update(TableName, value, " Ort='" + ort + "'", null);
		} catch (Exception e) {
			Log.e("Error", "Error", e);
		}

	}

	/**
	 * liest alle Speicherorte aus der DB aus und speichert diese im Spinner
	 */
	public ArrayList<String> leseTitel() {
		// Hinzufügen der Spinner
		ArrayList<String> list = new ArrayList<String>();
		String ort = "";
		try {
			// Lesen aller Datenbankeinträge
			Cursor c = db.rawQuery("SELECT DISTINCT Ort FROM " + TableName,
					null);
			// Speichert die Speicherungsorte in die ArrayList
			if (c != null) {
				int Column1 = c.getColumnIndex("Ort");
				c.moveToFirst();
				do {
					ort = c.getString(Column1);
					// Log.v(TAG, "AUS DB:" + ort);
					list.add(ort);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			// Log.e("Error", "Error", e);

		}
//		 Log.v(TAG, "AUS DB:" + ort);
		return list;
	}
	
	
	public ArrayList<String> leseDatensatz(String ort) {
		ArrayList<String> liste = new ArrayList<String>();
		if (ort != "") {

			try {
				String q = "SELECT * FROM " + TableName + " WHERE Ort = '"
						+ ort + "'";
				Cursor c = db.rawQuery(q, null);
				int Column1 = c.getColumnIndex("Titel");
				int Column2 = c.getColumnIndex("Dimension");
				int Column3 = c.getColumnIndex("Hash");
				int Column4 = c.getColumnIndex("Laenge");
				int Column5 = c.getColumnIndex("Groesse");
				int Column6 = c.getColumnIndex("Details");

				c.moveToFirst();
				if (c != null) {
					//Falls Werte vorhanden werden die ETs mit den Werten aus der
					//Datenbank befüllt und die 4 Mittleren werden auf Enabled gesetzt
//					Log.v(TAG, "SCHREIBE WERTE");
					do {
						String titel = c.getString(Column1);
						String dimension = c.getString(Column2);
						String hash = c.getString(Column3);
						String laenge = c.getString(Column4);
						String groesse = c.getString(Column5);
						String deatils = c.getString(Column6);
						liste.add(titel);
						liste.add(dimension);
						liste.add(hash);
						liste.add(laenge);
						liste.add(groesse);
						liste.add(deatils);
						

					} while (c.moveToNext());
				}
			} catch (Exception e) {
			}
		} else {
			liste = null;
		}
//		Log.v(TAG,"LISTE+++++++++:"+liste.toString());
		return liste;
	}

	/**
	 * Prueft ob ein Datensatz schon in der Datenbank vorhanden ist
	 * @param ort Pfad zum Video
	 * @param titel Titel der Datei
	 * @param dimension Auflösung des Videos
	 * @param hash Hashwert der Datei
	 * @param laenge Laenge des Videos
	 * @param groesse Groesse in Byte
	 * @param details Kommentarfeld was in Komponist gespeichert wird
	 * @return gibt true oder false zurueck
	 */
	public boolean datensatzVorhanden(String ort, String titel,
			String dimension, String hash, String laenge, String groesse,
			String details) {
		boolean ret = false;

		try {
			/* retrieve data from database */
			Cursor c = db.rawQuery("SELECT * FROM " + TableName
					+ " WHERE Ort ='" + ort + "';", null);
			// Check if our result was valid.
			c.moveToFirst();
			if (c != null) {
				Log.v(TAG, "Schon drin: " + "|" + ort + titel + "|" + dimension
						+ "|" + hash + "|" + laenge + "|" + groesse + "|"
						+ details);
				ret = true;
			}
		} catch (Exception e) {
			Log.e("Error", "Error", e);
		}
		return ret;
	}
	
	/**
	 * Beendet die Verbindung zur Datenbank
	 */
	public void beendeVerbindung() {

		if (db != null) {
			db.close();
		}

	}
	/**
	 * Löscht den Eintrag aus der Datenbank
	 * @param ort Ort in dem die Datei liegt
	 */
	public void deleteDB(String ort) {
		//Log.v(TAG, "Löschwert:" + ort);
		try {
			//löschen des Datenbank eintrags und aktualisieren der ETs
			db.execSQL("DELETE FROM " + TableName + " WHERE Ort ='" + ort
					+ "';");
			File file = new File(ort);
			file.delete();
			//	Log.v(TAG, ort + " wurde gelöscht");
		} catch (Exception e) {
			//	Log.e("Error", "Error", e);
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}
}