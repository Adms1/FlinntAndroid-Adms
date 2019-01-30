package com.edu.flinnt.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.util.LogWriter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * mContentsAdapter for file picker
 */
public class PickDocFileAdapter extends BaseAdapter {

    private final Activity _activity;
    private ArrayList<String> _filePaths = new ArrayList<String>();
    private final int imageWidth;
    private TextView foldername;
    private ImageView imageView;
    private static LayoutInflater inflater;
    public Map<Integer, View> myViews = new HashMap<Integer, View>();

    public PickDocFileAdapter(Activity activity, ArrayList<String> filePaths, int imageWidth) {
        _activity = activity;
        _filePaths = filePaths;
        
        this.imageWidth = imageWidth;
        PickDocFileAdapter.inflater = (LayoutInflater) this._activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return _filePaths.size();
    }

    @Override
    public Object getItem(int position) {
    	if ( position >= 0 && position < _filePaths.size() ) {
        return _filePaths.get(position);
    	}
    	else return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View vi = null;//convertView;

        vi = this.myViews.get(position);
        if (vi == null) {
            vi = PickDocFileAdapter.inflater.inflate(R.layout.pick_doc_file_item, null);

            this.imageView = (ImageView) vi.findViewById(R.id.imageView1);
            this.foldername = (TextView) vi.findViewById(R.id.FileName_Audio);

            this.ShowDocs(position);
            this.myViews.put(position, vi);

        }
        return vi;
    }


    public void ShowDocs(int position) {
        String a[];
        String url = this._filePaths.get(position);
        a = url.split("/");

        String FileName = a[a.length - 1];

        Resources res = _activity.getResources();
        int id = R.drawable.attachment_pdf;
        Bitmap bmp = BitmapFactory.decodeResource(res, id);

        this.imageView.setScaleType(ScaleType.CENTER_CROP);
        // mSectionOptionsImg.setLayoutParams(new GridView.LayoutParams(imageWidth,
        // imageWidth));
        this.imageView.setLayoutParams(new LayoutParams(this.imageWidth, this.imageWidth));
        this.imageView.setImageBitmap(bmp);
        this.imageView.setTag(url);
        this.foldername.setTag(url);

        try {
            File file = new File(url);
            String Filesise = "";
            double length = file.length();
            DecimalFormat d = new DecimalFormat("#.##");
            if (length > 1024) {
                length = length / 1024;

                if (length > 1024) {
                    length = length / 1024;

                    if (length > 1024) {
                        length = length / 1024;

                        if (length > 1024) {
                            length = length / 1024;

                            Filesise = Double.valueOf(d.format(length)) + " Tb";
                        } else {
                            Filesise = Double.valueOf(d.format(length)) + " Gb";
                        }
                    } else {
                        Filesise = Double.valueOf(d.format(length)) + " Mb";
                    }
                } else {
                    Filesise = Double.valueOf(d.format(length)) + " Kb";
                }
            } else {
                Filesise = Double.valueOf(d.format(length)) + " bytes";
            }

            this.foldername.setText(FileName + " | " + Filesise + "");
            // image view click listener
            this.imageView.setOnClickListener(new OnImageClickListener(position));
            this.foldername.setOnClickListener(new OnImageClickListener(position));
        }catch (Exception e){
            LogWriter.err(e);
        }
    }

    public void ShowDocs1(int position) {

        String a[];
        String url = this._filePaths.get(position);
        this.imageView.setTag(url);
        this.foldername.setTag(url);
        a = url.split("/");

        String FileName = a[a.length - 1];
        // String FolderName = a[a.length-2];

        Resources res = _activity.getResources();
        int id = R.drawable.attachment_pdf;
        Bitmap bmp = BitmapFactory.decodeResource(res, id);

        this.imageView.setScaleType(ScaleType.CENTER_CROP);
        this.imageView.setLayoutParams(new LayoutParams(this.imageWidth, this.imageWidth));
        this.imageView.setImageBitmap(bmp);
        this.imageView.setTag(url.replace("/" + FileName, ""));
        this.foldername.setText(FileName);
        // image view click listener
        this.imageView.setOnClickListener(new OnImageClickListener(position));
        this.foldername.setOnClickListener(new OnImageClickListener(position));

    }


    class OnImageClickListener implements View.OnClickListener {

        int _postion;

        // constructor
        public OnImageClickListener(int position) {
            _postion = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", v.getTag().toString());
            PickDocFileAdapter.this._activity.setResult(Activity.RESULT_OK, returnIntent);
            PickDocFileAdapter.this._activity.finish();
        }

    }

}
