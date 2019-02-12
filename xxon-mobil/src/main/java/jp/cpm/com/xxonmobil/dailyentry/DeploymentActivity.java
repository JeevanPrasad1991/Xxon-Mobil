package jp.cpm.com.xxonmobil.dailyentry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.CommonFunctions;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.gsonGetterSetter.PosmMaster;

import static jp.cpm.com.xxonmobil.constant.CommonFunctions.getCurrentTime;

public class DeploymentActivity extends AppCompatActivity implements View.OnClickListener {
    String store_cd, visit_date, user_type, username, storeCategory_Id, state_Id, region_Id, city_name, store_name, _pathforcheck, _path, audit_img_one = "", audit_img_two = "";
    boolean checkflag = true, update_flag = false, isDialogOpen = true, spinnerTouched = false;
    ArrayList<PosmMaster> deploymentList = new ArrayList<>();
    String[] dentiststring = {"-Select Deployment-", "0", "1", "2"};
    private ArrayAdapter<String> reason_adapter;
    private SharedPreferences.Editor editor = null;
    private SharedPreferences preferences;
    RecyclerView deployment_recycl;
    FloatingActionButton dep_fab;
    ValueAdapter adapter;
    int child_position = -1;
    Xxon_Database db;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deployment);
        context = this;
        deploymentui_interface();
    }

    private void deploymentui_interface() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        deployment_recycl = (RecyclerView) findViewById(R.id.deployment_recycl);
        dep_fab = (FloatingActionButton) findViewById(R.id.storeAudit_fab);
        TextView storenamewithbeatname_txt = (TextView) findViewById(R.id.storenamewithbeatname_txt);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        storeCategory_Id = preferences.getString(CommonString.KEY_STORE_CATEGORY_Id, "");
        state_Id = preferences.getString(CommonString.KEY_STATE_ID, "");
        region_Id = preferences.getString(CommonString.KEY_REGION_ID, "");
        city_name = preferences.getString(CommonString.KEY_CITY_NAME, "");
        store_name = preferences.getString(CommonString.KEY_STORE_NAME, "");
        getSupportActionBar().setTitle((R.string.posm_deployment_text));
        storenamewithbeatname_txt.setText("Store Name - " + store_name + " (Id - " + store_cd + ")" + "\n" + "City Name - " + city_name + " - Date - " + visit_date);
        db = new Xxon_Database(context);
        db.open();
        prepareListData();

        dep_fab.setOnClickListener(this);

        deployment_recycl.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    dep_fab.hide();
                else if (dy < 0)
                    dep_fab.show();
            }
        });
    }


    private void prepareListData() {
        db.open();
        deploymentList = db.getinserteddeployment(store_cd, visit_date);
        if (deploymentList.size() == 0) {
            deploymentList = db.getstoredeploymentList(region_Id, storeCategory_Id);
        } else {
            dep_fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.edit_txt));
        }
        if (deploymentList.size() > 0) {
            adapter = new ValueAdapter(context, deploymentList);
            deployment_recycl.setAdapter(adapter);
            deployment_recycl.setLayoutManager(new LinearLayoutManager(context));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.storeAudit_fab:
                adapter.notifyDataSetChanged();
                deployment_recycl.clearFocus();
                if (validatecondition(deploymentList)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage((R.string.save_data)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.open();
                            db.insertdeploymentdata(store_cd, visit_date, deploymentList);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            DeploymentActivity.this.finish();
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
        }

    }

    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<PosmMaster> data;

        public ValueAdapter(Context context, List<PosmMaster> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.deployment_row_xml, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ValueAdapter.MyViewHolder holder, final int position) {
            final PosmMaster current = data.get(position);
            holder.lblListHeader.setText(current.getPosm().trim());
            holder.lblListHeader.setId(position);

            holder.deployment_spin.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    System.out.println("Real touch felt.");
                    spinnerTouched = true;
                    return false;
                }
            });

            reason_adapter = new ArrayAdapter<>(context, R.layout.spinner_custom_item, dentiststring);
            holder.deployment_spin.setAdapter(reason_adapter);
            reason_adapter.setDropDownViewResource(R.layout.spinner_custom_item);

            holder.deployment_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (spinnerTouched) {
                        //Do the stuff you only want triggered by real user interaction.
                        if (pos != 0) {
                            if (parent.getSelectedItem().toString().equals("0")) {
                                holder.RLdep_img.setVisibility(View.GONE);
                                holder.RLdep_img.setId(position);
                                current.setDeployment_img_two("");
                                current.setDeployment_img_one("");

                            } else if (parent.getSelectedItem().toString().equals("1")) {
                                holder.RLdep_img.setVisibility(View.VISIBLE);
                                holder.posm_img_two.setVisibility(View.GONE);
                                holder.deployment_img_one.setVisibility(View.VISIBLE);
                                holder.RLdep_img.setId(position);
                                holder.posm_img_two.setId(position);
                                holder.deployment_img_one.setId(position);
                                current.setDeployment_img_two("");

                            } else if (parent.getSelectedItem().toString().equals("2")) {
                                holder.RLdep_img.setVisibility(View.VISIBLE);
                                holder.posm_img_two.setVisibility(View.VISIBLE);
                                holder.deployment_img_one.setVisibility(View.VISIBLE);
                                holder.RLdep_img.setId(position);
                                holder.posm_img_two.setId(position);
                                holder.deployment_img_one.setId(position);

                            }
                            adapter.notifyDataSetChanged();
                            current.setPosm_deployment(parent.getSelectedItem().toString());
                        } else {
                            current.setPosm_deployment("");
                            holder.RLdep_img.setVisibility(View.GONE);
                            holder.RLdep_img.setId(position);
                            current.setDeployment_img_two("");
                            current.setDeployment_img_one("");
                        }
                    }
                    spinnerTouched = false;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (!current.getPosm_deployment().equals("")) {
                for (int i = 0; i < holder.deployment_spin.getCount(); i++) {
                    if (holder.deployment_spin.getItemAtPosition(i).toString().equalsIgnoreCase(current.getPosm_deployment())) {
                        holder.deployment_spin.setSelection(i);
                        break;
                    }
                }

            }

            if (!current.getPosm_deployment().equals("") && current.getPosm_deployment().equals("0")) {
                holder.RLdep_img.setVisibility(View.GONE);
                holder.RLdep_img.setId(position);
            } else if (!current.getPosm_deployment().equals("") && current.getPosm_deployment().equals("1")) {
                holder.RLdep_img.setVisibility(View.VISIBLE);
                holder.posm_img_two.setVisibility(View.GONE);
                holder.deployment_img_one.setVisibility(View.VISIBLE);
                holder.RLdep_img.setId(position);
                holder.posm_img_two.setId(position);
                holder.deployment_img_one.setId(position);
            } else if (!current.getPosm_deployment().equals("") && current.getPosm_deployment().equals("2")) {
                holder.RLdep_img.setVisibility(View.VISIBLE);
                holder.posm_img_two.setVisibility(View.VISIBLE);
                holder.deployment_img_one.setVisibility(View.VISIBLE);
                holder.RLdep_img.setId(position);
                holder.posm_img_two.setId(position);
                holder.deployment_img_one.setId(position);
            }

            holder.deployment_img_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    child_position = position;
                    _pathforcheck = store_cd + "_" + current.getPosmId().toString() + "_DEPLONEIMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                    _path = CommonString.FILE_PATH + _pathforcheck;
                    CommonFunctions.startAnncaCameraActivity(context, _path);
                }
            });


            holder.posm_img_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    child_position = position;
                    _pathforcheck = store_cd + "_" + current.getPosmId().toString() + "_DEPLTWOIMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                    _path = CommonString.FILE_PATH + _pathforcheck;
                    CommonFunctions.startAnncaCameraActivity(context, _path);
                }
            });


            if (!audit_img_one.equals("")) {
                if (child_position == position) {
                    current.setDeployment_img_one(audit_img_one);
                    audit_img_one = "";
                }
            }

            if (!audit_img_two.equals("")) {
                if (child_position == position) {
                    current.setDeployment_img_two(audit_img_two);
                    audit_img_two = "";
                }
            }

            if (!current.getDeployment_img_one().equals("")) {
                holder.deployment_img_one.setImageResource(R.drawable.ic_menu_camera_done);
                holder.deployment_img_one.setId(position);
            } else {
                holder.deployment_img_one.setImageResource(R.drawable.ic_menu_camera);
                holder.deployment_img_one.setId(position);
            }

            if (!current.getDeployment_img_two().equals("")) {
                holder.posm_img_two.setImageResource(R.drawable.ic_menu_camera_done);
                holder.posm_img_two.setId(position);
            } else {
                holder.posm_img_two.setImageResource(R.drawable.ic_menu_camera);
                holder.posm_img_two.setId(position);
            }

            if (!checkflag) {
                boolean flag = false;
                if (current.getPosm_deployment().equals("")) {
                    flag = true;
                } else if (current.getPosm_deployment().equals("1") && current.getDeployment_img_one().equals("")) {
                    flag = true;
                } else if (current.getPosm_deployment().equals("2")) {
                    if (current.getDeployment_img_one().equals("") || current.getDeployment_img_two().equals("")) {
                        flag = true;
                    }
                }

                if (flag) {
                    holder.card_view.setCardBackgroundColor(getResources().getColor(R.color.red));
                    holder.card_view.setId(position);
                } else {
                    holder.card_view.setCardBackgroundColor(getResources().getColor(R.color.white));
                    holder.card_view.setId(position);
                }
            }

        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView lblListHeader;
            Spinner deployment_spin;
            LinearLayout RLdep_img;
            ImageView deployment_img_one, posm_img_two;
            CardView card_view;

            public MyViewHolder(View itemView) {
                super(itemView);
                lblListHeader = (TextView) itemView.findViewById(R.id.lblListHeader);
                deployment_spin = (Spinner) itemView.findViewById(R.id.dep_edt);
                RLdep_img = (LinearLayout) itemView.findViewById(R.id.RLdep_img);
                deployment_img_one = (ImageView) itemView.findViewById(R.id.deployment_img_one);
                posm_img_two = (ImageView) itemView.findViewById(R.id.posm_img_two);
                card_view = (CardView) itemView.findViewById(R.id.card_view);
            }
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
                            if (_pathforcheck.contains("_DEPLTWOIMG_")) {
                                String metadata = CommonFunctions.setMetadataAtImages(store_name, store_cd, "Deployment Image Two", username);
                                CommonFunctions.addMetadataAndTimeStampToImage(context, _path, metadata, visit_date);
                                audit_img_two = _pathforcheck;
                            } else {
                                String metadata = CommonFunctions.setMetadataAtImages(store_name, store_cd, "Deployment Image One", username);
                                CommonFunctions.addMetadataAndTimeStampToImage(context, _path, metadata, visit_date);
                                audit_img_one = _pathforcheck;
                            }
                            adapter.notifyDataSetChanged();
                            _pathforcheck = "";
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        e.printStackTrace();
                    }
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private boolean validatecondition(ArrayList<PosmMaster> deploymentList) {
        checkflag = true;
        for (int k = 0; k < deploymentList.size(); k++) {
            if (deploymentList.get(k).getPosm_deployment().equals("")) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage("Please select Deployment of " + deploymentList.get(k).getPosm().trim()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                checkflag = false;
                break;
            } else if (deploymentList.get(k).getPosm_deployment().equals("1") && deploymentList.get(k).getDeployment_img_one().equals("")) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage("Please Capture Deployment image of this " + deploymentList.get(k).getPosm().trim()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                checkflag = false;
                break;
            } else if (deploymentList.get(k).getPosm_deployment().equals("2") && deploymentList.get(k).getDeployment_img_two().equals("") || deploymentList.get(k).getPosm_deployment().equals("2") && deploymentList.get(k).getDeployment_img_one().equals("")) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage("Please Capture Deployment image of this " + deploymentList.get(k).getPosm().trim()).
                        setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alert.show();
                checkflag = false;
                break;
            } else {
                checkflag = true;
            }

        }
        return checkflag;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                        DeploymentActivity.this.finish();
                        dialog.dismiss();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            DeploymentActivity.this.finish();
                            dialog.dismiss();
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
        return super.onOptionsItemSelected(item);
    }


}
