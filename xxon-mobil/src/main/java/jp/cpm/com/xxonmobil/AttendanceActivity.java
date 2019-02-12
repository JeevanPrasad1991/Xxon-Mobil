package jp.cpm.com.xxonmobil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.MultipartBuilder;

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
import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonFunctions;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.dailyentry.CityActivity;
import jp.cpm.com.xxonmobil.download.DownloadActivity;
import jp.cpm.com.xxonmobil.gsonGetterSetter.NonWorkingReason;
import jp.cpm.com.xxonmobil.retrofit.PostApi;
import jp.cpm.com.xxonmobil.retrofit.PostApiForUpload;
import jp.cpm.com.xxonmobil.retrofit.StringConverterFactory;
import jp.cpm.com.xxonmobil.upload.PreviousDataUploadActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static jp.cpm.com.xxonmobil.dailyentry.StoreimageActivity.getAvailableSpaceInMB;
import static jp.cpm.com.xxonmobil.upload.UploadDataActivity.saveBitmapToFile;

public class AttendanceActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private String userId, date, _path, _pathforcheck = "", img_str = "", reasonname = "", reasonid = "", image_allow = "";
    ArrayList<NonWorkingReason> reasondata = new ArrayList<>();
    private ArrayAdapter<CharSequence> reason_adapter;
    Xxon_Database database;
    SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    FloatingActionButton btn_save_selfie;
    ImageView img_cam_attendance;
    LinearLayout no_data_lay, rl_spin;
    Spinner spin_attendance;
    boolean entryFlag = false;
    ProgressDialog loading;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        declaration();
    }

    private void declaration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_save_selfie = (FloatingActionButton) findViewById(R.id.btn_save_selfie);
        img_cam_attendance = (ImageView) findViewById(R.id.img_cam_attendance);
        spin_attendance = (Spinner) findViewById(R.id.spin_attendance);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        date = preferences.getString(CommonString.KEY_DATE, "");
        no_data_lay = (LinearLayout) findViewById(R.id.no_data_lay);
        rl_spin = (LinearLayout) findViewById(R.id.rl_spin);
        userId = preferences.getString(CommonString.KEY_USERNAME, "");
        getSupportActionBar().setTitle(getString(R.string.title_attendance) + " - " + date);
        database = new Xxon_Database(this);
        database.open();
        reasondata = database.getNonWorkingDataByFlag(false, true);

        reason_adapter = new ArrayAdapter<>(this, R.layout.spinner_custom_item);

        reason_adapter.add("-Select Attendance-");
        for (int i = 0; i < reasondata.size(); i++) {
            reason_adapter.add(reasondata.get(i).getReason());
        }

        spin_attendance.setAdapter(reason_adapter);
        reason_adapter.setDropDownViewResource(R.layout.spinner_custom_item);
        spin_attendance.setOnItemSelectedListener(this);
        img_cam_attendance.setOnClickListener(this);
        btn_save_selfie.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        database.open();
        if (database.getcitymapping(date).size() > 0) {
            btn_save_selfie.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_save));
            no_data_lay.setVisibility(View.GONE);
            rl_spin.setVisibility(View.VISIBLE);
        } else {
            btn_save_selfie.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.download_icon));
            no_data_lay.setVisibility(View.VISIBLE);
            rl_spin.setVisibility(View.GONE);
        }
    }


    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onBackPressed() {
        if (entryFlag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            AttendanceActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            AttendanceActivity.this.finish();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (entryFlag) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE).setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                AttendanceActivity.this.finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                AttendanceActivity.this.finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cam_attendance:
                entryFlag = true;
                try {
                    long freeSpace = getAvailableSpaceInMB();
                    if (freeSpace < 70) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Memory Error").setMessage("Your device storage is almost full.Your free space should be 70 MB.");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                AttendanceActivity.this.finish();
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    } else {
                        _pathforcheck = userId + "_ATTENDIMG_" + date.replace("/", "") +
                                "_" + getCurrentTime().replace(":", "") + ".jpg";
                        _path = CommonString.FILE_PATH + _pathforcheck;
                        CommonFunctions.startAnncaCameraActivity(context, _path);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    _pathforcheck = userId + "_ATTENDIMG_" + date.replace("/", "") +
                            "_" + getCurrentTime().replace(":", "") + ".jpg";
                    _path = CommonString.FILE_PATH + _pathforcheck;
                    CommonFunctions.startAnncaCameraActivity(context, _path);
                }
                break;

            case R.id.btn_save_selfie:
                if (checkNetIsAvailable()) {
                    if (database.getcitymapping(date).size() == 0) {
                        if (database.isCoverageDataFilled(date)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Parinaam");
                            builder.setMessage("Please upload previous data first")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(context, PreviousDataUploadActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            try {
                                database.open();
                                database.deletePreviousUploadedData(date);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Crashlytics.logException(e);
                            }
                            Intent startDownload = new Intent(context, DownloadActivity.class);
                            startActivity(startDownload);
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            finish();
                        }
                    } else {
                        if (validatecondition()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).
                                    setTitle(R.string.save_data).setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                JSONObject jsonObject = null;
                                                if (reasonname.equalsIgnoreCase("Present")) {
                                                    //region Coverage Data
                                                    jsonObject = new JSONObject();
                                                    jsonObject.put("UserId", userId);
                                                    jsonObject.put("Reason_Id", reasonid);
                                                    jsonObject.put("Att_Date", date);
                                                    jsonObject.put("Image_Url", img_str);
                                                    uploadattendancedata(jsonObject.toString(), context, userId, reasonid, date, img_str);
                                                } else {
                                                    jsonObject = new JSONObject();
                                                    jsonObject.put("UserId", userId);
                                                    jsonObject.put("Reason_Id", reasonid);
                                                    jsonObject.put("Att_Date", date);
                                                    jsonObject.put("Image_Url", "");
                                                    uploadattendancedata(jsonObject.toString(), context, userId, reasonid, date, img_str);
                                                }
                                                dialog.dismiss();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }
                    }
                } else {
                    Snackbar.make(btn_save_selfie, getString(R.string.nonetwork), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spin_attendance:
                if (position != 0) {
                    reasonname = reasondata.get(position - 1).getReason();
                    reasonid = reasondata.get(position - 1).getReasonId().toString();
                    image_allow = reasondata.get(position - 1).getImageAllow().toString();
                    if (image_allow.equalsIgnoreCase("true")) {
                        img_cam_attendance.setVisibility(View.VISIBLE);
                    } else {
                        img_cam_attendance.setVisibility(View.GONE);
                    }
                } else {
                    reasonname = "";
                    reasonid = "";
                    image_allow = "";
                    img_cam_attendance.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
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
                            String metadata = CommonFunctions.setmetadataforattendance("Attendance Image", userId);
                            Bitmap bmp = CommonFunctions.addMetadataAndTimeStampToImage(context, _path, metadata, date);
                            img_cam_attendance.setImageResource(R.drawable.ic_menu_camera_done);
                            //Set Clicked image to Imageview
                            img_str = _pathforcheck;
                            _pathforcheck = "";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validatecondition() {
        boolean flag = true;
        if (spin_attendance.getSelectedItemId() == 0) {
            AlertandMessages.showAlertlogin((Activity) context, "Please select a reason in spinner dropdown");
            flag = false;
        } else if (image_allow.equalsIgnoreCase("true") && img_str.equals("")) {
            AlertandMessages.showAlertlogin((Activity) context, "Please capture image");
            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }


    public void uploadattendancedata(String jsondata, final Context context, final String user_NM, final String reason_cd, final
    String visit_date, final String att_image) {
        try {
            loading = ProgressDialog.show(context, "Processing", "Please wait...", false, false);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS).build();

            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsondata.toString());
            Retrofit adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            PostApi api = adapter.create(PostApi.class);
            retrofit2.Call<ResponseBody> call = api.getAttendanceDetails(jsonData);
            call.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    ResponseBody responseBody = response.body();
                    String data = null;
                    if (responseBody != null && response.isSuccessful()) {
                        try {
                            data = response.body().string();
                            if (data.contains("1")) {
                                if (reasonname.equalsIgnoreCase("Present")) {
                                    editor.putString(CommonString.KEY_ATTENDENCE_STATUS, reason_cd);
                                    editor.apply();
                                    database.open();
                                    database.insertAttendenceData(user_NM, visit_date, att_image, reasonname, reason_cd, image_allow);
                                    Intent intent = new Intent(context, CityActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    AttendanceActivity.this.finish();
                                    loading.dismiss();
                                } else {
                                    editor.putString(CommonString.KEY_ATTENDENCE_STATUS, reason_cd);
                                    editor.apply();
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    AttendanceActivity.this.finish();
                                    loading.dismiss();
                                    if (!att_image.equals("")) {
                                        UploadImageRecursive(context, att_image, visit_date);
                                    }
                                }
                            } else {
                                if (data.contains("0")) {
                                    editor.putString(CommonString.KEY_ATTENDENCE_STATUS, reason_cd);
                                    editor.apply();
                                    database.open();
                                    database.insertAttendenceData(user_NM, visit_date, att_image, reasonname, reason_cd, image_allow);
                                    Intent intent = new Intent(context, CityActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    AttendanceActivity.this.finish();
                                    loading.dismiss();
                                } else {
                                    throw new java.lang.Exception();
                                }
                            }
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                            loading.dismiss();
                            editor.putString(CommonString.KEY_ATTENDENCE_STATUS, "0");
                            editor.apply();
                            AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE + "(" + e.toString() + ")");
                        }
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                    loading.dismiss();
                    editor.putString(CommonString.KEY_ATTENDENCE_STATUS, "0");
                    editor.apply();
                    if (t != null) {
                        if (t instanceof SocketTimeoutException || t instanceof IOException || t instanceof Exception) {
                            AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE + "(" + t.toString() + ")");
                        }
                    } else {
                        AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loading.dismiss();
            editor.putString(CommonString.KEY_ATTENDENCE_STATUS, "0");
            editor.apply();
            AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE + "(" + e.toString() + ")");
        }
    }

    public void UploadImageRecursive(final Context context, final String attendance_img, String visit_date) {
        try {
            String foldername = "AttendanceImages";
            File originalFile = new File(CommonString.FILE_PATH + attendance_img);
            final File finalFile = saveBitmapToFile(originalFile);
            String date = visit_date.replace("/", "");
            com.squareup.okhttp.OkHttpClient okHttpClient = new com.squareup.okhttp.OkHttpClient();
            okHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
            okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
            com.squareup.okhttp.RequestBody photo = com.squareup.okhttp.RequestBody.create(com.squareup.okhttp.MediaType.parse("application/octet-stream"), finalFile);

            com.squareup.okhttp.RequestBody body1 = new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart("file", finalFile.getName(), photo).addFormDataPart("FolderName", foldername).addFormDataPart("Path", date).build();

            retrofit.Retrofit adapter = new retrofit.Retrofit.Builder().baseUrl(CommonString.URLGORIMAG).client(okHttpClient).addConverterFactory(new StringConverterFactory()).build();

            PostApiForUpload api = adapter.create(PostApiForUpload.class);
            retrofit.Call<String> call = api.getUploadImageRetrofitOne(body1);
            call.enqueue(new retrofit.Callback<String>() {
                @Override
                public void onResponse(retrofit.Response<String> response) {
                    if (response.code() == 200 && response.message().equalsIgnoreCase("OK") && response.isSuccess() && response.body().contains("Success")) {
                        finalFile.delete();
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
