package jp.cpm.com.xxonmobil.dailyentry;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonFunctions;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.delegates.CoverageBean;
import jp.cpm.com.xxonmobil.gsonGetterSetter.JourneyPlan;
import jp.cpm.com.xxonmobil.gsonGetterSetter.NonWorkingReason;
import jp.cpm.com.xxonmobil.retrofit.PostApi;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NonWorkingReasonActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    ArrayList<NonWorkingReason> reasondata = new ArrayList<>();
    private Spinner reasonspinner;
    private Xxon_Database database;
    String reasonname = "", reasonid = "", entry_allow = "", image_allow = "", intime = "", city_name = "", user_type;
    FloatingActionButton save;
    private ArrayAdapter<CharSequence> reason_adapter;
    protected String _path, str;
    protected String _pathforcheck = "";
    private String image1 = "";
    private SharedPreferences preferences;
    String _UserId, visit_date, store_id, app_ver = "", store_name;
    protected boolean status = true;
    AlertDialog alert;
    ImageView camera;
    RelativeLayout rel_cam;
    ArrayList<JourneyPlan> jcp;
    boolean update_flag = false;
    GoogleApiClient mGoogleApiClient;
    double lat = 0.0, lon = 0.0;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_non_working_reason);
        reasonspinner = (Spinner) findViewById(R.id.spinner2);
        camera = (ImageView) findViewById(R.id.imgcam);
        save = (FloatingActionButton) findViewById(R.id.save);
        rel_cam = (RelativeLayout) findViewById(R.id.relimgcam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = preferences.getString(CommonString.KEY_STORE_CD, "");
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        store_name = preferences.getString(CommonString.KEY_STORE_NAME, "");
        city_name = preferences.getString(CommonString.KEY_CITY_NAME, "");
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, "");

        setTitle("Non Working -" + visit_date);
        database = new Xxon_Database(this);
        database.open();
        str = CommonString.FILE_PATH;
        jcp = database.getStoreData(visit_date);
        if (jcp.size() > 0) {
            boolean flag;
            if (database.isJCPStoreCheckIn(visit_date)) {
                flag = true;
                reasondata = database.getNonWorkingDataByFlag(flag, false);
            } else {
                flag = false;
                reasondata = database.getNonWorkingDataByFlag(flag, false);
            }
        }

        reason_adapter = new ArrayAdapter<>(this, R.layout.spinner_custom_item);
        reason_adapter.add("-Select Reason-");
        for (int i = 0; i < reasondata.size(); i++) {
            reason_adapter.add(reasondata.get(i).getReason());
        }

        intime = getCurrentTime();
        reasonspinner.setAdapter(reason_adapter);
        reason_adapter.setDropDownViewResource(R.layout.spinner_custom_item);
        reasonspinner.setOnItemSelectedListener(this);
        camera.setOnClickListener(this);
        save.setOnClickListener(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (!update_flag) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(NonWorkingReasonActivity.this);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            dialog.dismiss();
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub

        switch (arg0.getId()) {
            case R.id.spinner2:
                if (position != 0) {
                    reasonname = reasondata.get(position - 1).getReason();
                    reasonid = reasondata.get(position - 1).getReasonId().toString();
                    entry_allow = reasondata.get(position - 1).getEntryAllow().toString();
                    image_allow = reasondata.get(position - 1).getImageAllow().toString();
                    if (image_allow.equalsIgnoreCase("true")) {
                        rel_cam.setVisibility(View.VISIBLE);
                    } else {
                        rel_cam.setVisibility(View.GONE);
                    }
                } else {
                    reasonname = "";
                    reasonid = "";
                    image_allow = "";
                    entry_allow = "";
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    @SuppressWarnings("deprecation")
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
                        if (new File(str + _pathforcheck).exists()) {
                            String metadata = CommonFunctions.setMetadataAtImages(store_name, store_id, "Nonworking Image", _UserId);
                            Bitmap bmp = CommonFunctions.addMetadataAndTimeStampToImage(NonWorkingReasonActivity.this, _path, metadata, visit_date);
                            image1 = _pathforcheck;
                            camera.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera_done));
                            image1 = _pathforcheck;
                            _pathforcheck = "";
                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public boolean imageAllowed() {
        boolean result = true;
        if (image_allow.equalsIgnoreCase("true")) {
            if (image1.equals("")) {
                result = false;
            }
        }

        return result;

    }

    private boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.imgcam) {
            _pathforcheck = store_id + "_NONWORKING_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
            _path = CommonString.FILE_PATH + _pathforcheck;
            CommonFunctions.startAnncaCameraActivity(NonWorkingReasonActivity.this, _path);

        }
        if (v.getId() == R.id.save) {
            if (checkNetIsAvailable()) {
                if (validatedata()) {
                    update_flag = true;
                    if (imageAllowed()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                NonWorkingReasonActivity.this).setTitle(getString(R.string.parinaam));
                        builder.setMessage(getString(R.string.save_data)).setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                        if (entry_allow.equals("0")) {
                                            try {
                                                database.deleteAllTables();
                                                if (jcp.size() > 0) {
                                                    for (int i = 0; i < jcp.size(); i++) {
                                                        database.updateJaurneyPlanSpecificStoreStatus(jcp.get(i).getStoreId().toString(), visit_date, CommonString.KEY_U);
                                                        database.updateJaurneyPlanSpecificStoreofLastVisit_date(jcp.get(i).getStoreId().toString(), visit_date);
                                                    }
                                                }
                                                JSONObject jsonObject;
                                                if (user_type.equalsIgnoreCase("Merchandiser")) {
                                                    jsonObject = new JSONObject();
                                                    jsonObject.put("StoreId", store_id);
                                                    jsonObject.put("VisitDate", visit_date);
                                                    jsonObject.put("Latitude", String.valueOf(lat));
                                                    jsonObject.put("Longitude", String.valueOf(lon));
                                                    jsonObject.put("ReasonId", reasonid);
                                                    jsonObject.put("SubReasonId", "0");
                                                    jsonObject.put("Remark", "");
                                                    jsonObject.put("ImageName", "");
                                                    jsonObject.put("AppVersion", app_ver);
                                                    jsonObject.put("UploadStatus", CommonString.KEY_U);
                                                    jsonObject.put("Checkout_Image", "");
                                                    jsonObject.put("UserId", _UserId);
                                                } else {
                                                    jsonObject = new JSONObject();
                                                    jsonObject.put("StoreId", store_id);
                                                    jsonObject.put("VisitDate", visit_date);
                                                    jsonObject.put("Latitude", String.valueOf(lat));
                                                    jsonObject.put("Longitude", String.valueOf(lon));
                                                    jsonObject.put("ReasonId", reasonid);
                                                    jsonObject.put("SubReasonId", "0");
                                                    jsonObject.put("Remark", "");
                                                    jsonObject.put("ImageName", "");
                                                    jsonObject.put("AppVersion", app_ver);
                                                    jsonObject.put("UploadStatus", CommonString.KEY_U);
                                                    jsonObject.put("Checkout_Image", "");
                                                    jsonObject.put("UserId", _UserId);
                                                    jsonObject.put("Dep_MID", "0");
                                                }

                                                uploadCoverageIntimeDATA(jsonObject.toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        } else {

                                            try {
                                                CoverageBean cdata = new CoverageBean();
                                                cdata.setStore_Id(store_id);
                                                cdata.setVisitDate(visit_date);
                                                cdata.setUserId(_UserId);
                                                cdata.setReason(reasonname);
                                                cdata.setReasonid(reasonid);
                                                cdata.setLatitude(String.valueOf(lat));
                                                cdata.setLongitude(String.valueOf(lon));
                                                cdata.setImage(image1);
                                                cdata.setRemark("");
                                                cdata.setCity_name(city_name);
                                                //database.InsertCoverageData(cdata);
                                                database.updateJaurneyPlanSpecificStoreStatus(store_id, visit_date, CommonString.KEY_U);
                                                database.updateJaurneyPlanSpecificStoreofLastVisit_date(store_id, visit_date);
                                                JSONObject jsonObject = null;

                                                if (user_type.equalsIgnoreCase("Merchandiser")) {
                                                    jsonObject = new JSONObject();
                                                    jsonObject.put("StoreId", cdata.getStore_Id());
                                                    jsonObject.put("VisitDate", cdata.getVisitDate());
                                                    jsonObject.put("Latitude", cdata.getLatitude());
                                                    jsonObject.put("Longitude", cdata.getLongitude());
                                                    jsonObject.put("ReasonId", cdata.getReasonid());
                                                    jsonObject.put("SubReasonId", "0");
                                                    jsonObject.put("Remark", "");
                                                    jsonObject.put("ImageName", cdata.getImage());
                                                    jsonObject.put("AppVersion", app_ver);
                                                    jsonObject.put("UploadStatus", CommonString.KEY_U);
                                                    jsonObject.put("Checkout_Image", "");
                                                    jsonObject.put("UserId", _UserId);
                                                } else {
                                                    jsonObject = new JSONObject();
                                                    jsonObject.put("StoreId", cdata.getStore_Id());
                                                    jsonObject.put("VisitDate", cdata.getVisitDate());
                                                    jsonObject.put("Latitude", cdata.getLatitude());
                                                    jsonObject.put("Longitude", cdata.getLongitude());
                                                    jsonObject.put("ReasonId", cdata.getReasonid());
                                                    jsonObject.put("SubReasonId", "0");
                                                    jsonObject.put("Remark", "");
                                                    jsonObject.put("ImageName", cdata.getImage());
                                                    jsonObject.put("AppVersion", app_ver);
                                                    jsonObject.put("UploadStatus", CommonString.KEY_U);
                                                    jsonObject.put("UserId", _UserId);
                                                    jsonObject.put("Checkout_Image", "");
                                                    jsonObject.put("Dep_MID", "0");
                                                }
                                                uploadCoverageIntimeDATA(jsonObject.toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        finish();
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
                        Snackbar.make(reasonspinner, "Please Capture Nonworking Image", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(reasonspinner, "Please Select a Reason", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(reasonspinner, getString(R.string.nonetwork), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public boolean validatedata() {
        boolean result = false;
        if (reasonid != null && !reasonid.equals("")) {
            result = true;
        }
        return result;
    }


    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!update_flag) {
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(NonWorkingReasonActivity.this);
                builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                dialog.dismiss();
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
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
            loading = ProgressDialog.show(NonWorkingReasonActivity.this, "Processing", "Please wait...",
                    false, false);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();

            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsondata.toString());
            Retrofit adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostApi api = adapter.create(PostApi.class);
            retrofit2.Call<ResponseBody> call;
            if (user_type.equalsIgnoreCase("Merchandiser")) {
                call = api.getCoverageDetail(jsonData);
            } else {
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
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                NonWorkingReasonActivity.this.finish();
                                loading.dismiss();
                            }
                        } catch (Exception e) {
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
                            AlertandMessages.showAlertlogin(NonWorkingReasonActivity.this, t.getMessage().toString());
                        }
                    } else {
                        AlertandMessages.showAlertlogin(NonWorkingReasonActivity.this, "Please check internet connection");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loading.dismiss();
            if (e != null) {
                AlertandMessages.showAlertlogin(NonWorkingReasonActivity.this, "Please check internet connection" + e.toString());
            } else {
                AlertandMessages.showAlertlogin(NonWorkingReasonActivity.this, "Please check internet connection");

            }
        }
    }
}
