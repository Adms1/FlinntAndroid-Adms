package com.edu.flinnt.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.edu.flinnt.R;
import com.github.kayvannj.permission_utils.Func;
import com.github.kayvannj.permission_utils.Func2;
import com.github.kayvannj.permission_utils.PermissionUtil;

/**
 * Created by Nikhil Prajapati on 20062018
 */
public class AskPermition {
    private static Context mContext;
    private PermissionUtil.PermissionRequestObject mALLPermissionRequest;
    private static final AskPermition ourInstance = new AskPermition();

    public static AskPermition getInstance(Context context) {
        mContext = context;
        return ourInstance;
    }

    private AskPermition() {
    }

    public boolean isLocationAllowed() {
        boolean b = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int hasPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasPermission1 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasPermission == PackageManager.PERMISSION_GRANTED && hasPermission1 == PackageManager.PERMISSION_GRANTED) {
                b = true;
            } else {
                b = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    String[] allPermission = {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    };

                    mALLPermissionRequest = PermissionUtil.with((AppCompatActivity) mContext).request(allPermission).onResult(
                            new Func2() {
                                @Override
                                protected void call(int requestCode, String[] permissions, int[] grantResults) {

                                }
                            }).onAllGranted(new Func() {
                        @Override
                        protected void call() {

                        }
                    }).ask(101);
                } else {
                    showAlert();
                }
            }
        } else {
            b = true;
        }
        return b;
    }

    public boolean isPermitted() {
        boolean b = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasPermission2 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasPermission3 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);


            if (hasPermission == PackageManager.PERMISSION_GRANTED && hasPermission2 == PackageManager.PERMISSION_GRANTED && hasPermission3 == PackageManager.PERMISSION_GRANTED) {
                b = true;
            } else {
                b = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    String[] allPermission = {Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    };

                    mALLPermissionRequest = PermissionUtil.with((AppCompatActivity) mContext).request(allPermission).onResult(
                            new Func2() {
                                @Override
                                protected void call(int requestCode, String[] permissions, int[] grantResults) {

                                }
                            }).onAllGranted(new Func() {
                        @Override
                        protected void call() {

                        }
                    }).ask(101);
                } else {
                    showAlert();
                }
            }
        } else {
            b = true;
        }
        return b;
    }

    public boolean isStoragePermitted() {

        //@Chirag 06/08/2018 for storage access

        boolean b = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasPermission2 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);


            if (hasPermission == PackageManager.PERMISSION_GRANTED && hasPermission2 == PackageManager.PERMISSION_GRANTED ) {
                b = true;
            } else {
                b = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    String[] allPermission = {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    };

                    mALLPermissionRequest = PermissionUtil.with((AppCompatActivity) mContext).request(allPermission).onResult(
                            new Func2() {
                                @Override
                                protected void call(int requestCode, String[] permissions, int[] grantResults) {

                                }
                            }).onAllGranted(new Func() {
                        @Override
                        protected void call() {

                        }
                    }).ask(101);
                } else {
                    showAlert();
                }
            }
        } else {
            b = true;
        }
        return b;
    }


    private void showAlert() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle("Permission Required");
        alert.setMessage("Please click on Permissions and click \"on\" all permission to work Flinnt app work as intended");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                intent.setData(uri);
                ((Activity) mContext).startActivity(intent);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void RequestAllPermission() {
        String[] allPermission = {Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_SMS,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.RECEIVE_SMS};

        mALLPermissionRequest = PermissionUtil.with((AppCompatActivity) mContext).request(allPermission).onResult(
                new Func2() {
                    @Override
                    protected void call(int requestCode, String[] permissions, int[] grantResults) {

                    }
                }).onAllGranted(new Func() {
            @Override
            protected void call() {

            }
        }).ask(101);


    }

}
