package com.edu.flinnt.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.gui.PickDocFileActivity;
import com.edu.flinnt.util.LogWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for folder picker
 */
public class PickDocFolderAdapter extends BaseAdapter {

    private final Activity _activity;
    private ArrayList<String> _filePaths = new ArrayList<String>();
    private final int imageWidth;
    public Map<Integer, View> myViews = new HashMap<Integer, View>();
    TextView foldername;
    Activity activity;
    ImageView imageView;
    private static LayoutInflater inflater;

    public PickDocFolderAdapter(Activity activity, ArrayList<String> filePaths, int imageWidth) {
        _activity = activity;
        _filePaths = filePaths;
        this.imageWidth = imageWidth;
        PickDocFolderAdapter.inflater = (LayoutInflater) this._activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        View vi = null;
        try {

            vi = this.myViews.get(position);

            if (vi == null) {
                vi = PickDocFolderAdapter.inflater.inflate(R.layout.pick_doc_folder_item, null);

                this.imageView = (ImageView) vi.findViewById(R.id.imageView1);
                this.foldername = (TextView) vi.findViewById(R.id.foldername);

                this.ShowDocs(position);
                this.myViews.put(position, vi);
            }
            //return mSectionOptionsImg;
        } catch (Throwable t) {
            //Log.e("folder getView Throwable", this._filePaths.get(position));
        }
        return vi;
    }


    public void ShowDocs(int position) {
        try {
            String a[];
            String url = this._filePaths.get(position);
            a = url.split("/");

            String FileName = a[a.length - 1];
            String FolderName = a[a.length - 2];
            this.foldername.setText(FolderName);
            Resources res = _activity.getResources();
            int id = R.drawable.attachment_pdf;
            Bitmap bmp = BitmapFactory.decodeResource(res, id);

            Bitmap image = PickDocFolderAdapter.decodeFile(this._filePaths.get(position), this.imageWidth, this.imageWidth);

            this.imageView.setScaleType(ScaleType.CENTER_CROP);
            //mSectionOptionsImg.setLayoutParams(new GridView.LayoutParams(imageWidth,	imageWidth));
            this.imageView.setLayoutParams(new LayoutParams(this.imageWidth, this.imageWidth));
            this.imageView.setImageBitmap(bmp);
            this.imageView.setTag(url.replace("/" + FileName, ""));

            // image view click listener
            this.imageView.setOnClickListener(new OnImageClickListener(position));

        } catch (Throwable t) {

        }
    }


    class OnImageClickListener implements View.OnClickListener {

        int _postion;

        // constructor
        public OnImageClickListener(int position) {
            _postion = position;
        }

        @Override
        public void onClick(View v) {
        	
            Intent i1 = new Intent(PickDocFolderAdapter.this._activity, PickDocFileActivity.class);
            //Log.e("v.getTag().toString()",v.getTag().toString());
            //i1.putExtra("MyAllVal", MyAllVal);
            //i1.putExtra("File_type", String.valueOf(GridViewImageFolderAdapter.this.Filetype));
            i1.putExtra("folderuri", v.getTag().toString());

            PickDocFolderAdapter.this._activity.startActivityForResult(i1, 300);
        }

    }

    /*
     * Resizing image size
     */
    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {

            File f = new File(filePath);

            Options o = new Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            int REQUIRED_WIDTH = WIDTH;
            int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            Options o2 = new Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            LogWriter.err(e);
        }
        return null;
    }

}
