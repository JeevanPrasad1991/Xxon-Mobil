package jp.cpm.com.xxonmobil.dailyentry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.delegates.NavMenuItemGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.JourneyPlan;

public class StoreEntryActivity extends AppCompatActivity {
    Xxon_Database db;
    ValueAdapter adapter;
    RecyclerView recyclerView;
    TextView storenamewithbeatname_txt;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    String store_Id, visit_date, user_type, username, storeCategory_Id, state_Id, region_Id, city_name, store_name;
    ArrayList<JourneyPlan> specificStoreDATA = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_entry);
        uivalidate();
        db = new Xxon_Database(this);
    }


    private void uivalidate() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.drawer_layout_recycle_store);
        storenamewithbeatname_txt = (TextView) findViewById(R.id.storenamewithbeatname_txt);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        store_Id = preferences.getString(CommonString.KEY_STORE_CD, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        storeCategory_Id = preferences.getString(CommonString.KEY_STORE_CATEGORY_Id, "");
        state_Id = preferences.getString(CommonString.KEY_STATE_ID, "");
        region_Id = preferences.getString(CommonString.KEY_REGION_ID, "");
        city_name = preferences.getString(CommonString.KEY_CITY_NAME, "");
        store_name = preferences.getString(CommonString.KEY_STORE_NAME, "");

        getSupportActionBar().setTitle(getString(R.string.title_storeEntry));
        storenamewithbeatname_txt.setText("Store Name - " + store_name + " ( Id - " + store_Id + " ) " +"\n" + "City Name - " + city_name + " - Date - " + visit_date);

    }

    @Override
    protected void onResume() {
        super.onResume();
        db.open();
        specificStoreDATA = db.getSpecificStoreData(store_Id);
        adapter = new ValueAdapter(getApplicationContext(), getdata());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        this.finish();
    }

    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<NavMenuItemGetterSetter> data = Collections.emptyList();

        public ValueAdapter(Context context, List<NavMenuItemGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public ValueAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.custom_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ValueAdapter.MyViewHolder viewHolder, final int position) {
            final NavMenuItemGetterSetter current = data.get(position);
            viewHolder.icon.setImageResource(current.getIconImg());
            viewHolder.icon_txtname.setText(current.getIconName());
            viewHolder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (current.getIconImg() == R.drawable.audit_posm || current.getIconImg() == R.drawable.audit_posm_done) {
                        Intent in7 = new Intent(StoreEntryActivity.this, StoreAuditActivity.class);
                        startActivity(in7);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    } else if (current.getIconImg() == R.drawable.posm_deployment_done || current.getIconImg() == R.drawable.posm_deployment) {
                        Intent in7 = new Intent(StoreEntryActivity.this, DeploymentActivity.class);
                        startActivity(in7);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            TextView icon_txtname;

            public MyViewHolder(View itemView) {
                super(itemView);
                icon = (ImageView) itemView.findViewById(R.id.list_icon);
                icon_txtname = (TextView) itemView.findViewById(R.id.icon_txtname);
            }
        }

    }

    public List<NavMenuItemGetterSetter> getdata() {
        List<NavMenuItemGetterSetter> data = new ArrayList<>();
        int store_auditimg = 0, store_deployment = 0;

        if (db.getStoreAuditHeaderData(region_Id, storeCategory_Id).size() > 0) {
            if (db.isstore_auditexist(store_Id)) {
                store_auditimg = R.drawable.audit_posm_done;
            } else {
                store_auditimg = R.drawable.audit_posm;
            }
        } else {
            store_auditimg = R.drawable.audit_posm_grey;
        }

        if (db.getstoredeploymentList(region_Id, storeCategory_Id).size() > 0) {
            if (db.isposm_deployment(store_Id, visit_date)) {
                store_deployment = R.drawable.posm_deployment_done;
            } else {
                store_deployment = R.drawable.posm_deployment;
            }
        } else {
            store_deployment = R.drawable.posm_deployment_grey;
        }

        if (user_type.equalsIgnoreCase("Merchandiser")) {
            int img[] = {store_deployment};
            String name[] = {"POSM Deployment"};
            for (int i = 0; i < img.length; i++) {
                NavMenuItemGetterSetter recData = new NavMenuItemGetterSetter();
                recData.setIconImg(img[i]);
                recData.setIconName(name[i]);
                data.add(recData);
            }
        } else {
            int img[] = {store_auditimg};
            String name[] = {"POSM Audit"};
            for (int i = 0; i < img.length; i++) {
                NavMenuItemGetterSetter recData = new NavMenuItemGetterSetter();
                recData.setIconImg(img[i]);
                recData.setIconName(name[i]);
                data.add(recData);
            }
        }

        return data;
    }

}
