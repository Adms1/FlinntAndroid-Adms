package com.edu.flinnt.gui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by flinnt-android-3 on 4/4/17.
 */
public class QRCodeScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    public static String QR_CODE_PREFIX = "flinnt:course#";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mScannerView = new ZXingScannerView(this);
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(list);
        setContentView(mScannerView);


    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(final Result rawResult) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (rawResult != null) {
                    if (rawResult.getText() != null && !TextUtils.isEmpty(rawResult.getText())) {
                        if (!rawResult.getText().contains(QR_CODE_PREFIX)) {
                            showAlertMessage(QRCodeScannerActivity.this, getString(R.string.error), getString(R.string.invalid_qr_code), getString(R.string.ok));
                            return;
                        }
                        if (!Helper.isConnected()) {
                            showNetworkAlertMessage(QRCodeScannerActivity.this);
                            return;
                        }

                        Intent intent = new Intent(QRCodeScannerActivity.this, BrowseCourseDetailActivity.class);
                        intent.putExtra(BrowsableCourse.ID_KEY, rawResult.getText().substring(rawResult.getText().indexOf('#') + 1));
                        startActivityForResult(intent, MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK);
                        finish();
                    }
                    return;
                }
            }
        });
    }

    /**
     * Builds and display dialog
     *
     * @param context       activity context
     * @param title         dialog title
     * @param message       dialog message
     * @param dialogBtnText dailog operation button text
     */
    private void showAlertMessage(final Context context, String title, String message, String dialogBtnText) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        //alertDialogBuilder.setTitle(title);
        //alertDialogBuilder.setTitle("Change Password");
        TextView titleText = new TextView(context);
        // You Can Customise your Title here
        titleText.setText(title);
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);

        // set dialog message
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(dialogBtnText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                mScannerView.resumeCameraPreview(QRCodeScannerActivity.this);
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));

    }

    /**
     * Displays message of network inavailibility
     * network availability
     *
     * @param context
     */
    private void showNetworkAlertMessage(final Context context) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        //alertDialogBuilder.setTitle(R.string.no_internet_conn_title_dialog);
        TextView titleText = new TextView(context);
        // You Can Customise your Title here
        titleText.setText(R.string.no_internet_conn_title_dialog);
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.no_internet_conn_message_dialog)
                .setCancelable(false)
                .setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            LogWriter.err(e);
                        }
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mScannerView.resumeCameraPreview(QRCodeScannerActivity.this);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));

    }
}
