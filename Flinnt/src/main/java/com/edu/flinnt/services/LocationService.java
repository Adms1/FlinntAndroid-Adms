package com.edu.flinnt.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.edu.flinnt.core.UpdateUserLocation;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.FlinntLocationCallBack;
import com.edu.flinnt.util.FusedLocationUtil;
import com.edu.flinnt.util.LogWriter;

/**
 * Created by flinnt-android-3 on 1/9/16.
 */
public class LocationService extends Service implements FlinntLocationCallBack{

    public static final String IS_NEW_USER_LOGGED_IN = "is_new_user_logged_in";
    private FusedLocationUtil fusedLocationUtil;
    private Intent intent ;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        fusedLocationUtil = new FusedLocationUtil(this,this);
        if(fusedLocationUtil.isLocationFetchingAllowed()){
            fusedLocationUtil.onResume();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLocationChanged(Location location) {
        LogWriter.write("In side of onLocationChanged : "+location.toString());
        updateLocationOnServer(location);
        fusedLocationUtil.onPause();
        stopSelf();
    }

    private void updateLocationOnServer(Location location) {
        if(!shouldUpdateOnServer( location))
            return;

        Config.setStringValue(Config.USER_LOCATION_LATITUDE,location.getLatitude()+"");
        Config.setStringValue(Config.USER_LOCATION_LONGITUDE,location.getLongitude()+"");

        UpdateUserLocation updateUserLocation = new UpdateUserLocation(this, location);
        updateUserLocation.sendUpdateUserLocationRequest();
    }

    private double UPDATE_THRESHOLD = 500;

    private boolean shouldUpdateOnServer(Location location) {

        if(intent == null){
            return false;
        }

        if(intent.hasExtra(IS_NEW_USER_LOGGED_IN)){
            if(intent.getBooleanExtra(IS_NEW_USER_LOGGED_IN,false)){
                return true;
            }
        }

        double latitude = 0.0;
        double longitude = 0.0;

        try{
            latitude = Double.parseDouble(Config.getStringValue(Config.USER_LOCATION_LATITUDE));
            longitude = Double.parseDouble(Config.getStringValue(Config.USER_LOCATION_LONGITUDE));
            UPDATE_THRESHOLD = Config.getIntValue(Config.DISTANCE_VARIATION_THRESHOLD);
        }catch(Exception e){
            e.printStackTrace();
        }

        if(latitude == 0.0 || longitude==0.0){
            return true;
        }

        if(distance(location.getLatitude(),location.getLongitude(),latitude,longitude)<=UPDATE_THRESHOLD){
            return false;
        }

        return true;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist * 1000);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
