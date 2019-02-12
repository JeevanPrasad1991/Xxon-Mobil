package jp.cpm.com.xxonmobil.dailyentry;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.GeoTag.GeoTaggingActivity;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.delegates.CoverageBean;
import jp.cpm.com.xxonmobil.download.DownloadActivity;
import jp.cpm.com.xxonmobil.gpsenable.LocationEnableCommon;
import jp.cpm.com.xxonmobil.gsonGetterSetter.JourneyPlan;
import jp.cpm.com.xxonmobil.retrofit.PostApi;
import jp.cpm.com.xxonmobil.upload.PreviousDataUploadActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class StoreListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private String userId, city_name, user_type;
    private ArrayList<JourneyPlan> storelist = new ArrayList<>();
    private String date;
    CardView beat_card;
    TextView storenamewithbeatname_txt;
    private Xxon_Database database;
    private ValueAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout linearlay;
    private Dialog dialog;
    private FloatingActionButton fab;
    private double lat = 0.0;
    private double lon = 0.0;
    SharedPreferences preferences;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private SharedPreferences.Editor editor = null;
    private LocationRequest mLocationRequest;
    LocationEnableCommon locationEnableCommon;
    private static final String TAG = StoreimageActivity.class.getSimpleName();
    int downloadIndex;
    ProgressDialog loading;
    CardView search_store_cardv;
    EditText edt_searchstore;
    Button btn_searchstore;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storelistfablayout);
        context = this;
        declaration();
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Download data
                if (checkNetIsAvailable()) {
                    if (database.isCoverageDataFilled(date)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                        builder.setTitle("Parinaam");
                        builder.setMessage("Please Upload Previous Data First")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(StoreListActivity.this, PreviousDataUploadActivity.class);
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
                        Intent startDownload = new Intent(StoreListActivity.this, DownloadActivity.class);
                        startActivity(startDownload);
                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                        finish();
                    }
                } else {
                    Snackbar.make(recyclerView, getString(R.string.nonetwork), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }

        });
    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        database.open();
        if (database.getcitymapping(date).size() > 1) {
            if (!database.iscurrentdateworkingwithstoreCheckIN(date)) {
                database.open();
                database.deleteJourneyPlan();
            }
        }

        if (!database.iscurrentDSRMoved(date)) {
            startActivity(new Intent(this, CityActivity.class));
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            StoreListActivity.this.finish();
        } else {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            StoreListActivity.this.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            database.open();
            if (database.getcitymapping(date).size() > 1) {
                if (!database.iscurrentdateworkingwithstoreCheckIN(date)) {
                    database.open();
                    database.deleteJourneyPlan();
                }
            }

            if (!database.iscurrentDSRMoved(date)) {
                startActivity(new Intent(this, CityActivity.class));
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                StoreListActivity.this.finish();
            } else {
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                StoreListActivity.this.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLocationChanged(Location location) {
    }

    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<JourneyPlan> data = Collections.emptyList();

        public ValueAdapter(Context context, List<JourneyPlan> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.storeviewlist, parent, false);
            return new MyViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @SuppressWarnings("deprecation")
        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
            final JourneyPlan current = data.get(position);
            String lastVisited = "";
            if (!current.getLastVisitDate().trim().equals("")) {
                lastVisited = "Last Visited : " + current.getLastVisitDate().trim();
            } else {
                lastVisited = "";
            }

            viewHolder.txt.setText("Segment - " + current.getStoreType().trim() + "\n" +
                    "Store Category - " + current.getStoreCategory().trim() + "( Id - " + current.getStoreId().toString() + " )" + " \n" + current.getStoreName().trim());
            viewHolder.address.setText(current.getAddress().trim() + " - " + current.getCity().trim() + "\n" + lastVisited.trim());

            if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_U)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_u);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                if (current.getStoreTypeId().toString().equals("1")) {
                    viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.blueforstorelist));
                } else {
                    viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
                }

            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_d);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                if (current.getStoreTypeId().toString().equals("1")) {
                    viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.blueforstorelist));
                } else {
                    viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
                }
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_P)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_p);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                if (current.getStoreTypeId().toString().equals("1")) {
                    viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.blueforstorelist));
                } else {
                    viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
                }

            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_C)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_c);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                if (current.getStoreTypeId().toString().equals("1")) {
                    viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.blueforstorelist));
                } else {
                    viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
                }
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                if (chekDataforCheckout(current, user_type)) {
                    viewHolder.chkbtn.setVisibility(View.VISIBLE);
                    viewHolder.imageview.setVisibility(View.VISIBLE);
                    viewHolder.imageview.setBackgroundResource(R.drawable.store);
                    viewHolder.imgeditforposm.setVisibility(View.GONE);

                    if (current.getStoreTypeId().toString().equals("1")) {
                        viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.blueforstorelist));
                    } else {
                        viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
                    }

                } else {
                    viewHolder.imageview.setVisibility(View.VISIBLE);
                    viewHolder.imageview.setBackgroundResource(R.drawable.store);
                    viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                    viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.green));
                }
            } else if (database.getSpecificCoverageData(current.getVisitDate(), current.getStoreId().toString()).size() > 0) {
                if (!database.getSpecificCoverageData(current.getVisitDate(), current.getStoreId().toString()).get(0).getReasonid().equals("0")) {
                    viewHolder.imageview.setVisibility(View.VISIBLE);
                    viewHolder.imageview.setBackgroundResource(R.drawable.leave_tick);
                    if (current.getStoreTypeId().toString().equals("1")) {
                        viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.blueforstorelist));
                    } else {
                        viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
                    }
                }
            } else if (current.getStoreTypeId().toString().equals("1")) {
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.blueforstorelist));
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.store);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.txt.setTextColor(Color.WHITE);
            } else {
                viewHolder.txt.setTextColor(Color.BLACK);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.storelist));
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.store);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
            }


            viewHolder.relativelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int store_Id = current.getStoreId();
                    if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_U)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_already_done, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_data_uploaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_C)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_already_checkout, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_P)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_again_uploaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                        Snackbar.make(v, R.string.title_store_list_activity_already_store_closed, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        boolean entry_flag = true;
                        database.open();
                        database.open();
                        if (database.isjcpStoreCheckInwithdentist_Id(date, store_Id)) {
                            entry_flag = false;
                        }
                        if (entry_flag) {
                            showMyDialog(current);
                        } else {
                            Snackbar.make(v, R.string.title_store_list_checkout_current, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                    }
                }
            });

            viewHolder.chkbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckNetAvailability()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this).setTitle(R.string.parinaam);
                        builder.setMessage(R.string.wantcheckout)
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(StoreListActivity.this, CheckoutActivty.class);
                                        intent.putExtra(CommonString.KEY_STORE_CD, current.getStoreId().toString());
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    }
                                })
                                .setNegativeButton(R.string.closed, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Snackbar.make(recyclerView, R.string.nonetwork, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                }
            });
        }

        @SuppressWarnings("deprecation")
        public boolean CheckNetAvailability() {
            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .getState() == NetworkInfo.State.CONNECTED
                    || connectivityManager.getNetworkInfo(
                    ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                // we are connected to a network
                connected = true;
            }
            return connected;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt, address;
            RelativeLayout relativelayout;
            ImageView imageview, imgeditforposm;
            Button chkbtn;
            CardView Cardbtn;

            public MyViewHolder(View itemView) {
                super(itemView);
                txt = itemView.findViewById(R.id.storelistviewxml_storename);
                address = itemView.findViewById(R.id.storelistviewxml_storeaddress);
                relativelayout = itemView.findViewById(R.id.storenamelistview_layout);
                imageview = itemView.findViewById(R.id.storelistviewxml_storeico);
                chkbtn = itemView.findViewById(R.id.chkout);
                imgeditforposm = itemView.findViewById(R.id.imageView2);
                Cardbtn = itemView.findViewById(R.id.card_view);
            }
        }
    }


    void showMyDialog(final JourneyPlan current) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        TextView menu_txt = dialog.findViewById(R.id.menu_txt);
        menu_txt.setText(getString(R.string.store_visited));

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    editor = preferences.edit();
                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "Yes");
                    editor.putString(CommonString.KEY_STORE_NAME, current.getStoreName());
                    editor.putString(CommonString.KEY_STORE_CD, current.getStoreId().toString());
                    editor.putString(CommonString.KEY_STORE_CATEGORY_Id, current.getStoreCategoryId().toString());
                    editor.putString(CommonString.KEY_STATE_ID, current.getStateId().toString());
                    editor.putString(CommonString.KEY_REGION_ID, current.getRegionId().toString());
                    editor.commit();

                    if (!current.getGeoTag().equalsIgnoreCase(CommonString.KEY_Y)) {
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage(getString(R.string.title_store_list_geo_tag)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog_c, int which) {
                                startActivity(new Intent(context, GeoTaggingActivity.class));
                                btn_searchstore.setText("Search");
                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                dialog_c.dismiss();
                            }
                        });
                        builder.show();
                    } else {
                        editor = preferences.edit();
                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "Yes");
                        editor.putString(CommonString.KEY_STORE_NAME, current.getStoreName());
                        editor.putString(CommonString.KEY_STORE_CD, current.getStoreId().toString());
                        editor.putString(CommonString.KEY_STORE_CATEGORY_Id, current.getStoreCategoryId().toString());
                        editor.putString(CommonString.KEY_STATE_ID, current.getStateId().toString());
                        editor.putString(CommonString.KEY_REGION_ID, current.getRegionId().toString());
                        editor.commit();

                        dialog.cancel();
                        ArrayList<CoverageBean> specdata;
                        specdata = database.getSpecificCoverageData(current.getVisitDate(), current.getStoreId().toString());
                        if (specdata.size() == 0) {
                            Intent in = new Intent(StoreListActivity.this, StoreimageActivity.class);
                            startActivity(in);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        } else {
                            Intent in = new Intent(StoreListActivity.this, StoreProfileActivity.class);
                            startActivity(in);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        }
                    }
                } else if (checkedId == R.id.no) {
                    dialog.cancel();
                    if (current.getUploadStatus().equals(CommonString.KEY_CHECK_IN)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                        builder.setMessage(CommonString.DATA_DELETE_ALERT_MESSAGE)
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                try {
                                                    deletecoverageData(StoreListActivity.this, current, user_type);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        UpdateData(current.getStoreId().toString(), current.getVisitDate());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(CommonString.KEY_STORE_CD, current.getStoreId().toString());
                        editor.commit();
                        Intent in = new Intent(StoreListActivity.this, NonWorkingReasonActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    }
                }
            }

        });
        dialog.show();
    }

    public void UpdateData(String storeCd, String visit_date) {
        database.open();
        database.deleteSpecificStoreData(storeCd);
        database.updateJaurneyPlanSpecificStoreStatus(storeCd, visit_date, "N");
        database.updateJaurneyPlanSpecificStoreofLastVisit_date(storeCd, "");
    }

    private void declaration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);
        linearlay = (LinearLayout) findViewById(R.id.no_data_lay);
        beat_card = (CardView) findViewById(R.id.city_card);
        storenamewithbeatname_txt = (TextView) findViewById(R.id.storenamewithbeatname_txt);
        recyclerView = (RecyclerView) findViewById(R.id.drawer_layout_recycle);
        userId = preferences.getString(CommonString.KEY_USERNAME, null);
        city_name = preferences.getString(CommonString.KEY_CITY_NAME, "");
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, "");
        //for filter store
        search_store_cardv = (CardView) findViewById(R.id.search_store_cardv);
        edt_searchstore = (EditText) findViewById(R.id.edt_searchstore);
        btn_searchstore = (Button) findViewById(R.id.btn_searchstore);


        getSupportActionBar().setTitle(getString(R.string.title_storeList));
        locationEnableCommon = new LocationEnableCommon();
        locationEnableCommon.checkgpsEnableDevice(this);
        database = new Xxon_Database(this);
        storenamewithbeatname_txt.setText("City Name - " + city_name + " - Date - " + date);
        database.open();

        btn_searchstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom(context, btn_searchstore);
                Button b = (Button) v;
                String buttonText = b.getText().toString();
                if (buttonText.equalsIgnoreCase("Search")) {
                    if (!edt_searchstore.getText().toString().isEmpty()) {
                        btn_searchstore.setText("Clear");
                    }
                    filterstorelist();

                } else if (buttonText.equalsIgnoreCase("Clear")) {
                    btn_searchstore.setText("Search");
                    edt_searchstore.setText("");
                    evaluatestorelistAdapter();
                }
            }
        });


    }


    @SuppressWarnings("deprecation")
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        database.open();
        storelist = database.getStoreData(date);
        downloadIndex = preferences.getInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
        if (storelist.size() > 0 && downloadIndex == 0) {
            adapter = new ValueAdapter(StoreListActivity.this, storelist);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            linearlay.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            beat_card.setVisibility(View.VISIBLE);
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        int UPDATE_INTERVAL = 500;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        int FATEST_INTERVAL = 100;
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        int DISPLACEMENT = 5;
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void startLocationUpdates() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (mLastLocation != null) {
                    lat = mLastLocation.getLatitude();
                    lon = mLastLocation.getLongitude();

                }
            }
            startLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }


    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.connect();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    private boolean chekDataforCheckout(JourneyPlan current, String user_type) {
        boolean status = false;
        if (user_type.equalsIgnoreCase("Merchandiser")) {
            if (database.getstoredeploymentList(current.getRegionId().toString(), current.getStoreCategoryId().toString()).size() > 0) {
                if (database.isposm_deployment(current.getStoreId().toString(), current.getVisitDate())) {
                    status = true;
                } else {
                    status = false;
                }
            } else {
                status = true;
            }
        } else {
            if (database.getStoreAuditHeaderData(current.getRegionId().toString(), current.getStoreCategoryId().toString()).size() > 0) {
                if (database.isstore_auditexist(current.getStoreId().toString())) {
                    status = true;
                } else {
                    status = false;
                }
            } else {
                status = true;
            }
        }

        return status;
    }

    private void deletecoverageData(final Context context, final JourneyPlan current, String user_type) {
        try {
            loading = ProgressDialog.show(StoreListActivity.this, "Processing", "Please wait...",
                    false, false);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("StoreId", current.getStoreId().toString());
            jsonObject.put("VisitDate", current.getVisitDate());
            jsonObject.put("UserId", userId);
            String jsonString2 = jsonObject.toString();
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString2.toString());
            Retrofit adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
            PostApi api = adapter.create(PostApi.class);
            retrofit2.Call<ResponseBody> call;
            if (user_type.equalsIgnoreCase("Merchandiser")) {
                call = api.deleteCoverageData(jsonData);
            } else {
                call = api.deleteCoverageDataAudit(jsonData);
            }

            call.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    ResponseBody responseBody = response.body();
                    String data = null;
                    if (responseBody != null && response.isSuccessful()) {
                        try {
                            data = response.body().string();
                            if (data.contains(CommonString.KEY_SUCCESS)) {
                                UpdateData(current.getStoreId().toString(), current.getVisitDate());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(CommonString.KEY_STORE_CD, current.getStoreId().toString());
                                editor.commit();
                                Intent intent = new Intent((Activity) context, NonWorkingReasonActivity.class);
                                intent.putExtra(CommonString.KEY_DEVIATION_NONWORKING, "0");
                                startActivity(intent);
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                loading.dismiss();
                            } else {
                                loading.dismiss();
                                AlertandMessages.showAlertlogin((Activity) context, data.toString());

                            }
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                            loading.dismiss();
                            AlertandMessages.showAlertlogin((Activity) context, e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                    loading.dismiss();
                    if (t instanceof SocketTimeoutException || t instanceof IOException || t instanceof Exception) {
                        AlertandMessages.showAlertlogin((Activity) context, getResources().getString(R.string.nonetwork));
                    } else {
                        AlertandMessages.showAlertlogin((Activity) context, getResources().getString(R.string.nonetwork));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loading.dismiss();
            AlertandMessages.showAlertlogin((Activity) context, getResources().getString(R.string.nonetwork));
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void filterstorelist() {
        if (!edt_searchstore.getText().toString().isEmpty()) {
            storelist = database.getfilteredJCPstore(edt_searchstore.getText().toString());
            if (storelist.size() > 0) {
                edt_searchstore.setText("");
                edt_searchstore.setHint(getString(R.string.titlenodadainedt_forfilterstore));
                adapter = new ValueAdapter(context, storelist);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                edt_searchstore.setText("");
                Snackbar.make(btn_searchstore, getString(R.string.titlenodentist), Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(btn_searchstore, getString(R.string.titlenodadainedt_forfilterstore), Snackbar.LENGTH_LONG).show();
        }
    }

    private void evaluatestorelistAdapter() {
        database.open();
        storelist = database.getStoreData(date);
        downloadIndex = preferences.getInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
        if (storelist.size() > 0 && downloadIndex == 0) {
            adapter = new ValueAdapter(context, storelist);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
    }

}


