package com.geneva.hotel.starling.starlinghotelgenevatechsupport.classes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geneva.hotel.starling.starlinghotelgenevatechsupport.R;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Daniel on 09.04.2015.
 */



public class CustomListViewAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public CustomListViewAdapter(Context context, List<String> values) {
        super(context, R.layout.list_single, values);
        this.context = context;
        this.values = values;
    }
    public int getCount() {
        return values.size()/3;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_single, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.txt);
        TextView descView = (TextView) rowView.findViewById(R.id.desc);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        if(position*3+3<=values.size()){
            if(descView != null){
                descView.setText(values.get(position*3));
            }
            textView.setText(values.get(position*3+1));
            new DownloadImageTask(imageView)
                    .execute("http://212.243.48.10/support_technique/images/"+values.get(position*3+2));


        }






        // change the icon for Windows and iPhone
        String s = values.get(position);
        //if (s.startsWith("iPhone")) {
            //imageView.setImageResource(R.drawable.no);
        //} else {
            //imageView.setImageResource(R.drawable.ok);
        //}

        return rowView;
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

