package jp.cpm.com.xxonmobil.dailyentry;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.delegates.CoverageBean;
import jp.cpm.com.xxonmobil.retrofit.PostApi;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import jp.cpm.com.xxonmobil.upload.UploadDataActivity;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckoutActivty extends AppCompatActivity {
    String store_Id, visit_date, username;
    private SharedPreferences preferences;
    private Xxon_Database database;
    ArrayList<CoverageBean> specificDATa = new ArrayList<>();
    ProgressDialog loading;
    String app_ver,user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_activty);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, "");
        getSupportActionBar().setTitle(getString(R.string.store_checkout));
        store_Id = getIntent().getStringExtra(CommonString.KEY_STORE_CD);
        database = new Xxon_Database(this);
        database.open();
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        database.open();
        specificDATa = database.getSpecificCoverageData(visit_date, store_Id);
        try {
            if (specificDATa.size() > 0) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("UserId", username);
                jsonObject.put("StoreId", specificDATa.get(0).getStore_Id().toString());
                jsonObject.put("Latitude", specificDATa.get(0).getLatitude());
                jsonObject.put("Longitude", specificDATa.get(0).getLongitude());
                jsonObject.put("Checkout_Date", specificDATa.get(0).getVisitDate());
                uploadCoverageIntimeDATA(jsonObject.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    CheckoutActivty.this.finish();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                CheckoutActivty.this.finish();
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


    public void uploadCoverageIntimeDATA(String jsondata) {
        try {
            loading = ProgressDialog.show(CheckoutActivty.this, "Processing", "Please wait...",
                    false, false);
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsondata.toString());
            Retrofit adapter = new Retrofit.Builder().baseUrl(CommonString.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostApi api = adapter.create(PostApi.class);
            retrofit2.Call<ResponseBody> call;
            if (user_type.equalsIgnoreCase("Merchandiser")){
                 call = api.getCheckout(jsonData);
            }else {
                call = api. getCheckoutAudit(jsonData);
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
                                database.updateJaurneyPlanSpecificStoreStatus(store_Id, visit_date, CommonString.KEY_C);
                                database.updateJaurneyPlanSpecificStoreofLastVisit_date(store_Id, visit_date);
                                database.updateJaurneyPlanPosmEditableStatus(store_Id, visit_date, CommonString.KEY_STATUS_N);
                                Toast.makeText(CheckoutActivty.this, "Checkout Sucessfully.", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getBaseContext(), UploadDataActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                CheckoutActivty.this.finish();
                                loading.dismiss();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            loading.dismiss();
                            Crashlytics.logException(e);
                            if (e != null) {
                                AlertandMessages.showAlertlogin(CheckoutActivty.this, e.toString().toString());
                            } else {
                                AlertandMessages.showAlertlogin(CheckoutActivty.this, CommonString.MESSAGE_SOCKETEXCEPTION);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                    loading.dismiss();
                    if (t != null) {
                        if (t instanceof SocketTimeoutException || t instanceof IOException || t instanceof Exception) {
                            AlertandMessages.showAlertlogin(CheckoutActivty.this, t.getMessage().toString());
                        }
                    } else {
                        AlertandMessages.showAlertlogin(CheckoutActivty.this, CommonString.MESSAGE_SOCKETEXCEPTION);
                    }
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            loading.dismiss();
            Crashlytics.logException(e);
            if (e != null) {
                AlertandMessages.showAlertlogin(CheckoutActivty.this, e.toString().toString());
            } else {
                AlertandMessages.showAlertlogin(CheckoutActivty.this, CommonString.MESSAGE_SOCKETEXCEPTION);
            }
        }
    }
}
