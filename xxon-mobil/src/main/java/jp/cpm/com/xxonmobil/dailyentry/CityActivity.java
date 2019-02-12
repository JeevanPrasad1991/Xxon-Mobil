package jp.cpm.com.xxonmobil.dailyentry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.download.DownloadActivity;
import jp.cpm.com.xxonmobil.gsonGetterSetter.AllObjectGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.BeatName;
import jp.cpm.com.xxonmobil.gsonGetterSetter.JCPGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.MappingUserCity;
import jp.cpm.com.xxonmobil.retrofit.PostApi;
import jp.cpm.com.xxonmobil.upload.PreviousDataUploadActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CityActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private SharedPreferences.Editor editor = null;
    ArrayList<MappingUserCity> cityList = new ArrayList<>();
    String date, userId, city_Id = "", city_name = "", user_type;
    int downloadIndex;
    LinearLayout no_data_lay, RLDSR, searchlistRL;
    RecyclerView drawer_layout_recycle;
    ImageView img_cross;
    private Retrofit retrofit_Adapter;
    SharedPreferences preferences;
    boolean update_flag = true;
    FloatingActionButton fab;
    Xxon_Database db;
    ProgressDialog loading;
    Spinner dsr_spin;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsr);
        declaration();
    }

    private void declaration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        date = preferences.getString(CommonString.KEY_DATE, "");
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, "");
        no_data_lay = (LinearLayout) findViewById(R.id.no_data_lay);
        RLDSR = (LinearLayout) findViewById(R.id.rl_citylayout);
        searchlistRL = (LinearLayout) findViewById(R.id.searchlistRL);
        drawer_layout_recycle = (RecyclerView) findViewById(R.id.drawer_layout_recycle);
        dsr_spin = (Spinner) findViewById(R.id.city_spin);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        img_cross = (ImageView) findViewById(R.id.img_cross);

        userId = preferences.getString(CommonString.KEY_USERNAME, "");
        downloadIndex = preferences.getInt(CommonString.KEY_DOWNLOAD_INDEX, 0);

        getSupportActionBar().setTitle(getString(R.string.title_city) + " - " + date);
        db = new Xxon_Database(context);
        db.open();
        cityList = db.getcitymapping(date);
        if (cityList.size() > 0 && downloadIndex == 0) {
            searchlistRL.setVisibility(View.GONE);
            no_data_lay.setVisibility(View.GONE);
            RLDSR.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
        } else {
            no_data_lay.setVisibility(View.VISIBLE);
            searchlistRL.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            RLDSR.setVisibility(View.GONE);
        }


        //for non posm reason spinner
        MappingUserCity non = new MappingUserCity();
        non.setCity("-Select City-");
        non.setCityId(0);
        cityList.add(0, non);
        dsr_spin.setAdapter(new ReasonSpinnerAdapter(context, R.layout.spinner_text_view, cityList));
        dsr_spin.setOnItemSelectedListener(this);
        fab.setOnClickListener(this);
        img_cross.setOnClickListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            if (db.getCoverageData(date).size() > 0) {
                AlertandMessages.showAlertformovemain((Activity) context, getString(R.string.upload_first));
            } else {
                update_flag = false;
                city_Id = String.valueOf(cityList.get(position).getCityId());
                city_name = cityList.get(position).getCity();
                if (user_type.equalsIgnoreCase("Merchandiser")) {
                    downloadBeatListAndJourneyPlanData(context, userId, city_Id, CommonString.KEY_JOURNEY_PLAN);
                } else {
                    downloadBeatListAndJourneyPlanData(context, userId, city_Id, CommonString.KEY_JOURNEY_PLAN_AUDIT);
                }
            }
        } else {
            city_Id = "";
            city_name = "";
            searchlistRL.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (checkNetIsAvailable()) {
                    if (db.isCoverageDataFilled(date)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(getString(R.string.parinaam));
                        builder.setMessage("Please Upload Previous Data First")
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
                            db.open();
                            db.deletePreviousUploadedData(date);
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
                    Snackbar.make(fab, getString(R.string.nonetwork), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
                break;
            case R.id.img_cross:
                AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).
                        setTitle("Are you sure you want to close the Beats list ?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        searchlistRL.setVisibility(View.GONE);
                        dsr_spin.setSelection(0);
                        dialog.dismiss();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
        }

    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    public class DSRAdapter extends RecyclerView.Adapter<DSRAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<BeatName> data = Collections.emptyList();
        String dsr_name;

        public DSRAdapter(Context context, List<BeatName> data, String dsr_name) {
            inflator = LayoutInflater.from(context);
            this.dsr_name = dsr_name;
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.storeviewlist, parent, false);
            return new DSRAdapter.MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
            final BeatName current = data.get(position);
            viewHolder.txt.setText("Beat Name - " + current.getBeatName());
            viewHolder.txt.setTextColor(Color.BLACK);

            viewHolder.address.setText(current.getVisitDate());

            viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
            viewHolder.imageview.setBackgroundResource(R.drawable.route_icon);
            viewHolder.imageview.setVisibility(View.VISIBLE);
            viewHolder.chkbtn.setVisibility(View.INVISIBLE);

            viewHolder.relativelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  downloadBeatListAndJourneyPlanData(context, userId, city_Id, CommonString.KEY_JOURNEY_PLAN);
                }

            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt, address;
            RelativeLayout relativelayout;
            ImageView imageview;
            Button chkbtn;
            CardView Cardbtn;

            public MyViewHolder(View itemView) {
                super(itemView);
                txt = itemView.findViewById(R.id.storelistviewxml_storename);
                address = itemView.findViewById(R.id.storelistviewxml_storeaddress);
                relativelayout = itemView.findViewById(R.id.storenamelistview_layout);
                imageview = itemView.findViewById(R.id.storelistviewxml_storeico);
                chkbtn = itemView.findViewById(R.id.chkout);
                Cardbtn = itemView.findViewById(R.id.card_view);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (update_flag) {
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                CityActivity.this.finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                        CityActivity.this.finish();
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
            CityActivity.this.finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    CityActivity.this.finish();
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

    public class ReasonSpinnerAdapter extends ArrayAdapter<MappingUserCity> {
        List<MappingUserCity> list;
        Context context;
        int resourceId;

        public ReasonSpinnerAdapter(Context context, int resourceId, ArrayList<MappingUserCity> list) {
            super(context, resourceId, list);
            this.context = context;
            this.list = list;
            this.resourceId = resourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(resourceId, parent, false);
            MappingUserCity cm = list.get(position);
            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text);
            txt_spinner.setTextColor(Color.BLUE);
            txt_spinner.setText(list.get(position).getCity());

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(resourceId, parent, false);
            MappingUserCity cm = list.get(position);
            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text);
            txt_spinner.setTextColor(Color.WHITE);
            txt_spinner.setText(cm.getCity());

            return view;
        }

    }


    private void downloadBeatListAndJourneyPlanData(final Context context, final String userId, final String city_Id, final String KeydownloadType) {
        loading = ProgressDialog.show(context, "Processing", "Please wait...", false, false);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Downloadtype", KeydownloadType);
            jsonObject.put("Username", userId + ":" + city_Id);
            String jsonString = jsonObject.toString();
            try {
                final OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).connectTimeout(20, TimeUnit.SECONDS).build();
                RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
                retrofit_Adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
                PostApi api = retrofit_Adapter.create(PostApi.class);
                Call<String> call = api.getDownloadAll(jsonData);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseBody = response.body();
                        String data = null;
                        if (responseBody != null && response.isSuccessful()) {
                            try {
                                data = response.body();
                                if (data.equals("")) {
                                } else {
                                    if (!data.contains("No Data")) {
                                        if (user_type.equalsIgnoreCase("Merchandiser")) {
                                            JCPGetterSetter jcpObject = new Gson().fromJson(data, JCPGetterSetter.class);
                                            if (jcpObject != null) {
                                                editor = preferences.edit();
                                                editor.putString(CommonString.KEY_CITY_NAME, city_name);
                                                editor.commit();
                                                db.open();
                                                db.insertJCPData(jcpObject);
                                            }
                                        } else {
                                            AllObjectGetterSetter jcpObject = new Gson().fromJson(data, AllObjectGetterSetter.class);
                                            if (jcpObject != null) {
                                                editor = preferences.edit();
                                                editor.putString(CommonString.KEY_CITY_NAME, city_name);
                                                editor.commit();
                                                db.open();
                                                db.insertJCPDataforaudit(jcpObject);
                                            }
                                        }

                                        Intent intent = new Intent(context, StoreListActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                        CityActivity.this.finish();
                                        loading.dismiss();
                                    } else {
                                        AlertandMessages.showAlertlogin((Activity) context, "Journey Plan Data not found for '" + city_name + " .");
                                        loading.dismiss();
                                    }
                                }
                            } catch (Exception e) {
                                loading.dismiss();
                                AlertandMessages.showAlertlogin((Activity) context, KeydownloadType + " Data not found for '" + city_Id + " .");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        loading.dismiss();
                        AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE);
                    }
                });

            } catch (Exception e) {
                Crashlytics.logException(e);
                loading.dismiss();
                e.printStackTrace();
                if (e != null) {
                    AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION + "(" + e.toString() + ")");
                } else {
                    AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION);
                }
            }
        } catch (JSONException e) {
            loading.dismiss();
            AlertandMessages.showAlertlogin((Activity) context, CommonString.MESSAGE_INVALID_JSON);
        }
    }


}
