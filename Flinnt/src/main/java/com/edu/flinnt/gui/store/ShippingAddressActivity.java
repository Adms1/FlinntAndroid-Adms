package com.edu.flinnt.gui.store;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.store.PlacesAutocompleteAdapter;
import com.edu.flinnt.core.store.ManageUserAddress;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.store.AddUserAddressResponse;
import com.edu.flinnt.models.store.ShippingAdressModel;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.MyConfig;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.edu.flinnt.FlinntApplication.getContext;


public class ShippingAddressActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,AdapterView.OnItemClickListener {

    private Toolbar toolbar;
    private AppCompatSpinner stateSpinner;
    private String selectedStateId = "1",addressType = "Home";
    private HashMap<Integer,String> stateDataValues ;

    private TextInputEditText city,pin;
    private AutoCompleteTextView cityAutocomplete,stateAutoComplete;
    private AppCompatAutoCompleteTextView mAddress2AutoComplete,mAddress1AutoComplete;
    protected GeoDataClient mGeoDataClient;
    private PlacesAutocompleteAdapter mAdapter,mAdapter1,mAdapter2;
    private ManageUserAddress addUserAddress;
    private Handler mHandler;
    public static ProgressDialog mProgressDialog = null;
    private TextInputEditText inputfullName,inputMobileNo,inputlandMark;

    private RadioGroup rgAddressType;
    private RadioButton rbHome,rbAddress;
    private TextView btnCurrentLocation;

    private static final LatLngBounds BOUNDS_GREATER_IN = new LatLngBounds(new LatLng(0, 0), new LatLng(-0, 0));
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private double currentLat,currentLong;
    private ProgressBar progressBarLocation;
    private ObjectAnimator objectAnimator;
    private ArrayList<ShippingAdressModel.Datum> addressList;
    private boolean isRecordInUpdate;
    private String userAddresId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_shipping_address);

        addressList = new ArrayList<ShippingAdressModel.Datum>();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                           // LogWriter.write("SUCCESS_RESPONSE : " + msg.obj.toString());

                        if(msg.obj instanceof AddUserAddressResponse){
                            Toast.makeText(ShippingAddressActivity.this,"Address added successfully",Toast.LENGTH_SHORT).show();
                            Intent intentAddressList = new Intent(ShippingAddressActivity.this,ShippingAdreessListActivity.class);
                            startActivityForResult(intentAddressList,107);
                            finish();

                        }else if(msg.arg1 == 200){
                            Toast.makeText(ShippingAddressActivity.this,"Address updated successfully",Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                        break;

                    case Flinnt.FAILURE:
                        stopProgressDialog();
                        break;

                    default:
                        super.handleMessage(msg);
                }
            }
        };


        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        inputfullName = (TextInputEditText)findViewById(R.id.edt_fullname);
        inputMobileNo = (TextInputEditText)findViewById(R.id.edt_phone);
        inputlandMark = (TextInputEditText)findViewById(R.id.edt_landmark_addr);
        rgAddressType = (RadioGroup)findViewById(R.id.rg_addresstype);
        stateSpinner = (AppCompatSpinner) findViewById(R.id.state_spinner);
        cityAutocomplete = (AutoCompleteTextView)findViewById(R.id.input_city);
        stateAutoComplete =(AutoCompleteTextView)findViewById(R.id.input_state);
       // houseNo = (TextInputEditText)findViewById(R.id.edt_address1);
        btnCurrentLocation = (TextView)findViewById(R.id.btn_current_location);

        mAddress1AutoComplete = (AppCompatAutoCompleteTextView)findViewById(R.id.edt_address1);
        mAddress2AutoComplete = (AppCompatAutoCompleteTextView)findViewById(R.id.edt_address2);

        pin = (TextInputEditText)findViewById(R.id.edt_pin);

        rbHome = (RadioButton)findViewById(R.id.rb_home);
        rbAddress = (RadioButton)findViewById(R.id.rb_address);

        setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_drawer);
        getSupportActionBar().setTitle("Shipping Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();


        try {
            addressList = getIntent().getParcelableArrayListExtra("bundle_address_data");

            if(addressList != null){

                inputMobileNo.setText(String.valueOf(addressList.get(0).getPhone()));
                inputfullName.setText(String.valueOf(addressList.get(0).getFullname()));
                //inputlandMark.setText(String.valueOf(addressList.get(0).ge));
                cityAutocomplete.setText(String.valueOf(addressList.get(0).getCity()));
                stateAutoComplete.setText(String.valueOf(addressList.get(0).getName()));
                pin.setText(String.valueOf(addressList.get(0).getPin()));
                inputMobileNo.setText(String.valueOf(addressList.get(0).getPhone()));
                mAddress1AutoComplete.setText(String.valueOf(addressList.get(0).getAddress1()));
                mAddress2AutoComplete.setText(String.valueOf(addressList.get(0).getAddress2()));
                userAddresId = String.valueOf(addressList.get(0).getUserAddressId());

                isRecordInUpdate = true;

            }else{
                isRecordInUpdate = false;

            }

        }catch (Exception ex){
            ex.printStackTrace();
            isRecordInUpdate = false;

        }

        sendStateDataRequest();

        mGeoDataClient = Places.getGeoDataClient(this, null);

        progressBarLocation = (ProgressBar)findViewById(R.id.progress_bar);
        objectAnimator = ObjectAnimator.ofInt(progressBarLocation, "progress",progressBarLocation.getProgress(),100);

        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int progress = (int) valueAnimator.getAnimatedValue();
                progressBarLocation.setProgress(progress);
            }
        });

        AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry("IN").setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();
        mAdapter = new PlacesAutocompleteAdapter(this,mGeoDataClient,BOUNDS_GREATER_IN,filter);
        cityAutocomplete.setAdapter(mAdapter);
        cityAutocomplete.setOnItemClickListener(mAutocompleteClickListener);

//        AutocompleteFilter filter2 = new AutocompleteFilter.Builder().setCountry("IN").setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE).build();
//        mAdapter1 = new PlacesAutocompleteAdapter(this,mGeoDataClient,BOUNDS_GREATER_IN,filter2);
//        mAddress2AutoComplete.setAdapter(mAdapter1);
//        mAddress2AutoComplete.setOnItemClickListener(mAutocompleteClickListener1);


//        AutocompleteFilter filter2 = new AutocompleteFilter.Builder().setCountry("IN").setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).build();
//        mAdapter2 = new PlacesAutocompleteAdapter(this,mGeoDataClient,BOUNDS_GREATER_IN,filter2);
//        mAddress1AutoComplete.setAdapter(mAdapter2);
//        mAddress1AutoComplete.setOnItemClickListener(mAutocompleteClickListener2);

        AutocompleteFilter filter1 = new AutocompleteFilter.Builder().setCountry("IN").setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE).build();
        mAdapter1 = new PlacesAutocompleteAdapter(this,mGeoDataClient,BOUNDS_GREATER_IN,filter1);
        mAddress2AutoComplete.setAdapter(mAdapter1);
        mAddress2AutoComplete.setOnItemClickListener(mAutocompleteClickListener1);


        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarLocation.setVisibility(View.VISIBLE);
                btnCurrentLocation.setEnabled(false);
                btnCurrentLocation.setAlpha(0.5f);
                objectAnimator.start();
                settingCurrentLocationRequest();
            }
        });

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,View view,int position,long id) {
                selectedStateId = stateDataValues.get(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stateAutoComplete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedStateId = stateDataValues.get(i + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rgAddressType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(i);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                if(isChecked){
                    addressType = checkedRadioButton.getText().toString();
                }
            }
        });

        pin.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                try {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                        if (pin.getText().toString().equalsIgnoreCase("")) {
                            Helper.showAlertMessage(ShippingAddressActivity.this, "PinCode", "Please enter pin/zip code", "Close");
                        } else {
                            if (pin.getText().toString().length() < 6) {
                                Helper.showAlertMessage(ShippingAddressActivity.this, "PinCode", "Please enter valid pin/zip code", "Close");
                            } else {
                                //run on background thread.
                                Helper.hideKeyboardFromWindow(ShippingAddressActivity.this);
                            //    new GetLocationFromPinCodeTask().execute();
                            }
                        }


                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;
            }
        });

        findViewById(R.id.btn_checkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(stateAutoComplete.getText().toString())){

                    for (Map.Entry<Integer, String> entry : stateDataValues.entrySet()) {
                        if (entry.getValue().equalsIgnoreCase(stateAutoComplete.getText().toString().trim())) {
                            selectedStateId = String.valueOf(entry.getKey());
                        }
                    }
                }
                if(validateFields()){
                    startProgressDialog();
                    if(isRecordInUpdate){
                        addUserAddress = new ManageUserAddress(mHandler,Config.getStringValue(Config.USER_ID),inputfullName.getText().toString(),mAddress1AutoComplete.getText().toString(),mAddress2AutoComplete.getText().toString(),cityAutocomplete.getText().toString(),selectedStateId,pin.getText().toString(),addressType,inputMobileNo.getText().toString(),userAddresId);
                        addUserAddress.sendUpdateUserAddressRequest();
                    }else{
                        addUserAddress = new ManageUserAddress(mHandler,Config.getStringValue(Config.USER_ID),inputfullName.getText().toString(),mAddress1AutoComplete.getText().toString(),mAddress2AutoComplete.getText().toString(),cityAutocomplete.getText().toString(),selectedStateId,pin.getText().toString(),addressType,inputMobileNo.getText().toString());
                        addUserAddress.sendRequest();
                    }
                }

            }
        });



    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String str = (String) adapterView.getItemAtPosition(i);
        cityAutocomplete.setText(str);

    }


    public void settingCurrentLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);    // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1000);   // 1 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        btnCurrentLocation.setEnabled(true);
                        btnCurrentLocation.setAlpha(1);
                        progressBarLocation.setVisibility(View.GONE);
                        getLocation();

                        try {
                            if(currentLat != 0 && currentLong != 0) {
                                final Address address = getCurrentAddress(currentLat,currentLong);

                                final String str_address = address.getAddressLine(0);

                                Log.d("FullAdress",str_address);

                                if(str_address != null){
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ShippingAddressActivity.this);
                                    builder1.setTitle("Current Location");
                                    builder1.setMessage(str_address);
                                    builder1.setCancelable(true);

                                    builder1.setPositiveButton(
                                            "Set",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {


                                                    String [] addressArray = str_address.split("(?=,)");


                                                    // String country  = addressArray[addressArray.length - 1];
                                                    // String stateAndPin  = addressArray[addressArray.length - 2].replace(" ","/");

                                                    String [] fullAddress  = addressArray.toString().split(",");
                                                    String area = fullAddress[fullAddress.length - 4];

                                                    try {
                                                        //int addressEndIndex = str_address.indexOf(area);
                                                        int addressEndIndex = (area.length() - 1);
                                                        String completeAddress = str_address.substring(0,addressEndIndex);
                                                        mAddress1AutoComplete.setText(completeAddress.substring(0,completeAddress.length() - 1));
                                                        mAddress2AutoComplete.setText(address.getSubLocality());
                                                        cityAutocomplete.setText(address.getLocality());
                                                        stateAutoComplete.setText(address.getAdminArea());
                                                        pin.setText(address.getPostalCode());
                                                    }catch (Exception ex){
                                                        ex.printStackTrace();
                                                    }
                                                    dialog.cancel();
                                                }
                                            });

                                    builder1.setNegativeButton(
                                            "cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                    mAddress1AutoComplete.setText("");
                                                    mAddress2AutoComplete.setText("");
                                                    cityAutocomplete.setText("");
                                                    stateAutoComplete.setText("");
                                                    pin.setText("");
                                                }
                                            });

                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }



                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                        }


                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        btnCurrentLocation.setEnabled(true);
                        btnCurrentLocation.setAlpha(1);
                        progressBarLocation.setVisibility(View.GONE);

                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(ShippingAddressActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        btnCurrentLocation.setEnabled(true);
                        btnCurrentLocation.setAlpha(1);
                        progressBarLocation.setVisibility(View.GONE);

                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Location Service not Enabled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        } else {
            /*Getting the location after aquiring location service*/
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                currentLat = mLastLocation.getLatitude();
                currentLong = mLastLocation.getLongitude();
            } else {
                Log.i("Current Location", "No data for location found");
                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

               // LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, ShippingAddressActivity.this);
            }
        }
    }

    /*When Location changes, this method get called. */
//    @Override
//    public void onLocationChanged(Location location) {
//        mLastLocation = location;
//        currentLat = mLastLocation.getLatitude();
//        currentLong = mLastLocation.getLongitude();
////        _progressBar.setVisibility(View.INVISIBLE);
////        _latitude.setText("Latitude: " + String.valueOf(mLastLocation.getLatitude()));
////        _longitude.setText("Longitude: " + String.valueOf(mLastLocation.getLongitude()));
//    }

    private boolean validateFields(){
        if(TextUtils.isEmpty(inputfullName.getText().toString())){
            inputfullName.requestFocus();
            Helper.showAlertMessage(ShippingAddressActivity.this,"Full Name","Please enter full name","Close");
            return false;
        }else if(inputfullName.getText().toString().length() < 4){
            inputfullName.requestFocus();
            Helper.showAlertMessage(ShippingAddressActivity.this,"Full Name","Name must be min 4 characters","Close");
            return false;

        }else if(TextUtils.isEmpty(inputMobileNo.getText().toString())){
            inputfullName.requestFocus();
            Helper.showAlertMessage(ShippingAddressActivity.this,"Phone no","Please enter phone no","Close");
            return false;

        }else if(inputMobileNo.getText().toString().length() <  10){
            inputfullName.requestFocus();
            Helper.showAlertMessage(ShippingAddressActivity.this,"Phone no","Phone no must be 10 digits","Close");
            return false;

        }else if(TextUtils.isEmpty(mAddress1AutoComplete.getText().toString())){
            mAddress1AutoComplete.requestFocus();
            Helper.showAlertMessage(ShippingAddressActivity.this,"Address 1","Please enter Address 1(house no,building name) ","Close");
            return false;

        }else if(TextUtils.isEmpty(mAddress2AutoComplete.getText().toString())){
            mAddress2AutoComplete.requestFocus();
            Helper.showAlertMessage(ShippingAddressActivity.this,"Address 2","Please enter Address 2(Locality) ","Close");
            return false;

        }else if(TextUtils.isEmpty(cityAutocomplete.getText().toString())){
            cityAutocomplete.requestFocus();
            Helper.showAlertMessage(ShippingAddressActivity.this,"City","Please enter city","Close");
            return false;

        }else if(TextUtils.isEmpty(stateAutoComplete.getText().toString())){
            stateAutoComplete.requestFocus();
            Helper.showAlertMessage(ShippingAddressActivity.this,"State","Please enter state","Close");
            return false;

        }else if(TextUtils.isEmpty(pin.getText().toString())){
            pin.requestFocus();
            Helper.showAlertMessage(ShippingAddressActivity.this,"Pin/Zip Code","Please enter pin/zip code","Close");
            return false;

        }
        return true;
    }

    private class GetLocationFromPinCodeTask extends AsyncTask<String, String, Task<PlaceBufferResponse>> {

        private Task<PlaceBufferResponse> placeResult;


        @Override
        protected void onPreExecute() {
            startProgressDialog();
        }


        @Override
        protected Task<PlaceBufferResponse> doInBackground(String... params) {
           try {


               ArrayList<AutocompletePrediction> currentAddressData = getPlaceByZipCode(pin.getText().toString());
               if (currentAddressData != null) {
                   if (currentAddressData.size() > 0) {

                       final AutocompletePrediction item = currentAddressData.get(0);
                       final String placeId = item.getPlaceId();
                       final CharSequence primaryText = item.getPrimaryText(null);

                       placeResult = mGeoDataClient.getPlaceById(placeId);
                       return placeResult;
                   } else {
                       Helper.showAlertMessage(ShippingAddressActivity.this, "Pincode", "Please enter valid pin code", "Close");
                   }
               }
           }catch (Exception ex){
               ex.printStackTrace();
           }
            return null;
        }


        @Override
        protected void onPostExecute(Task<PlaceBufferResponse> result) {
            // execution of result of Long time consuming operation
            stopProgressDialog();
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback2);

        }

    }


    private ArrayList<AutocompletePrediction> getPlaceByZipCode(String zipCode) {

        AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry("IN").setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE).build();


        Task<AutocompletePredictionBufferResponse> results = mGeoDataClient.getAutocompletePredictions(zipCode,BOUNDS_GREATER_IN,filter);

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(results, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        try {
            AutocompletePredictionBufferResponse autocompletePredictions = results.getResult();

            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        } catch (RuntimeExecutionException e) {
            // If the query did not complete successfully return null
            Toast.makeText(getContext(), "Error contacting API: " + e.toString(),
                    Toast.LENGTH_SHORT).show();
            // Log.e(TAG, "Error getting autocomplete prediction API call", e);
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();

            // Log.e(TAG, "Error getting autocomplete prediction API call", e);
            return null;
        }



    }
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            //Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data Client to retrieve a Place object with
             additional details about the place.
              */
            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);

           // Toast.makeText(getApplicationContext(), "Clicked: " +primaryText,Toast.LENGTH_SHORT).show();
           // Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener1 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter1.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            //Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data Client to retrieve a Place object with
             additional details about the place.
              */
            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback3);

            // Toast.makeText(getApplicationContext(), "Clicked: " +primaryText,Toast.LENGTH_SHORT).show();
            // Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };


    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();

                // Get the Place object from the buffer.
                final Place place = places.get(0);

                // Format details of the place for display and show it in a TextView.
                cityAutocomplete.setText(place.getName());


                // Display the third party attributions if set.
//                final CharSequence thirdPartyAttribution = places.getAttributions();
//                if (thirdPartyAttribution == null) {
//                    mPlaceDetailsAttribution.setVisibility(View.GONE);
//                } else {
//                    mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                    mPlaceDetailsAttribution.setText(
//                            Html.fromHtml(thirdPartyAttribution.toString()));
//                }
//
//                Log.i(TAG, "Place details received: " + place.getName());

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                //Log.e(TAG, "Place query did not complete.", e);
                return;
            }
        }
    };

    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback2 = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();

                // Get the Place object from the buffer.
                final Place place = places.get(0);

                // Format details of the place for display and show it in a TextView.

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cityAutocomplete.setText(place.getName());
                            LatLng latLng =  place.getLatLng();

                            String address = getAddress(latLng.latitude,latLng.longitude);

                            mAddress2AutoComplete.setText(address);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                    }
                });

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                //Log.e(TAG, "Place query did not complete.", e);
                return;
            }
        }
    };

    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback3 = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();

                // Get the Place object from the buffer.
                final Place place = places.get(0);

                // Format details of the place for display and show it in a TextView.

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mAddress2AutoComplete.setText(place.getName());
                            LatLng latLng = place.getLatLng();
                            String address = getAddress2(latLng.latitude,latLng.longitude);
                            String[] addressArray = address.split(",");

                            String country  = addressArray[addressArray.length - 1];
                            String stateAndPin  = addressArray[addressArray.length - 2].replace(" ","/");
                            String city  = addressArray[addressArray.length - 3];

                            try {
                              String [] stateAddress = stateAndPin.split("/");

                              if(stateAddress != null){
                                  try {
                                      if(stateAddress.length >=  2){
                                          String state = stateAddress[1];
                                          String pin1 = stateAddress[2];
                                          stateAutoComplete.setText(state);
                                          pin.setText(pin1);
                                      }else if(stateAddress.length == 1){
                                          String state  = stateAddress[1];
                                          stateAutoComplete.setText(state);
                                      }
                                  }catch (Exception ex){

                                  }

                              }
                              if(city != null){
                                  cityAutocomplete.setText(city);
                              }

                            }catch (Exception ex){
                                ex.printStackTrace();
                                stateAutoComplete.setText(stateAndPin);

                            }

                            Log.d("fullAdrees",address);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                    }
                });

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                //Log.e(TAG, "Place query did not complete.", e);
                return;
            }
        }
    };

    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback4 = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();

                // Get the Place object from the buffer.
                final Place place = places.get(0);

                // Format details of the place for display and show it in a TextView.

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LatLng latLng = place.getLatLng();
                            String address  = getAddress2(latLng.latitude,latLng.longitude);

                            Log.d("fullAddress",address);
                            mAddress1AutoComplete.setText(place.getName());
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });


                //  houseNo.setText(place.get);


                // Display the third party attributions if set.
//                final CharSequence thirdPartyAttribution = places.getAttributions();
//                if (thirdPartyAttribution == null) {
//                    mPlaceDetailsAttribution.setVisibility(View.GONE);
//                } else {
//                    mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                    mPlaceDetailsAttribution.setText(
//                            Html.fromHtml(thirdPartyAttribution.toString()));
//                }
//
//                Log.i(TAG, "Place details received: " + place.getName());

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                //Log.e(TAG, "Place query did not complete.", e);
                return;
            }
        }
    };

    public String getAddress(double lat, double lng) {
        String add = "";
        Geocoder geocoder = new Geocoder(ShippingAddressActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
          //  add = obj.getAddressLine(0);
           // add = add + "\n" + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea() +",";
            //add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea() +",";
            add = add + "\n" + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return add;
    }

    public String getAddress2(double lat, double lng) {
        String add = "";
        Geocoder geocoder = new Geocoder(ShippingAddressActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
//              add = add + "\n" + obj.getCountryName();
//              add = add + "\n" + obj.getCountryCode();
//              add = add + "\n" + obj.getAdminArea() +",";
//            add = add + "\n" + obj.getPostalCode();
//            add = add + "\n" + obj.getSubAdminArea() +",";
//            add = add + "\n" + obj.getLocality();
//            add = add + "\n" + obj.getSubThoroughfare();


            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return add;
    }


    public Address getCurrentAddress(double lat,double lng) {
        Address address = null;
        Geocoder geocoder = new Geocoder(ShippingAddressActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            address = addresses.get(0);
            Log.v("IGA", "Address" + address.getAddressLine(0));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }


    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
     //   Log.e(TAG, "Place Details", name, id, address, phoneNumber, websiteUri);
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(ShippingAddressActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !Helper.isFinishingOrIsDestroyed(this))
                mProgressDialog.show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity="+Flinnt.SETTINGS+"&user="+Config.getStringValue(Config.USER_ID));
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }catch (Exception e){
            LogWriter.err(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        }catch (Exception e){
            LogWriter.err(e);
        }
    }

    public static void stopProgressDialog(){
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        }
        catch (Exception e) {
            LogWriter.err(e);
        }
        finally {
            mProgressDialog = null;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); //onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void fillStateSpinner() {




        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(stateSpinner);

            popupWindow.setHeight(stateDataValues.size() > 4 ? 500 : stateDataValues.size() * 100);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }

        ArrayList<String> values = new ArrayList<>();
        for (Integer key: stateDataValues.keySet()) {
            values.add(stateDataValues.get(key));

        }
        ArrayAdapter<String> adapterTerm = new ArrayAdapter<String>(ShippingAddressActivity.this, android.R.layout.simple_spinner_dropdown_item,values);
        stateAutoComplete.setAdapter(adapterTerm);
        selectedStateId = stateDataValues.get(0);

    }



    public void sendStateDataRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Helper.lockCPU();
                try {
                    sendRequest();
                } catch (Exception e) {
                    LogWriter.err(e);
                } finally {
                    Helper.unlockCPU();
                }
            }
        }.start();
    }

    public void sendRequest() {
        synchronized (ShippingAddressActivity.class) {
            try {
                String url = buildURLString();

                sendJsonObjectRequest(url, null);

            } catch (Exception e) {
                LogWriter.err(e);
            }

        }
    }
    public String buildURLString() {
        return Flinnt.LOCAL_API_URL_NEW + Flinnt.STATE_DATA_API;
    }

    private void sendJsonObjectRequest(String url, JSONObject jsonObject) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(LogWriter.isValidLevel(Log.DEBUG))

                {
                    try {
                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if(status == 1) {

                            String page = response.optString("page");

                            JSONObject data = response.getJSONObject("data");
                            stateDataValues = new HashMap<>();

                            if (data != null) {
                                Iterator<String> keys = data.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    String value = data.optString(key);
                                    stateDataValues.put(Integer.parseInt(key),value);

                                }

                                fillStateSpinner();
                            }
                        }else{
                           if(status == 0){
                               if(message != null){
                                   if(message.length() > 0){
                                       Toast.makeText(ShippingAddressActivity.this,message.replace("_"," "), Toast.LENGTH_SHORT).show();
                                   }
                               }
                           }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                    LogWriter.write("response :\n" + response.toString());



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(LogWriter.isValidLevel(Log.ERROR)) LogWriter.write("Error in getting state data : " + error.getMessage());
            }
        });

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        Requester.getInstance().addToRequestQueue(jsonObjReq);

        /** remove old unwanted files...*/
        Helper.deleteOldFiles(new File(MyConfig.FLINNT_FOLDER_PATH));
    }



}
