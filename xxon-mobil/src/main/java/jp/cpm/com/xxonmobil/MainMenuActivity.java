package jp.cpm.com.xxonmobil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.util.ArrayList;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.GeoTag.GeoTagStoreList;
import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.dailyentry.CityActivity;
import jp.cpm.com.xxonmobil.dailyentry.ReportActivity;
import jp.cpm.com.xxonmobil.dailyentry.ServiceActivity;
import jp.cpm.com.xxonmobil.dailyentry.StoreListActivity;
import jp.cpm.com.xxonmobil.delegates.CoverageBean;
import jp.cpm.com.xxonmobil.download.DownloadActivity;
import jp.cpm.com.xxonmobil.upload.PreviousDataUploadActivity;
import jp.cpm.com.xxonmobil.upload.UploadDataActivity;

public class MainMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private WebView webView;
    private ImageView imageView;
    private View headerView;
    private String error_msg;
    private Toolbar toolbar;
    private Context context;
    private int downloadIndex;
    private SharedPreferences preferences;
    Xxon_Database db;
    String visit_date, user_type, user_name, ATTENDENCE_STATUS;
    private ArrayList<CoverageBean> coverageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        declaration();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false);
        TextView tv_username = (TextView) headerView.findViewById(R.id.nav_user_name);
        TextView tv_usertype = (TextView) headerView.findViewById(R.id.nav_user_type);
        tv_username.setText(user_name);
        tv_usertype.setText(user_type);
        navigationView.addHeaderView(headerView);
        setTitle(getString(R.string.notice_board));
        navigationView.setNavigationItemSelectedListener(this);
        db = new Xxon_Database(context);
        db.open();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_route_plan) {
            db.open();
            if (ATTENDENCE_STATUS != null && ATTENDENCE_STATUS.equals("0")) {
                Intent in = new Intent(context, AttendanceActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            } else if (ATTENDENCE_STATUS != null && downloadIndex == 0 && db.getcitymapping(visit_date).size() > 0 && !db.checknonworkingreasonusingreason_Id(ATTENDENCE_STATUS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this).setTitle(getString(R.string.parinaam))
                        .setMessage(getString(R.string.present_action));
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(context, XxonLoginActivity.class));
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        MainMenuActivity.this.finish();
                        dialogInterface.dismiss();
                    }
                });

                builder.show();


            } else if (db.getStoreData(visit_date).size() == 0 && db.getcitymapping(visit_date).size() == 0) {
                Intent in = new Intent(context, CityActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            } else if (db.getcitymapping(visit_date).size() > 0 && db.getStoreData(visit_date).size() == 0) {
                Intent in = new Intent(context, CityActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            } else if (db.getcitymapping(visit_date).size() > 0 && db.getStoreData(visit_date).size() > 0 && !db.iscurrentDSRMoved(visit_date)) {
                Intent in = new Intent(context, CityActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }
            else if (db.getStoreData(visit_date).size() > 0) {
                Intent in = new Intent(context, StoreListActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }

        } else if (id == R.id.nav_download) {
            if (checkNetIsAvailable()) {
                if (!db.isCoverageDataFilled(visit_date)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);
                    builder.setTitle(getString(R.string.parinaam));
                    builder.setMessage(getResources().getString(R.string.want_download_data)).setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        db.open();
                                        db.deletePreviousUploadedData(visit_date);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Crashlytics.logException(e);
                                    }
                                    Intent in = new Intent(getApplicationContext(), DownloadActivity.class);
                                    startActivity(in);
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);
                    builder.setTitle(getString(R.string.parinaam));
                    builder.setMessage(getResources().getString(R.string.previous_data_upload)).setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent in = new Intent(getApplicationContext(), PreviousDataUploadActivity.class);
                                    startActivity(in);
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            } else {
                Snackbar.make(webView, getResources().getString(R.string.nonetwork), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }

        } else if (id == R.id.nav_upload) {
            if (checkNetIsAvailable()) {
                db.open();
                //getStoreData
                if (db.getcitymapping(visit_date).size() > 0 && downloadIndex == 0) {
                    if (coverageList.size() == 0) {
                        Snackbar.make(webView, R.string.no_data_for_upload, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    } else {
                        if (isStoreCheckedIn()) {
                            if (isValid()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);
                                builder.setTitle(getString(R.string.parinaam));
                                builder.setMessage(getResources().getString(R.string.want_upload_data)).setCancelable(false)
                                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent i = new Intent(getBaseContext(), UploadDataActivity.class);
                                                startActivity(i);
                                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                            }
                                        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                AlertandMessages.showSnackbarMsg(context, "No data for Upload");
                            }
                        } else {
                            Snackbar.make(webView, error_msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                    }
                } else {
                    Snackbar.make(webView, R.string.title_store_list_download_data, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            } else {
                Snackbar.make(webView, getResources().getString(R.string.nonetwork), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }

        } else if (id == R.id.nav_attendance) {
            if (ATTENDENCE_STATUS != null && ATTENDENCE_STATUS.equals("0")) {
                Intent in = new Intent(context, AttendanceActivity.class);
                startActivity(in);
            } else if (ATTENDENCE_STATUS != null && downloadIndex == 0 && db.getcitymapping(visit_date).size() > 0 && db.checknonworkingreasonusingreason_Id(ATTENDENCE_STATUS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this).setTitle(getString(R.string.parinaam))
                        .setMessage(getString(R.string.attendance_action));
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (db.getStoreData(visit_date).size() == 0 && db.getcitymapping(visit_date).size() == 0) {
                            Intent in = new Intent(context, CityActivity.class);
                            startActivity(in);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        } else if (db.getcitymapping(visit_date).size() > 0 && db.getStoreData(visit_date).size() == 0) {
                            Intent in = new Intent(context, CityActivity.class);
                            startActivity(in);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        } else if (db.getcitymapping(visit_date).size() > 0 && db.getStoreData(visit_date).size() > 0 && !db.iscurrentDSRMoved(visit_date)) {
                            Intent in = new Intent(context, CityActivity.class);
                            startActivity(in);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        } else if (db.getStoreData(visit_date).size() > 0) {
                            Intent in = new Intent(context, StoreListActivity.class);
                            startActivity(in);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        }
                        dialogInterface.dismiss();
                    }
                });

                builder.show();

            } else if (ATTENDENCE_STATUS != null && db.getcitymapping(visit_date).size() > 0 && downloadIndex == 0 && !db.checknonworkingreasonusingreason_Id(ATTENDENCE_STATUS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this).setTitle(getString(R.string.parinaam))
                        .setMessage(getString(R.string.present_action));
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(context, XxonLoginActivity.class));
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        dialogInterface.dismiss();
                    }
                });

                builder.show();
            }
        } else if (id == R.id.nav_exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(getString(R.string.parinaam)).setMessage("Do you want to Exit ?");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.finishAffinity(MainMenuActivity.this);
                    Intent intent = new Intent(getApplicationContext(), XxonLoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    finish();
                }
            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();

        } else if (id == R.id.nav_services) {
            Intent search = new Intent(this, ServiceActivity.class);
            startActivity(search);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        } else if (id == R.id.nav_geotag) {
            if (db.getcitymapping(visit_date).size()>0 && downloadIndex==0 && db.getStoreData(visit_date).size()>0){
                Intent search = new Intent(this, GeoTagStoreList.class);
                startActivity(search);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }else {
                Snackbar.make(webView, R.string.title_store_list_download_data, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }

        } else if (id == R.id.nav_report) {
            if (db.getcitymapping(visit_date).size() > 0 && downloadIndex == 0) {
                final Dialog dialog = new Dialog(MainMenuActivity.this);
                dialog.setCancelable(false);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.report);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                // set the custom dialog components - text, image and button
                CardView card_beatwise = (CardView) dialog.findViewById(R.id.card_beatwise);
                CardView card_dsrwisewise = (CardView) dialog.findViewById(R.id.card_dsrwisewise);
                ImageView img_cross = (ImageView) dialog.findViewById(R.id.img_cross);
                img_cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).
                                setTitle("Are you sure you want to close the Report Dialog ?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int which) {
                                dialog.dismiss();
                                dialog1.dismiss();
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int which) {
                                dialog.dismiss();
                                dialog1.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
                card_beatwise.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (db.getbeatwiseORDSRWiseReport(true).size() > 0) {
                            startActivity(new Intent(MainMenuActivity.this, ReportActivity.class).putExtra(CommonString.KEY_REPORT_FLAG, "0"));
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            dialog.dismiss();
                        } else {
                            Snackbar.make(webView, "BeatWise Report Data not available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            dialog.dismiss();
                        }
                    }
                });

                card_dsrwisewise.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (db.getbeatwiseORDSRWiseReport(false).size() > 0) {
                            startActivity(new Intent(MainMenuActivity.this, ReportActivity.class).putExtra(CommonString.KEY_REPORT_FLAG, "1"));
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            dialog.dismiss();
                        } else {
                            Snackbar.make(webView, "DSRWise Report Data not available.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            } else {
                Snackbar.make(webView, R.string.title_store_list_download_data, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isStoreCheckedIn() {
        boolean result_flag = true;
        for (int i = 0; i < coverageList.size(); i++) {
            db.open();
            if (db.getSpecificStoreDatawithdate(visit_date, coverageList.get(i).getStore_Id()).size() > 0) {
                db.open();
                if (db.isPJPCheckinformainmenu(visit_date, coverageList.get(i).getStore_Id())) {
                    result_flag = false;
                    error_msg = getResources().getString(R.string.title_store_list_checkout_current);
                    break;
                }
            }
        }
        return result_flag;
    }


    private boolean isValid() {
        boolean flag = false;
        String storestatus = "";
        for (int i = 0; i < coverageList.size(); i++) {
            db.open();
            if (db.getSpecificStoreDatawithdate(visit_date, coverageList.get(i).getStore_Id()).size() > 0) {
                storestatus = db.getSpecificStoreDatawithdate(visit_date, coverageList.get(i).getStore_Id()).get(0).getUploadStatus();
                if (!storestatus.equalsIgnoreCase(CommonString.KEY_U)) {
                    if ((storestatus.equalsIgnoreCase(CommonString.KEY_C) || storestatus.equalsIgnoreCase(CommonString.KEY_P) ||
                            storestatus.equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE) ||
                            storestatus.equalsIgnoreCase(CommonString.KEY_D))) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        if (!flag)
            error_msg = getResources().getString(R.string.no_data_for_upload);

        return flag;
    }


    void declaration() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        imageView = (ImageView) findViewById(R.id.img_main);
        webView = (WebView) findViewById(R.id.webview);
        visit_date = preferences.getString(CommonString.KEY_DATE, "");
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        user_name = preferences.getString(CommonString.KEY_USERNAME, null);
    }


    private boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            imageView.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
            view.clearCache(true);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.open();
        downloadIndex = preferences.getInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
        ATTENDENCE_STATUS = preferences.getString(CommonString.KEY_ATTENDENCE_STATUS, null);
        coverageList = db.getCoverageData(visit_date);
        if (checkNetIsAvailable()) {
            //load notice board url
            String url = preferences.getString(CommonString.KEY_NOTICE_BOARD_LINK, "");
            webView.setWebViewClient(new MyWebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            if (url != null && !url.equals("")) {
                webView.loadUrl(url);
            }
        } else {
            imageView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.INVISIBLE);
        }

        // Create a Folder for Images
        File file = new File(Environment.getExternalStorageDirectory(), ".GskGtTactical_Images");
        if (!file.isDirectory()) {
            file.mkdir();
        }
    }

}
