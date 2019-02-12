package jp.cpm.com.xxonmobil.GeoTag;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.dailyentry.StoreListActivity;
import jp.cpm.com.xxonmobil.gsonGetterSetter.JourneyPlan;

import static jp.cpm.com.xxonmobil.dailyentry.StoreListActivity.hideKeyboardFrom;

public class GeoTagStoreList extends AppCompatActivity {

    private SharedPreferences preferences;
    ArrayList<JourneyPlan> storelist = new ArrayList<JourneyPlan>();
    String date, visit_status;
    Xxon_Database db;
    ValueAdapter adapter;
    RecyclerView recyclerView;
    private SharedPreferences.Editor editor = null;
    LinearLayout linearlay;
    FloatingActionButton fab;
    Context context;
    Toolbar toolbar;
    CardView search_store_cardv;
    EditText edt_searchstore;
    Button btn_searchstore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_tag_store_list);
        declaration();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        GeoTagStoreList.this.finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

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
            View view = inflator.inflate(R.layout.geotagstorelist, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

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
                    "Store Category - " + current.getStoreCategory().trim() + "(Id - " + current.getStoreId().toString() + ")" + " \n" + current.getStoreName().trim());
            viewHolder.txt_storeAddress.setText(current.getAddress().trim() + " - " + current.getCity().trim() + "\n" + lastVisited.trim());

            if (current.getGeoTag().equalsIgnoreCase(CommonString.KEY_Y)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.mipmap.geotag_grey);
            } else if (current.getGeoTag().equalsIgnoreCase(CommonString.KEY_STATUS_N)) {
                viewHolder.imageview.setVisibility(View.INVISIBLE);
                viewHolder.imageview.setBackgroundResource(R.mipmap.entry_grey);
            } else {
                viewHolder.imageview.setVisibility(View.INVISIBLE);
            }

            viewHolder.relativelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current.getGeoTag().equalsIgnoreCase(CommonString.KEY_Y)) {
                        Snackbar.make(v, R.string.title_geo_tag_activity_geo_already_done, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getGeoTag().equalsIgnoreCase(CommonString.KEY_STATUS_N)) {
                        Intent in = new Intent(GeoTagStoreList.this, GeoTaggingActivity.class);
                        in.putExtra(CommonString.KEY_STORE_CD, current.getStateId().toString());
                        startActivity(in);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt, txt_storeAddress;
            ImageView icon;
            RelativeLayout relativelayout;
            ImageView imageview;

            public MyViewHolder(View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.geolistviewxml_storename);
                relativelayout = (RelativeLayout) itemView.findViewById(R.id.relativelayout);
                imageview = (ImageView) itemView.findViewById(R.id.imageView1);
                txt_storeAddress = (TextView) itemView.findViewById(R.id.txt_storeAddress);
            }
        }
    }

    void declaration() {
        context = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.drawer_layout_recycle);
        linearlay = (LinearLayout) findViewById(R.id.no_data_lay);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, "");
        visit_status = preferences.getString(CommonString.KEY_STOREVISITED_STATUS, "");
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_geo_tag_store_list) + " - " + date);
        db = new Xxon_Database(GeoTagStoreList.this);
        db.open();

        //for filter store
        search_store_cardv = (CardView) findViewById(R.id.search_store_cardv);
        edt_searchstore = (EditText) findViewById(R.id.edt_searchstore);
        btn_searchstore = (Button) findViewById(R.id.btn_searchstore);

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


    private void filterstorelist() {
        if (!edt_searchstore.getText().toString().isEmpty()) {
            storelist = db.getfilteredJCPstore(edt_searchstore.getText().toString());
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
        db.open();
        storelist = db.getStoreData(date);
        if (storelist.size() > 0 && preferences.getInt(CommonString.KEY_DOWNLOAD_INDEX, 0) == 0) {
            adapter = new ValueAdapter(context, storelist);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        storelist = db.getStoreData(date);
        if (storelist.size() > 0) {
            adapter = new ValueAdapter(getApplicationContext(), storelist);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            linearlay.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }

    }

}
