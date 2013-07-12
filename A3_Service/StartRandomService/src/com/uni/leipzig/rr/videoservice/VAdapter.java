package com.uni.leipzig.rr.videoservice;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
/**
 * Erstellen der View 
 *
 */
public class VAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;
 
	/**
	 * Konstruktor des VAdapter
	 * @param context
	 * @param values
	 */
	public VAdapter(Context context, String[] values) {
		super(context, R.layout.activity_video, values);
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.activity_video, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
		textView.setText(values[position]);
 
		// Change icon based on name
		String s = values[position];
		// Erstelle Bitmap
		Bitmap curThumb = ThumbnailUtils.createVideoThumbnail(s, MediaStore.Video.Thumbnails.MINI_KIND);
		imageView.setImageBitmap(curThumb);

 
		return rowView;
	}
}