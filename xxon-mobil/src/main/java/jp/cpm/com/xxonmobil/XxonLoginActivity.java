package jp.cpm.com.xxonmobil;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import jp.cpm.com.xxonmobil.Get_IMEI_number.ImeiNumberClass;
import jp.cpm.com.xxonmobil.autoupdate.AutoUpdateActivity;
import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.gettersetter.LoginGsonGetterSetter;
import jp.cpm.com.xxonmobil.gpsenable.LocationEnableCommon;
import jp.cpm.com.xxonmobil.retrofit.PostApi;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class XxonLoginActivity extends AppCompatActivity {
    private TextView tv_version;
    private String app_ver;
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;
    private final String lat = "0.0";
    private final String lon = "0.0";
    // UI references.
    private AutoCompleteTextView museridView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Context context;
    private String userid;
    private String password;
    private int versionCode;
    private String[] imeiNumbers;
    private int i = 0;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    LocationEnableCommon locationEnableCommon;
    private static final int REQUEST_LOCATION = 1;
    private Button museridSignInButton;
    private ImeiNumberClass imei;
    private String manufacturer;
    private String model;
    private String os_version;
    private Retrofit adapter;
    String imei1 = "", imei2 = "", status;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_xxon_login_activty);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Ui_declaration();
        getDeviceName();
    }


    private void attemptLogin() {
        // Reset errors.
        museridView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        userid = museridView.getText().toString().trim();
        password = mPasswordView.getText().toString().trim();
        boolean cancel = false;
        View focusView = null;
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid userid address.
        if (TextUtils.isEmpty(userid)) {
            museridView.setError(getString(R.string.error_field_required));
            focusView = museridView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else if (!isuseridValid(userid)) {
            Snackbar.make(museridView, getString(R.string.error_incorrect_username), Snackbar.LENGTH_SHORT).show();
        } else if (!isPasswordValid(password)) {
            Snackbar.make(museridView, getString(R.string.error_incorrect_password), Snackbar.LENGTH_SHORT).show();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if (checkNetIsAvailable())
                if (locationEnableCommon.checkgpsEnableDevice(this)) {
                    AttempLogin();
                }
        }
    }

    private boolean isuseridValid(String userid) {
        //TODO: Replace this with your own logic
        boolean flag = true;
        String u_id = preferences.getString(CommonString.KEY_USERNAME, "");
        if (!u_id.equals("") && !userid.equalsIgnoreCase(u_id)) {
            flag = false;
        }
        return flag;
    }

    private void AttempLogin() {
        try {
            loading = ProgressDialog.show(XxonLoginActivity.this, "Processing", "Please wait...", false, false);
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Userid", userid);
            jsonObject.put("Password", password);
            jsonObject.put("Intime", getCurrentTime());
            jsonObject.put("Latitude", lat);
            jsonObject.put("Longitude", lon);
            jsonObject.put("Appversion", app_ver);
            jsonObject.put("Attmode", "0");
            jsonObject.put("Networkstatus", "0");
            jsonObject.put("Manufacturer", manufacturer);
            jsonObject.put("ModelNumber", model);
            jsonObject.put("OSVersion", os_version);

            if (!imei1.equals("") && !imei2.equals("")) {
                jsonObject.put("IMEINumber1", imei1);
                jsonObject.put("IMEINumber2", imei2);
            } else if (!imei1.equals("") || imei2.equals("")) {
                jsonObject.put("IMEINumber1", imei1);
                jsonObject.put("IMEINumber2", "0");
            } else {
                jsonObject.put("IMEINumber1", "0");
                jsonObject.put("IMEINumber2", "0");
            }

            String jsonString = jsonObject.toString();
            try {
                final String[] data_global = {""};
                final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .build();

                RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
                adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                PostApi api = adapter.create(PostApi.class);
                Call<ResponseBody> call = api.getLogindetail(jsonData);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody = response.body();
                        String data = null;
                        if (responseBody != null && response.isSuccessful()) {
                            try {
                                data = response.body().string();
                                data = data.substring(1, data.length() - 1).replace("\\", "");
                                if (data.contains("Changed")) {
                                    loading.dismiss();
                                    AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_CHANGED);
                                } else if (data.contains("No data")) {
                                    loading.dismiss();
                                    AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_LOGIN_NO_DATA);

                                } else if (data.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                    AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.KEY_FAILURE + " Please try again");
                                    loading.dismiss();
                                } else {
                                    Gson gson = new Gson();
                                    final LoginGsonGetterSetter userObject = gson.fromJson(data, LoginGsonGetterSetter.class);
                                    //Download Todays Questions
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("Username", userid);
                                    jsonObject.put("Downloadtype", "Attendance_Status");
                                    String jsonString = jsonObject.toString();
                                    RequestBody questionjsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
                                    adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build();
                                    PostApi api1 = adapter.create(PostApi.class);
                                    Call<ResponseBody> callquest = api1.getDownloadAllUSINGLOGIN(questionjsonData);
                                    callquest.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            ResponseBody responseBodyforatt = response.body();
                                            String dataforatt = null;
                                            if (responseBodyforatt != null && response.isSuccessful()) {
                                                try {
                                                    dataforatt = response.body().string();
                                                    dataforatt = dataforatt.substring(1, dataforatt.length() - 1).replace("\\", "");
                                                    if (dataforatt.equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                                                        loading.dismiss();
                                                        AlertandMessages.showAlertlogin((Activity) context, "Check Your Internet Connection");
                                                    } else {
                                                        LoginGsonGetterSetter attendancestatus = null;
                                                        if (dataforatt.contains("No Data")) {
                                                            editor.putString(CommonString.KEY_ATTENDENCE_STATUS, "0");
                                                        } else {
                                                            Gson gson = new Gson();
                                                            attendancestatus = gson.fromJson(dataforatt, LoginGsonGetterSetter.class);
                                                            if (attendancestatus != null) {
                                                                editor.putString(CommonString.KEY_ATTENDENCE_STATUS, attendancestatus.getAttendanceStatus().get(0).getReasonId().toString());
                                                            }
                                                        }

                                                        ///addd crashlytics.
                                                        Crashlytics.setUserIdentifier(userid);
                                                        // PUT IN PREFERENCES
                                                        editor.putString(CommonString.KEY_USERNAME, userid);
                                                        editor.putString(CommonString.KEY_PASSWORD, password);
                                                        editor.putString(CommonString.KEY_VERSION, String.valueOf(userObject.getResult().get(0).getAppVersion()));
                                                        editor.putString(CommonString.KEY_PATH, userObject.getResult().get(0).getAppPath());
                                                        editor.putString(CommonString.KEY_DATE, userObject.getResult().get(0).getCurrentdate());
                                                        Date initDate = new SimpleDateFormat("MM/dd/yyyy").parse(userObject.getResult().get(0).getCurrentdate());
                                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                                                        String parsedDate = formatter.format(initDate);
                                                        editor.putString(CommonString.KEY_USER_TYPE, userObject.getResult().get(0).getRightname());

                                                        editor.putString(CommonString.KEY_YYYYMMDD_DATE, parsedDate);
                                                        editor.putString(CommonString.KEY_NOTICE_BOARD_LINK, userObject.getResult().get(0).getNotice_board());
                                                        editor.commit();

                                                        if (preferences.getString(CommonString.KEY_VERSION, "").equals(Integer.toString(versionCode))) {
                                                            loading.dismiss();
                                                            Intent intent = new Intent(getBaseContext(), MainMenuActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            loading.dismiss();
                                                            Intent intent = new Intent(getBaseContext(), AutoUpdateActivity.class);
                                                            intent.putExtra(CommonString.KEY_PATH, preferences.getString(CommonString.KEY_PATH, ""));
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    Crashlytics.logException(e);
                                                    loading.dismiss();
                                                    e.printStackTrace();
                                                    if (e != null) {
                                                        AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_SOCKETEXCEPTION + "(" + e.toString() + ")");
                                                    } else {
                                                        AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_SOCKETEXCEPTION);

                                                    }

                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            loading.dismiss();
                                            if (t != null) {
                                                if (t instanceof SocketTimeoutException || t instanceof IOException || t instanceof Exception) {
                                                    AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_INTERNET_NOT_AVALABLE + "(" + t.toString().toString() + ")");
                                                }
                                            } else {
                                                AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_SOCKETEXCEPTION);

                                            }
                                        }
                                    });
                                }

                            } catch (Exception e) {
                                Crashlytics.logException(e);
                                loading.dismiss();
                                e.printStackTrace();
                                if (e != null) {
                                    AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_SOCKETEXCEPTION + "(" + e.toString() + ")");
                                } else {
                                    AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_SOCKETEXCEPTION);

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loading.dismiss();
                        if (t != null) {
                            if (t instanceof SocketTimeoutException || t instanceof IOException || t instanceof Exception) {
                                AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_INTERNET_NOT_AVALABLE + "(" + t.toString().toString() + ")");
                            }
                        } else {
                            AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_SOCKETEXCEPTION);

                        }
                    }
                });

            } catch (Exception e) {
                loading.dismiss();
                e.printStackTrace();
                if (e != null) {
                    AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_SOCKETEXCEPTION + "(" + e.toString() + ")");
                } else {
                    AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_SOCKETEXCEPTION);
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            loading.dismiss();
            if (e != null) {
                AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_SOCKETEXCEPTION + "(" + e.toString() + ")");

            } else {
                AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_SOCKETEXCEPTION);
            }
        } catch (JSONException e) {
            loading.dismiss();
            if (e != null) {
                AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_INVALID_JSON + "(" + e.toString() + ")");

            } else {
                AlertandMessages.showAlertlogin(XxonLoginActivity.this, CommonString.MESSAGE_INVALID_JSON);

            }
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        boolean flag = true;
        String pw = preferences.getString(CommonString.KEY_PASSWORD, "");
        if (!pw.equals("") && !password.equals(pw)) {
            flag = false;
        }
        return flag;
    }

    public void getDeviceName() {
        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;
        os_version = Build.VERSION.RELEASE;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        finish();
                    }
                    default: {
                        break;
                    }
                }
                break;
        }

    }

    private void showToast(String message) {
        Snackbar.make(museridSignInButton, message, Snackbar.LENGTH_LONG).show();
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            imeiNumbers = imei.getDeviceImei();
        }
    }

    private void Ui_declaration() {
        context = this;
        tv_version = findViewById(R.id.tv_version_code);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        museridView = findViewById(R.id.userid);
        mPasswordView = findViewById(R.id.password);
//        museridView.setText("as.user1");
//        mPasswordView.setText("cpm123");
        museridSignInButton = findViewById(R.id.user_login_button);
        museridSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkNetIsAvailable()) {
                    attemptLogin();
                } else {
                    AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, false);
                }
            }
        });
        try {
            app_ver = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tv_version.setText("Version - " + app_ver);
        imei = new ImeiNumberClass(context);
        locationEnableCommon = new LocationEnableCommon();
        locationEnableCommon.checkgpsEnableDevice(this);
        imei = new ImeiNumberClass(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            imeiNumbers = imei.getDeviceImei();
            if (imeiNumbers.length == 2) {
                imei1 = imeiNumbers[0];
                imei2 = imeiNumbers[1];
            } else {
                imei1 = imeiNumbers[0];
                imei2 = "";
            }
        }

    }

    public boolean checkNetIsAvailable() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // we are connected to a network
            connected = true;
        }

        return connected;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Create a Folder for Images
        File file = new File(Environment.getExternalStorageDirectory(), ".XxonMobil_Images");
        if (!file.isDirectory()) {
            file.mkdir();
        }
    }
}
