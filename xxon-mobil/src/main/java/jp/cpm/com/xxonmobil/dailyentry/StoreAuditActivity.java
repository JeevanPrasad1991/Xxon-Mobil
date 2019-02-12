package jp.cpm.com.xxonmobil.dailyentry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.CommonFunctions;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.gsonGetterSetter.NonPosmReason;
import jp.cpm.com.xxonmobil.gsonGetterSetter.PosmMaster;

public class StoreAuditActivity extends AppCompatActivity {
    String store_cd, visit_date, user_type, username, storeCategory_Id, state_Id, region_Id, city_name, store_name, _pathforcheck, _path, audit_img_one = "", audit_img_two = "";
    boolean checkflag = true, update_flag = false, isDialogOpen = true;
    String[] posminstalledspinnvalue = {"-Select-", "Yes", "No"};//array of strings used to populate the spinner
    ArrayList<Integer> checkHeaderArray = new ArrayList<>();
    HashMap<PosmMaster, List<PosmMaster>> listDataChild;
    private SharedPreferences.Editor editor = null;
    int grp_position = -1, child_position = -1;
    MultiPurposeDialog multiPurposeDialog;
    private SharedPreferences preferences;
    FloatingActionButton storePOSM_fab;
    ExpandableListAdapter listAdapter;
    List<PosmMaster> listDataHeader;
    List<PosmMaster> posmChildList;
    ExpandableListView lvExp_posm;
    Xxon_Database db;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_audit);
        context = this;
        posmUiData();
        //save audit data
        storePOSM_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lvExp_posm.clearFocus();
                lvExp_posm.invalidateViews();
                listAdapter.notifyDataSetChanged();
                if (validateData(listDataChild, listDataHeader)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage(R.string.alertsaveData);
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.open();
                            db.insertStorePosmListData(store_cd, visit_date, listDataChild, listDataHeader);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            StoreAuditActivity.this.finish();
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
        });


        lvExp_posm.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (firstVisibleItem == 0) {
                    storePOSM_fab.setVisibility(View.VISIBLE);
                } else if (lastItem == totalItemCount) {
                    storePOSM_fab.setVisibility(View.INVISIBLE);
                } else {
                    storePOSM_fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                lvExp_posm.invalidateViews();
                lvExp_posm.clearFocus();
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
            }
        });

        // Listview Group click listener
        lvExp_posm.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        // Listview Group expanded listener
        lvExp_posm.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                lvExp_posm.invalidateViews();
                lvExp_posm.clearFocus();
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getWindow().getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
            }
        });

        // Listview Group collasped listener
        lvExp_posm.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                lvExp_posm.invalidateViews();
                lvExp_posm.clearFocus();
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getWindow().getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
            }
        });

        // Listview on child click listener
        lvExp_posm.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });

    }

    private void posmUiData() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lvExp_posm = findViewById(R.id.lvExp_audit);
        storePOSM_fab = findViewById(R.id.storeAudit_fab);
        TextView storenamewithbeatname_txt = (TextView) findViewById(R.id.storenamewithbeatname_txt);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
        getSupportActionBar().setTitle((R.string.posm_text));
        storenamewithbeatname_txt.setText("Store Name - " + store_name + " (Id - " + store_cd + ")" + "\n" + "City Name - " + city_name + " - Date - " + visit_date);
        db = new Xxon_Database(this);
        db.open();
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        lvExp_posm.setAdapter(listAdapter);
    }

    private void prepareListData() {
        db.open();
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader = db.getinsertedauditHeaderData(store_cd, visit_date);
        if (listDataHeader.size() == 0) {
            listDataHeader = db.getStoreAuditHeaderData(region_Id, storeCategory_Id);
        }
        if (listDataHeader.size() > 0) {
            for (int i = 0; i < listDataHeader.size(); i++) {
                posmChildList = db.getstoreposminsertedData(store_cd, listDataHeader.get(i).getPosmId().toString());
                if (posmChildList.size() > 0) {
                    update_flag = true;
                    storePOSM_fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.edit_txt));
                } else {
                    posmChildList = db.getposmchecklistby_posmId(listDataHeader.get(i).getPosmId());
                }
                listDataChild.put(listDataHeader.get(i), posmChildList); // Header, Child data
            }
        }
    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context _context;
        private List<PosmMaster> _listDataHeader;
        private HashMap<PosmMaster, List<PosmMaster>> _listDataChild;

        public ExpandableListAdapter(Context context, List<PosmMaster> listDataHeader, HashMap<PosmMaster, List<PosmMaster>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final PosmMaster childText = (PosmMaster) getChild(groupPosition, childPosition);
            final PosmMaster headerTitle = (PosmMaster) getGroup(groupPosition);
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item_parent_storeposm, null);
                holder = new ViewHolder();
                holder.cardView = (CardView) convertView.findViewById(R.id.card_view);
                holder.checklist_spin = (Spinner) convertView.findViewById(R.id.checklist_spin);
                holder.checklist_name = (TextView) convertView.findViewById(R.id.checklist_name);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.checklist_name.setText(childText.getChecklist().trim());
            holder.checklist_name.setId(childPosition);

            //for non posm reason spinner
            final ArrayList<NonPosmReason> checklistans_list = db.getnonposmreason(childText.getChecklist_Id());
            NonPosmReason non = new NonPosmReason();
            non.setPreason("-Select Ans-");
            non.setPreasonId(0);
            checklistans_list.add(0, non);
            holder.checklist_spin.setAdapter(new ReasonSpinnerAdapter(_context, R.layout.spinner_text_forposm, checklistans_list));
            for (int i = 0; i < checklistans_list.size(); i++) {
                if (checklistans_list.get(i).getPreasonId().toString().equals(childText.getCurrectans_Ic())) {
                    holder.checklist_spin.setSelection(i);
                    break;
                }
            }

            holder.checklist_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (pos != 0) {
                        checklistans_list.get(pos);
                        childText.setCurrectans_Ic(checklistans_list.get(pos).getPreasonId().toString());
                        childText.setCurrectans(checklistans_list.get(pos).getPreason());
                    } else {
                        childText.setCurrectans_Ic("0");
                        childText.setCurrectans("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (!checkflag) {
                boolean tempflag = false;
                if (headerTitle.getPosm_deployment().equals("1")) {
                    if (childText.getCurrectans_Ic().equals("0")) {
                        tempflag = true;
                    }
                }
                if (tempflag) {
                    holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                }
            } else {
                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
            }

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final PosmMaster headerTitle = (PosmMaster) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group_storeaudit, null);
            }
            CardView card_view = convertView.findViewById(R.id.card_view);
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            final ImageView img_posm_planogram = (ImageView) convertView.findViewById(R.id.img_posm_planogram);
            final Spinner posminstall_spin = (Spinner) convertView.findViewById(R.id.posminstall_spin);
            final Spinner posmremark_spin = (Spinner) convertView.findViewById(R.id.posmremark_spin);
            final LinearLayout RLposm_img = (LinearLayout) convertView.findViewById(R.id.RLposm_img);
            final LinearLayout RLposm_nonreeason = (LinearLayout) convertView.findViewById(R.id.RLposm_nonreeason);
            final LinearLayout RLposm_remarkothers = (LinearLayout) convertView.findViewById(R.id.RLposm_remarkothers);
            final EditText edt_remarkothers = (EditText) convertView.findViewById(R.id.edt_remarkothers);
            final ImageView deployment_img_one = (ImageView) convertView.findViewById(R.id.deployment_img_one);
            final ImageView posm_img_two = (ImageView) convertView.findViewById(R.id.posm_img_two);
            View viewaudit = (View) convertView.findViewById(R.id.view_audit);
            if (groupPosition == 0) {
                viewaudit.setVisibility(View.GONE);
            } else {
                viewaudit.setVisibility(View.VISIBLE);
            }

            card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (posminstall_spin.getSelectedItemId() == 1) {
                        if (!headerTitle.getDeployment_img_one().equals("") || !headerTitle.getDeployment_img_two().equals("")) {
                            if (lvExp_posm.isGroupExpanded(groupPosition)) {
                                lvExp_posm.collapseGroup(groupPosition);
                            } else {
                                lvExp_posm.expandGroup(groupPosition);
                            }
                        } else {
                            if (isDialogOpen) {
                                isDialogOpen = !isDialogOpen;
                                AlertDialog.Builder alert = new AlertDialog.Builder(_context).setTitle(getString(R.string.parinaam)).setMessage("Please click atleast one image of " + headerTitle.getPosm()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isDialogOpen = !isDialogOpen;
                                        dialog.dismiss();
                                    }
                                });
                                alert.show();
                            }
                        }
                    } else if (posminstall_spin.getSelectedItemId() == 2) {
                        if (posmremark_spin.getSelectedItemId() == 0) {
                            if (isDialogOpen) {
                                isDialogOpen = !isDialogOpen;
                                AlertDialog.Builder alert = new AlertDialog.Builder(_context).setTitle(getString(R.string.parinaam)).setMessage("Please select Remark of " + headerTitle.getPosm()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isDialogOpen = !isDialogOpen;
                                        dialog.dismiss();
                                    }
                                });
                                alert.show();
                            }
                        }

                    } else {
                        if (isDialogOpen) {
                            isDialogOpen = !isDialogOpen;
                            AlertDialog.Builder alert = new AlertDialog.Builder(_context).setTitle(getString(R.string.parinaam)).setMessage("Please select Deployment of " + headerTitle.getPosm()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isDialogOpen = !isDialogOpen;
                                    dialog.dismiss();
                                }
                            });
                            alert.show();
                        }
                    }
                }
            });

            ///for installed posm spinner
            //setting array adaptors to spinners
            //ArrayAdapter is a BaseAdapter that is backed by an array of arbitrary objects
            ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(_context, R.layout.spinner_custom_item_forposm, posminstalledspinnvalue);
            // setting adapteers to spinners
            posminstall_spin.setAdapter(spin_adapter);
            posminstall_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        headerTitle.setPosm_deployment(String.valueOf(parent.getItemIdAtPosition(position)));
                        if (headerTitle.getPosm_deployment().equals("1")) {
                            RLposm_img.setVisibility(View.VISIBLE);
                            RLposm_nonreeason.setVisibility(View.GONE);
                            headerTitle.setCurrect_reason("");
                            headerTitle.setCurrect_reason_Id("0");
                            posmremark_spin.setSelection(0);

                            if (!lvExp_posm.isGroupExpanded(groupPosition)) {
                                lvExp_posm.expandGroup(groupPosition);
                            }

                            RLposm_remarkothers.setVisibility(View.GONE);
                            headerTitle.setEdittext_remarkfor_others("");
                            edt_remarkothers.setText("");

                        } else {
                            RLposm_img.setVisibility(View.GONE);
                            RLposm_nonreeason.setVisibility(View.VISIBLE);
                            headerTitle.setDeployment_img_one("");
                            headerTitle.setDeployment_img_two("");
                            deployment_img_one.setImageResource(R.drawable.ic_menu_camera);
                            posm_img_two.setImageResource(R.drawable.ic_menu_camera);
                            try {
                                for (int k = 0; k < _listDataChild.get(headerTitle).size(); k++) {
                                    _listDataChild.get(headerTitle).get(k).setCurrectans_Ic("0");
                                    _listDataChild.get(headerTitle).get(k).setCurrectans("");
                                }

                                if (lvExp_posm.isGroupExpanded(groupPosition)) {
                                    lvExp_posm.collapseGroup(groupPosition);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Crashlytics.logException(e);
                            }

                        }
                    } else {

                        RLposm_img.setVisibility(View.GONE);
                        RLposm_nonreeason.setVisibility(View.GONE);
                        posmremark_spin.setSelection(0);
                        headerTitle.setDeployment_img_one("");
                        headerTitle.setDeployment_img_two("");
                        headerTitle.setCurrect_reason("");
                        headerTitle.setCurrect_reason_Id("0");
                        headerTitle.setPosm_deployment("0");
                        deployment_img_one.setImageResource(R.drawable.ic_menu_camera);
                        posm_img_two.setImageResource(R.drawable.ic_menu_camera);
                        try {
                            for (int k = 0; k < _listDataChild.get(headerTitle).size(); k++) {
                                _listDataChild.get(headerTitle).get(k).setCurrectans_Ic("0");
                                _listDataChild.get(headerTitle).get(k).setCurrectans("");
                            }

                            if (lvExp_posm.isGroupExpanded(groupPosition)) {
                                lvExp_posm.collapseGroup(groupPosition);
                            }

                            headerTitle.setEdittext_remarkfor_others("");
                            edt_remarkothers.setText("");
                            RLposm_remarkothers.setVisibility(View.GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (headerTitle.getPosm_deployment().equals("1")) {
                posminstall_spin.setSelection(1);
            } else if (headerTitle.getPosm_deployment().equals("2")) {
                posminstall_spin.setSelection(2);
            } else if (headerTitle.getPosm_deployment().equals("0")) {
                RLposm_img.setVisibility(View.GONE);
                RLposm_nonreeason.setVisibility(View.GONE);
            }

            deployment_img_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lvExp_posm.clearFocus();
                    grp_position = groupPosition;
                    _pathforcheck = store_cd + "_" + headerTitle.getPosmId().toString() + "_AUDITONEIMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                    _path = CommonString.FILE_PATH + _pathforcheck;
                    CommonFunctions.startAnncaCameraActivity(_context, _path);
                }
            });


            posm_img_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lvExp_posm.clearFocus();
                    grp_position = groupPosition;
                    _pathforcheck = store_cd + "_" + headerTitle.getPosmId().toString() + "_AUDITTWOIMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                    _path = CommonString.FILE_PATH + _pathforcheck;
                    CommonFunctions.startAnncaCameraActivity(_context, _path);
                }
            });


            if (!audit_img_one.equals("")) {
                if (grp_position == groupPosition) {
                    headerTitle.setDeployment_img_one(audit_img_one);
                    audit_img_one = "";
                }
            }

            if (!audit_img_two.equals("")) {
                if (grp_position == groupPosition) {
                    headerTitle.setDeployment_img_two(audit_img_two);
                    audit_img_two = "";
                }
            }

            if (!headerTitle.getDeployment_img_one().equals("")) {
                deployment_img_one.setImageResource(R.drawable.ic_menu_camera_done);
            } else {
                deployment_img_one.setImageResource(R.drawable.ic_menu_camera);
            }

            if (!headerTitle.getDeployment_img_two().equals("")) {
                posm_img_two.setImageResource(R.drawable.ic_menu_camera_done);
            } else {
                posm_img_two.setImageResource(R.drawable.ic_menu_camera);
            }


            //for non posm reason spinner
            final ArrayList<NonPosmReason> reason_list = db.getnonposmreason();
            NonPosmReason non = new NonPosmReason();
            non.setPreason("-Select Reason-");
            non.setPreasonId(0);
            reason_list.add(0, non);
            posmremark_spin.setAdapter(new ReasonSpinnerAdapter(_context, R.layout.spinner_text_forposm, reason_list));

            for (int i = 0; i < reason_list.size(); i++) {
                if (reason_list.get(i).getPreasonId().toString().equals(headerTitle.getCurrect_reason_Id())) {
                    posmremark_spin.setSelection(i);
                    break;
                }
            }

            posmremark_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (pos != 0) {
                        NonPosmReason ans = reason_list.get(pos);
                        headerTitle.setCurrect_reason_Id(ans.getPreasonId().toString());
                        headerTitle.setCurrect_reason(ans.getPreason());
                        headerTitle.setRemarkflag(ans.getRemark());
                        if (!ans.getRemark()) {
                            RLposm_remarkothers.setVisibility(View.GONE);
                            headerTitle.setEdittext_remarkfor_others("");
                            edt_remarkothers.setText("");
                        } else {
                            RLposm_remarkothers.setVisibility(View.VISIBLE);
                        }
                    } else {
                        headerTitle.setCurrect_reason_Id("0");
                        headerTitle.setCurrect_reason("");
                        headerTitle.setEdittext_remarkfor_others("");
                        edt_remarkothers.setText("");
                        RLposm_remarkothers.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            img_posm_planogram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkNetIsAvailable()) {
                        if (!headerTitle.getRefImage().equals("NA")) {
                            showplanogramposm(_context, headerTitle.getRefImage());
                        } else {
                            if (isDialogOpen) {
                                isDialogOpen = !isDialogOpen;
                                AlertDialog.Builder alert = new AlertDialog.Builder(_context).setTitle(getString(R.string.parinaam)).setMessage("Planogram image not available this " + headerTitle.getPosm() + ".").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isDialogOpen = !isDialogOpen;
                                        dialog.dismiss();
                                    }
                                });
                                alert.show();
                            }

                        }
                    } else {
                        if (isDialogOpen) {
                            isDialogOpen = !isDialogOpen;
                            AlertDialog.Builder alert = new AlertDialog.Builder(_context).setTitle(getString(R.string.parinaam)).
                                    setMessage(R.string.nonetwork).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isDialogOpen = !isDialogOpen;
                                    dialog.dismiss();
                                }
                            });
                            alert.show();
                        }
                    }
                }
            });

            if (headerTitle.isRemarkflag()) {
                RLposm_remarkothers.setVisibility(View.VISIBLE);
            } else {
                RLposm_remarkothers.setVisibility(View.GONE);
            }

            edt_remarkothers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString().replaceAll("[(!@#$%^&*?)\"]", "");
                        if (!value1.equals("")) {
                            headerTitle.setEdittext_remarkfor_others(value1);
                        } else {
                            headerTitle.setEdittext_remarkfor_others("");
                        }
                    }
                }
            });
            edt_remarkothers.setText(headerTitle.getEdittext_remarkfor_others());

            lblListHeader.setText(headerTitle.getPosm().trim());

            if (!checkflag) {
                if (headerTitle.isRemarkflag() && headerTitle.getEdittext_remarkfor_others().equals("")) {
                    edt_remarkothers.setHintTextColor(Color.WHITE);
                    edt_remarkothers.setHint("EMPTY");
                }
                if (checkHeaderArray.contains(groupPosition)) {
                    card_view.setCardBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    card_view.setCardBackgroundColor(getResources().getColor(R.color.grey_dark_background));
                }
            } else {
                card_view.setCardBackgroundColor(getResources().getColor(R.color.grey_dark_background));
            }
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    public class ViewHolder {
        TextView checklist_name;
        Spinner checklist_spin;
        CardView cardView;
    }


    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    boolean validateData(HashMap<PosmMaster, List<PosmMaster>> listDataChild2, List<PosmMaster> listDataHeader2) {
        checkflag = true;
        checkHeaderArray.clear();
        for (int i = 0; i < listDataHeader2.size(); i++) {
            for (int j = 0; j < listDataChild2.get(listDataHeader2.get(i)).size(); j++) {
                String posm = listDataHeader2.get(i).getPosm();
                String posminstalledSpinValue = listDataHeader2.get(i).getPosm_deployment();
                String deployment_img_one = listDataHeader2.get(i).getDeployment_img_one();
                String deployment_img_two = listDataHeader2.get(i).getDeployment_img_two();
                String reason_id = listDataHeader2.get(i).getCurrect_reason_Id();
                boolean remark_flag = listDataHeader2.get(i).isRemarkflag();
                String remarks = listDataHeader2.get(i).getEdittext_remarkfor_others();


                String checklist_currectans = listDataChild2.get(listDataHeader2.get(i)).get(j).getCurrectans_Ic();
                String checklist = listDataChild2.get(listDataHeader2.get(i)).get(j).getChecklist();
                if (posminstalledSpinValue.equals("0")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage("Please select Deployment of " + posm).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                    checkflag = false;
                    break;
                } else if (posminstalledSpinValue.equals("1")) {
                    if (!deployment_img_one.equals("") && !deployment_img_two.equals("")) {
                    } else if (!deployment_img_one.equals("") && deployment_img_two.equals("")) {
                    } else if (!deployment_img_one.equals("") && deployment_img_two.equals("")) {
                    } else if (deployment_img_one.equals("") && deployment_img_two.equals("")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage("Please click atleast one image of " + posm).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                        checkflag = false;
                        break;
                    }
                    if (checklist_currectans.equals("0")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam)).setMessage("Please select answer of " + checklist).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                        checkflag = false;
                        break;
                    }
                } else if (posminstalledSpinValue.equals("2")) {
                    if (reason_id.equals("0")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(StoreAuditActivity.this).setTitle(getString(R.string.parinaam)).setMessage("Please select Reason of " + posm).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                        checkflag = false;
                        break;
                    } else if (!reason_id.equals("0") && remark_flag && remarks.equals("")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(StoreAuditActivity.this).setTitle(getString(R.string.parinaam)).setMessage("Please fill Remark of " + posm).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                        checkflag = false;
                        break;
                    }

                } else {
                    checkflag = true;
                }
            }
            if (!checkflag) {
                if (!checkHeaderArray.contains(i)) {
                    checkHeaderArray.add(i);
                }
                break;
            }
        }
        listAdapter.notifyDataSetChanged();
        return checkflag;
    }


    public class ReasonSpinnerAdapter extends ArrayAdapter<NonPosmReason> {
        List<NonPosmReason> list;
        Context context;
        int resourceId;

        public ReasonSpinnerAdapter(Context context, int resourceId, ArrayList<NonPosmReason> list) {
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
            NonPosmReason cm = list.get(position);
            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text_posm);
            txt_spinner.setText(list.get(position).getPreason());

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(resourceId, parent, false);
            NonPosmReason cm = list.get(position);
            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text_posm);
            txt_spinner.setText(cm.getPreason());

            return view;
        }

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
                            if (_pathforcheck.contains("_AUDITTWOIMG_")) {
                                String metadata = CommonFunctions.setMetadataAtImages(store_name, store_cd, "Audit Image Two", username);
                                Bitmap bmp = CommonFunctions.addMetadataAndTimeStampToImage(StoreAuditActivity.this, _path, metadata, visit_date);
                                audit_img_two = _pathforcheck;
                            } else {
                                String metadata = CommonFunctions.setMetadataAtImages(store_name, store_cd, "Audit Image One", username);
                                Bitmap bmp = CommonFunctions.addMetadataAndTimeStampToImage(StoreAuditActivity.this, _path, metadata, visit_date);
                                audit_img_one = _pathforcheck;
                            }
                            lvExp_posm.invalidateViews();
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


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StoreAuditActivity.this);
        builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                        StoreAuditActivity.this.finish();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(StoreAuditActivity.this);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            StoreAuditActivity.this.finish();
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


    private void showplanogramposm(Context context, String planogram_url) {
        multiPurposeDialog = new MultiPurposeDialog(context);
        multiPurposeDialog.setContentView(R.layout.custom_dialog_planogram);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(multiPurposeDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        multiPurposeDialog.getWindow().setAttributes(lp);
        multiPurposeDialog.setCancelable(true);
        WebView webview = multiPurposeDialog.findViewById(R.id.webview);
        webview.setWebViewClient(new MyWebViewClient());
//        webview.getSettings().setJavaScriptEnabled(true);
//        webview.getSettings().setBuiltInZoomControls(true);
        webview.loadUrl(planogram_url);
        ImageView dismisDi = (ImageView) multiPurposeDialog.findViewById(R.id.planogram_dcancel);
        dismisDi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiPurposeDialog.dismiss();
            }
        });
        multiPurposeDialog.show();
    }

    public class MultiPurposeDialog extends Dialog {
        public MultiPurposeDialog(Context context) {
            super(context);
            // DIALOG USER_INTERFACE TEMPLATE
            WindowManager.LayoutParams wmLayoutParams = getWindow().getAttributes();
            wmLayoutParams.gravity = Gravity.CENTER;
            getWindow().setAttributes(wmLayoutParams);
            setTitle(null);
            setCancelable(false);
            setOnCancelListener(null);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
        }

        @Override
        public void show() {
            super.show();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.clearCache(true);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }

}
