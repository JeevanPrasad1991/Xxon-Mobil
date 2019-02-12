package jp.cpm.com.xxonmobil.dailyentry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonFunctions;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.delegates.CoverageBean;
import jp.cpm.com.xxonmobil.gsonGetterSetter.JourneyPlan;
import jp.cpm.com.xxonmobil.retrofit.PostApi;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoreimageActivity extends AppCompatActivity implements
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    ImageView img_cam, img_clicked;
    FloatingActionButton btn_save;
    String _pathforcheck, _path, str;
    String Store_Id, visit_date, username, intime,user_type;
    private SharedPreferences preferences;
    AlertDialog alert;
    String img_str;
    private Xxon_Database database;
    double lat = 0.0, lon = 0.0;
    GoogleApiClient mGoogleApiClient;
    ByteArrayOutputStream bytearrayoutputstream;
    private static final int REQUEST_LOCATION = 1;
    ArrayList<JourneyPlan> specific_store = new ArrayList<>();
    TextView storenamewithbeatname_txt;
    EditText store_remark;
    ProgressDialog loading;
    CoverageBean cdata;
    List<Address> addresses;
    String app_ver, complete_locality = "", store_name, city_name;
    boolean update_flag = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storeimage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        img_cam = findViewById(R.id.img_selfie);
        img_clicked = findViewById(R.id.img_cam_selfie);
        bytearrayoutputstream = new ByteArrayOutputStream();
        btn_save = findViewById(R.id.btn_save_selfie);
        store_remark = findViewById(R.id.store_remark);
        storenamewithbeatname_txt = (TextView) findViewById(R.id.storenamewithbeatname_txt);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Store_Id = preferences.getString(CommonString.KEY_STORE_CD, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, "");
        username = preferences.getString(CommonString.KEY_USERNAME, "");
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        city_name = preferences.getString(CommonString.KEY_CITY_NAME, "");
        store_name = preferences.getString(CommonString.KEY_STORE_NAME, "");
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, "");


        getSupportActionBar().setTitle(getString(R.string.clinic_front_img));
        storenamewithbeatname_txt.setText("Store Name - " + store_name + " (Id - " + Store_Id + ") " + "\n" + "City Name - " + city_name + " - Date - " + visit_date);
        str = CommonString.FILE_PATH;
        database = new Xxon_Database(this);
        database.open();
        img_cam.setOnClickListener(this);
        img_clicked.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        specific_store = database.getSpecificStoreData(Store_Id);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (update_flag) {
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                StoreimageActivity.this.finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                        StoreimageActivity.this.finish();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (update_flag) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            StoreimageActivity.this.finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    StoreimageActivity.this.finish();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_cam_selfie:
                update_flag = false;
                try {
                    long freeSpace = getAvailableSpaceInMB();
                    if (freeSpace < 70) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Memory Error").setMessage("Your device storage is almost full.Your free space should be 70 MB.");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                StoreimageActivity.this.finish();
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    } else {
                        _pathforcheck = Store_Id + "_STOREIMG_" + visit_date.replace("/", "") +
                                "_" + getCurrentTime().replace(":", "") + ".jpg";
                        _path = CommonString.FILE_PATH + _pathforcheck;
                        intime = getCurrentTime();
                        CommonFunctions.startAnncaCameraActivity(StoreimageActivity.this, _path);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    _pathforcheck = Store_Id + "_STOREIMG_" + visit_date.replace("/", "") +
                            "_" + getCurrentTime().replace(":", "") + ".jpg";
                    _path = CommonString.FILE_PATH + _pathforcheck;
                    intime = getCurrentTime();
                    CommonFunctions.startAnncaCameraActivity(StoreimageActivity.this, _path);
                }

                break;

            case R.id.btn_save_selfie:
                if (img_str != null) {
                    if (checkNetIsAvailable()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                StoreimageActivity.this).setTitle(getString(R.string.parinaam));
                        builder.setMessage(getString(R.string.save_data))
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                try {
                                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                    cdata = new CoverageBean();
                                                    cdata.setStore_Id(Store_Id);
                                                    cdata.setVisitDate(visit_date);
                                                    cdata.setUserId(username);
                                                    cdata.setReason("");
                                                    cdata.setReasonid("0");
                                                    cdata.setLatitude(String.valueOf(lat));
                                                    cdata.setLongitude(String.valueOf(lon));
                                                    cdata.setCity_name(city_name);
                                                    cdata.setImage(img_str);
                                                    cdata.setRemark(store_remark.getText().toString().replaceAll("[(!@#$%^&*?)\"]", ""));

                                                    //region Coverage Data
                                                    JSONObject jsonObject = null;
                                                    if (user_type.equalsIgnoreCase("Merchandiser")){
                                                         jsonObject = new JSONObject();
                                                        jsonObject.put("StoreId", cdata.getStore_Id());
                                                        jsonObject.put("VisitDate", cdata.getVisitDate());
                                                        jsonObject.put("Latitude", cdata.getLatitude());
                                                        jsonObject.put("Longitude", cdata.getLongitude());
                                                        jsonObject.put("ReasonId", cdata.getReasonid());
                                                        jsonObject.put("SubReasonId", "0");
                                                        jsonObject.put("Remark", cdata.getRemark());
                                                        jsonObject.put("ImageName", cdata.getImage());
                                                        jsonObject.put("AppVersion", app_ver);
                                                        jsonObject.put("UploadStatus", CommonString.KEY_CHECK_IN);
                                                        jsonObject.put("Checkout_Image", "");
                                                        jsonObject.put("UserId", username);
                                                    }else {
                                                        jsonObject = new JSONObject();
                                                        jsonObject.put("StoreId", cdata.getStore_Id());
                                                        jsonObject.put("VisitDate", cdata.getVisitDate());
                                                        jsonObject.put("Latitude", cdata.getLatitude());
                                                        jsonObject.put("Longitude", cdata.getLongitude());
                                                        jsonObject.put("ReasonId", cdata.getReasonid());
                                                        jsonObject.put("SubReasonId", "0");
                                                        jsonObject.put("Remark", cdata.getRemark());
                                                        jsonObject.put("ImageName", cdata.getImage());
                                                        jsonObject.put("AppVersion", app_ver);
                                                        jsonObject.put("UploadStatus", CommonString.KEY_CHECK_IN);
                                                        jsonObject.put("Checkout_Image", "");
                                                        jsonObject.put("UserId", username);
                                                        jsonObject.put("Dep_MID", "0");
                                                    }
                                                    uploadCoverageIntimeDATA(jsonObject.toString());
                                                } catch (Exception e) {
                                                    Crashlytics.logException(e);
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }
                                });
                        alert = builder.create();
                        alert.show();
                    } else {
                        Snackbar.make(btn_save, getString(R.string.nonetwork), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(btn_save, "Please click the image", Snackbar.LENGTH_SHORT).show();

                }

                break;

        }

    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    try {
                        if (new File(CommonString.FILE_PATH + _pathforcheck).exists()) {
                            String metadata = CommonFunctions.setMetadataAtImages(store_name, Store_Id, "Store Image", username);
                            Bitmap bmp = CommonFunctions.addMetadataAndTimeStampToImage(StoreimageActivity.this, _path, metadata, visit_date);
                            img_cam.setImageBitmap(bmp);
                            img_clicked.setVisibility(View.GONE);
                            img_cam.setVisibility(View.VISIBLE);
                            //Set Clicked image to Imageview
                            img_str = _pathforcheck;
                            _pathforcheck = "";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        finish();
                    }
                    default: {
                        break;
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();
                //get locality addresssss
                Geocoder geocoder;
                addresses = null;
                geocoder = new Geocoder(this, Locale.getDefault());
                addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                complete_locality = addresses.get(0).getAddressLine(0); // If any additional address line present than only, c// heck with max available address lines by getMaxAddressLineIndex()
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void uploadCoverageIntimeDATA(String jsondata) {
        try {
            loading = ProgressDialog.show(StoreimageActivity.this, "Processing", "Please wait...", false, false);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsondata.toString());
            Retrofit adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            PostApi api = adapter.create(PostApi.class);
            retrofit2.Call<ResponseBody> call;

            if (user_type.equalsIgnoreCase("Merchandiser")){
                call = api.getCoverageDetail(jsonData);
            }else {
                call = api.getCoverageDetailAudit(jsonData);
            }
            call.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    ResponseBody responseBody = response.body();
                    String data = null;
                    if (responseBody != null && response.isSuccessful()) {
                        try {
                            data = response.body().string();
                            if (!data.equals("0")) {
                                database.open();
                                database.InsertCoverageData(cdata);
                                database.updateJaurneyPlanSpecificStoreStatus(cdata.getStore_Id(), cdata.getVisitDate(), CommonString.KEY_CHECK_IN);
                                database.updateJaurneyPlanSpecificStoreofLastVisit_date(cdata.getStore_Id(), cdata.getVisitDate());
                                Intent intent = new Intent(StoreimageActivity.this, StoreProfileActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                StoreimageActivity.this.finish();
                                loading.dismiss();
                            }

                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                            loading.dismiss();
                        }
                    }

                }

                @Override
                public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                    loading.dismiss();
                    if (t != null) {
                        if (t instanceof SocketTimeoutException || t instanceof IOException || t instanceof Exception) {
                            AlertandMessages.showAlertlogin(StoreimageActivity.this, t.getMessage().toString());
                        }
                    } else {
                        AlertandMessages.showAlertlogin(StoreimageActivity.this, t.getMessage().toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loading.dismiss();
        }
    }

    public static long getAvailableSpaceInMB() {
        final long SIZE_KB = 1024L;
        final long SIZE_MB = SIZE_KB * SIZE_KB;
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        return availableSpace / SIZE_MB;
    }
}


