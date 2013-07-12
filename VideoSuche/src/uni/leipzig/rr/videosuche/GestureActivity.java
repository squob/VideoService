package uni.leipzig.rr.videosuche;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class GestureActivity extends Activity implements OnGesturePerformedListener {
	
	Thread sT;
	GestureLibrary gestureLib;
	String JSONString;
	String resultString;
	JSONObject jOkbject;
	EditText eText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		eText = (EditText)findViewById(R.id.editText1);
		eText.setText("Hier Suchbegriffe eingeben..");
		eText.setSelectAllOnFocus(true);
		
		GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
		View inflate = getLayoutInflater().inflate(R.layout.activity_main, null);
		Toast.makeText(getApplicationContext(), "CREATED!", 0).show();
		gestureOverlayView.setGestureColor(Color.rgb(0, 255, 0));
		gestureOverlayView.setGestureVisible(false);
		gestureOverlayView.setUncertainGestureColor(Color.rgb(0, 0, 255));
		
		gestureOverlayView.addView(inflate);
		gestureOverlayView.addOnGesturePerformedListener(this);
		
		gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		
		if(!gestureLib.load()) {
			Log.i("debug", "failed");
		}
		else {
			Log.i("debug","loaded");
		}
		
		setContentView(gestureOverlayView);
		Log.i("debug","onCreate");
		
	}
	
	
	public void suchen(View view) throws JSONException {
		//Toast.makeText(getApplicationContext(), "clicked.", 1).show();
		sT = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "checked.", 1).show();
//				try {
//					jOkbject.accumulate("Worte", eText.getText());
//					jOkbject.accumulate("Titel", cBoxTitel.isChecked());
//					jOkbject.accumulate("Kommentar", cBoxKommentar.isChecked());
//					JSONString = jOkbject.toString();
//				}
//				catch(JSONException ex) {
//					Log.i("debug", "Keine Daten bekommen.");
//				}
				//TODO Anfrage senden.
			}
		});
		sT.run();
	}
	
	@Override
	public void onGesturePerformed(GestureOverlayView arg0, Gesture g) {
		// TODO Auto-generated method stub
		ArrayList<Prediction> predictions = gestureLib.recognize(g);
		
		if (predictions.size() > 0) {
			Prediction prediction= predictions.get(0);
			
			if (prediction.score > 1) {
				String s = prediction.name;
				if (s.equals("left")) {
					Toast.makeText(getApplicationContext(), "links", 0).show();
				}
				else if (s.equals("right")) {
					Toast.makeText(getApplicationContext(), "rechts", 0).show();
				}
			}
			
		}
	}	
}
