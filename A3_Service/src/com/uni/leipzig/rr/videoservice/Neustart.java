package com.uni.leipzig.rr.videoservice;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;

/**
 * Startet den Service nach Booten des Android Geraets durch die onReceive
 * Methode
 */
public class Neustart extends BroadcastReceiver {
	/**
	 * Die OnRevieve Methode wird aufgerufen wenn der Bootvorgang abgeschlossen
	 * ist und stellt sicher, dass der Service startet
	 */
	public void onReceive(Context context, Intent intent) {
		// Pruefen ob der Bootvorgang vollstaendig abgeschlossen ist
		if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
			// Starten des Services
			Intent serviceIntent = new Intent(context, VideoService.class);
			context.startService(serviceIntent);
		}
	}
}