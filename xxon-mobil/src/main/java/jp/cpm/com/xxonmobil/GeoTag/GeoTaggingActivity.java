package jp.cpm.com.xxonmobil.GeoTag;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonFunctions;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.dailyentry.StoreListActivity;
import jp.cpm.com.xxonmobil.dailyentry.StoreimageActivity;
import jp.cpm.com.xxonmobil.gettersetter.GeotaggingBeans;
import jp.cpm.com.xxonmobil.retrofit.DownloadAllDatawithRetro;
import jp.cpm.com.xxonmobil.retrofit.PostApi;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeoTaggingActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    double latitude = 0.0;
    double longitude = 0.0;
    protected String _path, _pathforcheck, img_str = "", status, user_type;
    private Location mLastLocation;
    private LocationManager locmanager = null;
    FloatingActionButton fab, fabcarmabtn;
    SharedPreferences preferences;
    String username, str, visitDate, store_cd;
    Xxon_Database db;
    LocationManager locationManager;
    Marker currLocationMarker;
    Geocoder geocoder;
    boolean enabled;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 500; // 5 sec
    private static int FATEST_INTERVAL = 100; // 1 sec
    private static int DISPLACEMENT = 5; // 10 meters
    private static final String TAG = GeoTaggingActivity.class.getSimpleName();
    ArrayList<GeotaggingBeans> geotaglist = new ArrayList<>();
    Context context;
    Activity activity;
    DownloadAllDatawithRetro upload;
    ProgressDialog loading;
    String app_ver = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_tagging);
        declaration();
        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }
        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        enabled = locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.gps));
            // Setting Dialog Message
            alertDialog.setMessage(getResources().getString(R.string.gpsebale));
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton(getResources().getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton(getResources().getString(R.string.no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();

        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkNetIsAvailable()) {
                    if (!img_str.equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage("Do you want to save and upload Geo Tag data ?");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                status = CommonString.KEY_STATUS_N;
                                if (db.InsertSTOREgeotag(store_cd, latitude, longitude, img_str, status) > 0) {
                                    img_str = "";
                                    jsonData();
                                } else {
                                    Snackbar.make(fab, "Error in saving Geotag", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();


                    } else {
                        Snackbar.make(fab, "Please Take Geo Tag Image.", Snackbar.LENGTH_SHORT).show();

                    }
                } else {
                    Snackbar.make(fab, getString(R.string.nonetwork), Snackbar.LENGTH_SHORT).show();

                }
            }
        });
        fabcarmabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (latitude == 0.0 && longitude == 0.0) {
                        AlertandMessages.showAlertlogin((Activity) context, "Wait for Geo location");
                    } else {
                        _pathforcheck = store_cd + "_GEO_TAG_" + visitDate.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                        _path = CommonString.FILE_PATH + _pathforcheck;
                        CommonFunctions.startAnncaCameraActivity(context, _path);
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }


            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.notsuppoted), Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
        }
    }


    private boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            String result = null;
            LatLng latLng;
            try {
                List<Address> addressList = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                if (addressList != null && addressList.size() > 0) {
                    result = addressList.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                Crashlytics.logException(e);
                Log.e(TAG, "Unable connect to Geocoder", e);
            }
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(result);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:mmm");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    try {
                        if (new File(CommonString.FILE_PATH + _pathforcheck).exists()) {
                            String metadata = CommonFunctions.setmetadataforattendance("GeoTag Image", username);
                            Bitmap bmp = CommonFunctions.addMetadataAndTimeStampToImage(context, _path, metadata, visitDate);
                            fabcarmabtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.camera_green));
                            fabcarmabtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#888888")));
                            img_str = _pathforcheck;
                            _pathforcheck = "";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);

                    }
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    public void onLocationChanged(Location location) {

    }


    void declaration() {
        activity = this;
        context = this;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, "");
        visitDate = preferences.getString(CommonString.KEY_DATE, "");
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, "");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabcarmabtn = (FloatingActionButton) findViewById(R.id.camrabtn);
        db = new Xxon_Database(context);
        db.open();
        str = CommonString.FILE_PATH;
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        upload = new DownloadAllDatawithRetro(context);
        try {
            app_ver = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void jsonData() {
        geotaglist = db.getinsertGeotaggingData(store_cd, CommonString.KEY_STATUS_N);
        try {
            if (geotaglist.size() > 0) {
                if (user_type.equalsIgnoreCase("Merchandiser")) {
                    JSONArray topUpArray = new JSONArray();
                    for (int j = 0; j < geotaglist.size(); j++) {
                        JSONObject obj = new JSONObject();
                        obj.put("Store_Cd", geotaglist.get(j).getStore_Id());
                        obj.put("Visit_Date", visitDate);
                        obj.put("Latitude", geotaglist.get(j).getLatitude());
                        obj.put("Longitude", geotaglist.get(j).getLongitude());
                        obj.put("Geo_Image", geotaglist.get(j).getImage());
                        topUpArray.put(obj);
                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("MID", "0");
                    jsonObject.put("Keys", "Geo_Tag");
                    jsonObject.put("JsonData", topUpArray.toString());
                    jsonObject.put("UserId", username);
                    String jsonString2 = jsonObject.toString();
                    uploadGeoTagData(jsonString2);
                } else {
                    JSONArray topUpArray = new JSONArray();
                    for (int j = 0; j < geotaglist.size(); j++) {
                        JSONObject obj = new JSONObject();
                        obj.put("Store_Cd", geotaglist.get(j).getStore_Id());
                        obj.put("Visit_Date", visitDate);
                        obj.put("Latitude", geotaglist.get(j).getLatitude());
                        obj.put("Longitude", geotaglist.get(j).getLongitude());
                        obj.put("Geo_Image", geotaglist.get(j).getImage());
                        topUpArray.put(obj);
                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("MID", "0");
                    jsonObject.put("Keys", "Geo_Tag_Audit");
                    jsonObject.put("JsonData", topUpArray.toString());
                    jsonObject.put("UserId", username);
                    String jsonString2 = jsonObject.toString();
                    uploadGeoTagData(jsonString2);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    protected void uploadGeoTagData(String jsonString) {
        try {
            loading = ProgressDialog.show(context, "Processing", "Please wait...", false, false);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).connectTimeout(20, TimeUnit.SECONDS).build();
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
            Retrofit adapter;
            adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
            PostApi api = adapter.create(PostApi.class);
            Call<JsonObject> call = api.getGeotag(jsonData);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    String responseBody = response.body().get("UploadJsonDetailResult").toString();
                    String data = null;
                    if (responseBody != null && response.isSuccessful()) {
                        try {
                            data = response.body().get("UploadJsonDetailResult").toString();
                            if (data.equals("")) {
                            } else {
                                data = data.substring(1, data.length() - 1).replace("\\", "");
                                if (data.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    status = CommonString.KEY_Y;
                                    db.updateStatus(store_cd, status);
                                    if (db.updateInsertedGeoTagStatus(store_cd, status) > 0) {
                                        img_str = "";
                                        AlertandMessages.showToastMsg(context, "Geotag Saved Successfully");
                                        Intent in = new Intent(context, StoreimageActivity.class);
                                        startActivity(in);
                                        GeoTaggingActivity.this.finish();
                                    } else {
                                        AlertandMessages.showAlertlogin((Activity) context, "Error in updating Geotag status");
                                    }
                                } else {
                                    AlertandMessages.showAlertlogin((Activity) context, "Error in updating Geotag status. Please try again");
                                }
                                loading.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                            loading.dismiss();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    loading.dismiss();
                    if (t != null && t instanceof SocketTimeoutException || t != null && t instanceof IOException || t != null && t instanceof Exception) {
                        AlertandMessages.showAlertlogin(GeoTaggingActivity.this, CommonString.MESSAGE_SOCKETEXCEPTION + "(" + t.toString() + ")");
                    } else {
                        AlertandMessages.showAlertlogin(GeoTaggingActivity.this, CommonString.MESSAGE_SOCKETEXCEPTION);
                    }
                }
            });

        } catch (Exception e) {
            loading.dismiss();
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}
