package jp.cpm.com.xxonmobil.dailyentry;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.gsonGetterSetter.BeatWiseReport;

public class ReportActivity extends AppCompatActivity {
    TextView beator_dsr_nametext, outlat_maerchandising_txt;
    ArrayList<BeatWiseReport> beatswiseORDsrWiseList = new ArrayList<>();
    private SharedPreferences preferences;
    BeatswiseOrDsrWiseAdapter adapter;
    RecyclerView recycl_report;
    String beatsORDsrFlag;
    Context context;
    Xxon_Database db;
    String visit_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        recycl_report = (RecyclerView) findViewById(R.id.recycl_report);
        beator_dsr_nametext = (TextView) findViewById(R.id.beator_dsr_nametext);
        outlat_maerchandising_txt = (TextView) findViewById(R.id.outlat_maerchandising_txt);
        beatsORDsrFlag = getIntent().getStringExtra(CommonString.KEY_REPORT_FLAG);
        visit_date = preferences.getString(CommonString.KEY_DATE, "");
        db = new Xxon_Database(context);
        db.open();
        if (beatsORDsrFlag.equals("0")) {
            setTitle("BeatsWise Report - " + visit_date);
            beator_dsr_nametext.setText(getString(R.string.beat_name));
            outlat_maerchandising_txt.setText(getString(R.string.beat_outlet_ach));
            beatswiseORDsrWiseList = db.getbeatwiseORDSRWiseReport(true);
        } else {
            setTitle("DSRWise Report - " + visit_date);
            beator_dsr_nametext.setText(getString(R.string.dsr_name));
            outlat_maerchandising_txt.setText(getString(R.string.dsr_outlet_ach));
            beatswiseORDsrWiseList = db.getbeatwiseORDSRWiseReport(false);
        }

        if (beatswiseORDsrWiseList.size() > 0) {
            adapter = new BeatswiseOrDsrWiseAdapter(context, beatswiseORDsrWiseList);
            recycl_report.setAdapter(adapter);
            recycl_report.setLayoutManager(new LinearLayoutManager(context));
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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


    private class BeatswiseOrDsrWiseAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private LayoutInflater inflator;
        Context context;
        ArrayList<BeatWiseReport> insertedlist_Data;

        BeatswiseOrDsrWiseAdapter(Context context, ArrayList<BeatWiseReport> insertedlist_Data) {
            inflator = LayoutInflater.from(context);
            this.context = context;
            this.insertedlist_Data = insertedlist_Data;

        }


        @Override
        public int getItemCount() {
            return insertedlist_Data.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.secondary_beatswise_dsrwise_child, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.beatordsr_name.setText(insertedlist_Data.get(position).getBeatName());
            holder.target_noofoutlet.setText(insertedlist_Data.get(position).getTargetOutlet().toString());
            holder.outlet_ach_merch.setText(insertedlist_Data.get(position).getMerchandised().toString());
            holder.outlets_ach.setText(insertedlist_Data.get(position).getOutletAch().toString());
            holder.outlets_ach_perfectly.setText(insertedlist_Data.get(position).getOutletPerfect().toString());
            holder.perf_achievment.setText(insertedlist_Data.get(position).getPefectAch().toString());


            holder.beatordsr_name.setId(position);
            holder.target_noofoutlet.setId(position);
            holder.outlet_ach_merch.setId(position);
            holder.outlets_ach.setId(position);
            holder.outlets_ach_perfectly.setId(position);
            holder.perf_achievment.setId(position);

        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView beatordsr_name, target_noofoutlet, outlet_ach_merch, outlets_ach, outlets_ach_perfectly, perf_achievment;

        public MyViewHolder(View convertView) {
            super(convertView);
            beatordsr_name = (TextView) convertView.findViewById(R.id.beatordsr_name);
            target_noofoutlet = (TextView) convertView.findViewById(R.id.target_noofoutlet);
            outlet_ach_merch = (TextView) convertView.findViewById(R.id.outlet_ach_merch);
            outlets_ach = (TextView) convertView.findViewById(R.id.outlets_ach);
            outlets_ach_perfectly = (TextView) convertView.findViewById(R.id.outlets_ach_perfectly);
            perf_achievment = (TextView) convertView.findViewById(R.id.perf_achievment);
        }
    }

}
