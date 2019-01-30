package com.edu.flinnt.util;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;


/**
 * Created by flinnt-android-3 on 29/8/16.
 */
public class FusedLocationUtil implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener , com.google.android.gms.location.LocationListener {

    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    // Constants for Google Location API
    private static final int INTERVAL = 2000;
    private static final int FASTESTINTERVAL = 1000;

    // Variables that hold location API/info/etc..
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = null;
    private Location mLastLocation = null;
    private boolean wantLocations = true;
    private boolean mGoogleError = false;
    protected LocationSettingsRequest mLocationSettingsRequest;
    private Activity mActivity;
    private Context mContext;

    FlinntLocationCallBack callBack;
    public FusedLocationUtil(Context context , FlinntLocationCallBack callBack){
        this.mContext = context;
        this.callBack = callBack;

        if (wantLocations) mGoogleApiClient = getGoogleApiClient();
    }

    private GoogleApiClient getGoogleApiClient() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(mContext.getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS || resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            return new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        return null;

    }

    public void onResume(){
        startLocationUpdates();
    }

    public void onPause(){
        stopLocationUpdates();
    }

    public boolean isLocationFetchingAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if(mActivity !=null){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            // Request location updates
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            // // This doesn't get updates in the genymotion emulator
            //mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setInterval(INTERVAL);
            mLocationRequest.setFastestInterval(FASTESTINTERVAL);
            fetchLocationWithSettings();
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        // Request location updates
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // // This doesn't get updates in the genymotion emulator
        //mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTESTINTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }



    private void startLocationUpdates() {
        if (!wantLocations) return;
        if (mGoogleError) return;
        // Get the client if we don't have it (such as if we are resuming)
        // If we do this, this will call connect for us
        if (mGoogleApiClient==null) mGoogleApiClient = getGoogleApiClient();
        if (mGoogleApiClient==null || mGoogleError) return;
        // If we have the client but it's not connected, then connect it.
        if (!mGoogleApiClient.isConnected())
            // Start the connection process which will start the location request
            mGoogleApiClient.connect();
        else
            onConnected(null);
    }

    // Stop location updates if we are currently getting them
    private void stopLocationUpdates() {
        if (wantLocations && mGoogleApiClient != null && mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        //if (mGoogleApiClient != null) mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        callBack.onLocationChanged(location);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mGoogleError) return;
        mGoogleError = true;
    }

    public void startLocationUpdatesWithSettings() {
        if (!wantLocations) return;
        if (mGoogleError) return;
        // Get the client if we don't have it (such as if we are resuming)
        // If we do this, this will call connect for us
        if (mGoogleApiClient==null) mGoogleApiClient = getGoogleApiClient();
        if (mGoogleApiClient==null || mGoogleError) return;
        // If we have the client but it's not connected, then connect it.
        if (!mGoogleApiClient.isConnected())
            // Start the connection process which will start the location request
            mGoogleApiClient.connect();
        else
            onConnected(null);
    }

    private void fetchLocationWithSettings(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
        ).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, FusedLocationUtil.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            status.startResolutionForResult(mActivity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }
}
