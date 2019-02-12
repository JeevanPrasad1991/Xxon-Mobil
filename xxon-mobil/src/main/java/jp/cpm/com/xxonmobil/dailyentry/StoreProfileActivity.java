package jp.cpm.com.xxonmobil.dailyentry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.io.File;
import java.util.ArrayList;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonFunctions;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.gettersetter.StoreProfileGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.JourneyPlan;

import static jp.cpm.com.xxonmobil.constant.CommonFunctions.getCurrentTime;

public class StoreProfileActivity extends AppCompatActivity implements View.OnClickListener {
    EditText storeProfile_address, storeProfile_contctN, store_name, profileEmailId, pincode, profile_remark, retailer_name, ageingof_branding;
    String visit_date, userId, user_type, store_Id, visiting_card_img = "", _pathforcheck, _path;
    TextView storeProfile_City, profile_store_Id, store_category, segment, distributor;
    ImageView visiting_card;
    FloatingActionButton btn_save, btn_next;
    StoreProfileGetterSetter storePGT;
    SharedPreferences preferences;
    boolean update_flag = false;
    Xxon_Database db;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile);
        context = this;
        db = new Xxon_Database(context);
        db.open();
        declaration();
        validate();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, StoreEntryActivity.class));
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                StoreProfileActivity.this.finish();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_next.setVisibility(View.GONE);
                uienble();
                btn_save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.save_icon));
                if (update_flag) {
                    if (checkCondition()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage(R.string.alertsaveData);
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.open();
                                storePGT = new StoreProfileGetterSetter();
                                storePGT.setStore_name(store_name.getText().toString().trim().replaceAll("[(!@#$%^&*?)\"]", ""));
                                storePGT.setProfileAddress(storeProfile_address.getText().toString().trim().replaceAll("[(!@#$%^&*?)\"]", ""));
                                storePGT.setProfileCity(storeProfile_City.getText().toString().trim().replaceAll("[(!@#$%^&*?)\"]", ""));
                                storePGT.setPin_Code(pincode.getText().toString());
                                storePGT.setEmail_Id(profileEmailId.getText().toString());
                                storePGT.setProfileContact(storeProfile_contctN.getText().toString());
                                storePGT.setRetailer_name(retailer_name.getText().toString().trim().replaceAll("[(!@#$%^&*?)\"]", ""));
                                storePGT.setAgeingof_branding(ageingof_branding.getText().toString().trim().replaceAll("[(!@#$%^&*?)\"]", ""));
                                storePGT.setCategory_name(store_category.getText().toString().trim().replaceAll("[(!@#$%^&*?)\"]", ""));
                                storePGT.setSegment(segment.getText().toString().trim().replaceAll("[(!@#$%^&*?)\"]", ""));
                                storePGT.setDistributor(distributor.getText().toString());
                                storePGT.setVisiting_card(visiting_card_img);
                                storePGT.setProfile_remark(profile_remark.getText().toString().trim().replaceAll("[(!@#$%^&*?)\"]", ""));
                                db.insertStoreProfileData(store_Id, visit_date, storePGT);

                                btn_next.setVisibility(View.VISIBLE);
                                btn_save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit_txt));
                                uidisableEnable(true);
                                dialogInterface.dismiss();
                                update_flag = false;
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        builder.show();
                    }
                }
                update_flag = true;
            }
        });
    }

    private void validate() {
        update_flag = false;
        db.open();
        storePGT = db.getStoreProfileData(store_Id, visit_date);
        if (storePGT != null && storePGT.getStore_name() != null) {
            uidisableEnable(true);
            try {
                btn_save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit_txt));
                profile_store_Id.setText(store_Id);
                storeProfile_address.setText(storePGT.getProfileAddress());
                storeProfile_contctN.setText(storePGT.getProfileContact());
                store_name.setText(storePGT.getStore_name());
                profileEmailId.setText(storePGT.getEmail_Id());
                pincode.setText(storePGT.getPin_Code());
                profile_remark.setText(storePGT.getProfile_remark());
                retailer_name.setText(storePGT.getRetailer_name());
                ageingof_branding.setText(storePGT.getAgeingof_branding());
                storeProfile_City.setText(storePGT.getProfileCity());
                store_category.setText(storePGT.getCategory_name());
                segment.setText(storePGT.getSegment());
                distributor.setText(storePGT.getDistributor());
                if (!storePGT.getVisiting_card().equals("")) {
                    visiting_card.setImageResource(R.drawable.ic_menu_camera_done);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            uidisableEnable(true);
            if (db.getSpecificCoverageData(visit_date, store_Id).size() > 0) {
                try {
                    ArrayList<JourneyPlan> specifDATA = db.getSpecificStoreData(store_Id);

                    profile_store_Id.setText(store_Id);
                    storeProfile_address.setText(specifDATA.get(0).getAddress());
                    storeProfile_contctN.setText(specifDATA.get(0).getMobileNo());
                    store_name.setText(specifDATA.get(0).getStoreName());
                    profileEmailId.setText(specifDATA.get(0).getStoreEmail());
                    pincode.setText(specifDATA.get(0).getPincode());
                    profile_remark.setText("");
                    retailer_name.setText(specifDATA.get(0).getContactPerson());
                    ageingof_branding.setText(specifDATA.get(0).getAgeing_Of_Branding());
                    storeProfile_City.setText(specifDATA.get(0).getCity());
                    store_category.setText(specifDATA.get(0).getStoreCategory());
                    distributor.setText(specifDATA.get(0).getDistributor());
                    segment.setText(specifDATA.get(0).getStoreType());
                    if (!specifDATA.get(0).getVisitingCardPic().equals("")) {
                        visiting_card.setImageResource(R.drawable.ic_menu_camera_done);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            }
        }
    }

    private void declaration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_save = (FloatingActionButton) findViewById(R.id.btn_save);
        btn_next = (FloatingActionButton) findViewById(R.id.btn_next);

        storeProfile_address = (EditText) findViewById(R.id.storeProfile_address);
        storeProfile_contctN = (EditText) findViewById(R.id.storeProfile_contctN);
        store_name = (EditText) findViewById(R.id.store_name);
        profileEmailId = (EditText) findViewById(R.id.profileEmailId);
        pincode = (EditText) findViewById(R.id.pincode);
        profile_remark = (EditText) findViewById(R.id.profile_remark);
        retailer_name = (EditText) findViewById(R.id.retailer_name);
        ageingof_branding = (EditText) findViewById(R.id.ageingof_branding);


        profile_store_Id = (TextView) findViewById(R.id.profile_store_Id);
        storeProfile_City = (TextView) findViewById(R.id.storeProfile_City);
        store_category = (TextView) findViewById(R.id.store_category);
        segment = (TextView) findViewById(R.id.segment);
        distributor = (TextView) findViewById(R.id.distributor);
        //   new changes 12/12/2018
        visiting_card = (ImageView) findViewById(R.id.visiting_card);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        userId = preferences.getString(CommonString.KEY_USERNAME, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        store_Id = preferences.getString(CommonString.KEY_STORE_CD, null);
        profile_store_Id.setText(store_Id);
        setTitle("Store Profile - " + visit_date);
        db.open();

        visiting_card.setOnClickListener(this);
    }


    private boolean checkCondition() {
        boolean status = false;
        if (store_name.getText().toString().isEmpty()) {
            meassage(CommonString.forclinic_name);
        } else if (storeProfile_address.getText().toString().isEmpty()) {
            meassage(CommonString.for_clinic_address);
        } else if (storeProfile_City.getText().toString().isEmpty()) {
            meassage(CommonString.forcity);
        } else if (pincode.getText().toString().isEmpty()) {
            meassage(CommonString.forpincode);
        } else if (!pincode.getText().toString().isEmpty() && pincode.getText().toString().length() < 6) {
            meassage(CommonString.forvalidPinCode);
        } else if (profileEmailId.getText().toString().isEmpty()) {
            meassage(CommonString.foremail_Id);
        } else if (!profileEmailId.getText().toString().isEmpty() && !AlertandMessages.isValidEmail(profileEmailId.getText().toString())) {
            meassage(CommonString.forValidEmail);
        } else if (storeProfile_contctN.getText().toString().isEmpty()) {
            meassage(CommonString.formobilenumber);
        } else if (!storeProfile_contctN.getText().toString().isEmpty() && storeProfile_contctN.getText().toString().length() < 10) {
            meassage(CommonString.stpcontactnolenght);
        } else if (retailer_name.getText().toString().isEmpty()) {
            meassage(CommonString.for_retailer_name);
        } else if (ageingof_branding.getText().toString().isEmpty()) {
            meassage(CommonString.for_ageingof_branding);
        } else if (visiting_card_img.equals("")) {
            meassage(CommonString.for_visiting_card);
        } else if (profile_remark.getText().toString().isEmpty()) {
            meassage(CommonString.for_remark);
        } else {
            status = true;
        }
        return status;
    }

    private void meassage(String msg) {
        Snackbar.make(btn_save, msg, Snackbar.LENGTH_LONG).show();
    }

    private void uidisableEnable(boolean status) {
        if (status) {
            store_name.setEnabled(false);
            storeProfile_address.setEnabled(false);
            storeProfile_contctN.setEnabled(false);
            pincode.setEnabled(false);
            profileEmailId.setEnabled(false);
            profileEmailId.setEnabled(false);
            profile_remark.setEnabled(false);
            retailer_name.setEnabled(false);
            ageingof_branding.setEnabled(false);
        } else {
            store_name.setEnabled(true);
            storeProfile_address.setEnabled(true);
            storeProfile_contctN.setEnabled(true);
            pincode.setEnabled(true);
            profileEmailId.setEnabled(true);
            profileEmailId.setEnabled(true);
            profile_remark.setEnabled(true);
            retailer_name.setEnabled(true);
            ageingof_branding.setEnabled(true);
        }

    }

    private boolean uienble() {
        store_name.setEnabled(true);
        storeProfile_address.setEnabled(true);
        storeProfile_contctN.setEnabled(true);
        pincode.setEnabled(true);
        profileEmailId.setEnabled(true);
        profileEmailId.setEnabled(true);
        profile_remark.setEnabled(true);
        retailer_name.setEnabled(true);
        ageingof_branding.setEnabled(true);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (!update_flag) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            StoreProfileActivity.this.finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // finish();
                            update_flag = false;
                            btn_next.setVisibility(View.VISIBLE);
                            btn_save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit_txt));
                            uidisableEnable(true);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!update_flag) {
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                StoreProfileActivity.this.finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                update_flag = false;
                                btn_next.setVisibility(View.VISIBLE);
                                btn_save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit_txt));
                                uidisableEnable(true);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.visiting_card:
                _pathforcheck = store_Id + "_VISITINGIMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                CommonFunctions.startAnncaCameraActivity(context, _path);
                break;
        }

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
                            //Set Clicked image to Imageview
                            String metadata = CommonFunctions.setMetadataAtImages(preferences.getString(CommonString.KEY_STORE_NAME, ""), store_Id, "Visiting Image", userId);
                            CommonFunctions.addMetadataAndTimeStampToImage(context, _path, metadata, visit_date);
                            visiting_card.setImageResource(R.drawable.ic_menu_camera_done);
                            visiting_card_img = _pathforcheck;
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

}
