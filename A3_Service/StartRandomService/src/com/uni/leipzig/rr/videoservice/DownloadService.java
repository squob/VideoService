package com.uni.leipzig.rr.videoservice;

import java.io.BufferedReader;
import java.net.*;
import java.io.*;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;


import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.app.Service;
import android.content.Intent;

/***
 * Die Klasse RandomService erstellt den ZahlenServer und wird als Service
 * implementiert.
 */
public class DownloadService extends Service  {
	Thread serverThread;
	ServerSocket server;
	DBFunktionen db;
	/**
	 * Implementierung der Standard Service Methoden wird nicht veraendert
	 */
	
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Initialisiert mit Beginn des Serives den Server
	 */
	public void onCreate() {
		Log.v("TADA","Läuft");
		super.onCreate();
		db = new DBFunktionen(this);  
		serverThread = new Thread(new Runnable() {
			public void run() {

				try {
					server = new ServerSocket(5001);
					while (true) {
						Log.v("5001", "ARBEITET");
						Socket connectedSocket = server.accept();
						BufferedReader bufferedReader = 
					            new BufferedReader(
					                new InputStreamReader(
					                		connectedSocket.getInputStream()));
						String str = bufferedReader.readLine();
						if(!str.equals("EOF")){
//							Log.v("TAG","VOM CLIENT:"+str);

							JSONObject jso = new JSONObject(str);
//							Log.v("TAG","Das neue JSO:"+jso);
							if(db.dbAbfrageJSON(jso) == null){
								Log.v("JSON","JSON IST LEER");
							}
						}
						
						Log.v("TESTAD",db.leseAlleMP4().toString());

						PrintStream raus = new PrintStream(
								connectedSocket.getOutputStream());
						raus.println(schreibeTESTJSON());
						connectedSocket.close();
						
					}
				}
				// Ausgabe der Exception falls der Server nicht gestartet werden
				// kann
				catch (Exception e) {
					Message msg3 = Message.obtain();
					msg3.obj = e.getMessage();
				}
			}
		});

		// starten des Treads
		serverThread.start();
	}
	
	
	public void download() {
		
		final DBFunktionen db = new DBFunktionen(this);  
		serverThread = new Thread(new Runnable() {
			public void run() {

				try {
					// Verbindungsaufbau mit dem Client und uebergabe der
					// Zufallszahl
					server = new ServerSocket(5001);
					while (true) {
						Socket connectedSocket = server.accept();
						BufferedReader bufferedReader = 
					            new BufferedReader(
					                new InputStreamReader(
					                		connectedSocket.getInputStream()));
						String str = bufferedReader.readLine();
						Log.v("50001","50001 arbeitet");
						 // sendfile
					      File myFile = new File ("/mnt/sdcard/Pictures/Troja-Tr.MP4");
					      byte [] mybytearray  = new byte [(int)myFile.length()];
					      FileInputStream fis = new FileInputStream(myFile);
					      BufferedInputStream bis = new BufferedInputStream(fis);
					      bis.read(mybytearray,0,mybytearray.length);
					      OutputStream os = connectedSocket.getOutputStream();
					      System.out.println("Sending...");
					      os.write(mybytearray,0,mybytearray.length);
					      os.flush();
						connectedSocket.close();
						
					}
				}
				// Ausgabe der Exception falls der Server nicht gestartet werden
				// kann
				catch (Exception e) {
					Message msg3 = Message.obtain();
					msg3.obj = e.getMessage();
				}
			}
		});

		// starten des Treads
		serverThread.start();
	}
	public JSONObject schreibeTESTJSON() {

		JSONObject j = new JSONObject();

			try {
				j.put("Titel", "Wer ist denn Trojans");
				j.put("Kommentar", "");
				j.put("Groesse", "138983448");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
//		Log.v("TEST", j.toString() + " tadaaa");
		return j;
	}
	

	/**
	 * Generiert eine Zufallszahl zwischen 0 und 100
	 * 
	 * @return gibt Zufallszahl als int zurueck
	 */
	public int generate() {
		return new Random().nextInt(100);
	}
}