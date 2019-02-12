package jp.cpm.com.xxonmobil.upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.MultipartBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.delegates.CoverageBean;
import jp.cpm.com.xxonmobil.gettersetter.GeotaggingBeans;
import jp.cpm.com.xxonmobil.gettersetter.StoreProfileGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.JourneyPlan;
import jp.cpm.com.xxonmobil.gsonGetterSetter.PosmMaster;
import jp.cpm.com.xxonmobil.retrofit.PostApi;
import jp.cpm.com.xxonmobil.retrofit.PostApiForUpload;
import jp.cpm.com.xxonmobil.retrofit.StringConverterFactory;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PreviousDataUploadActivity extends AppCompatActivity {
    Xxon_Database db;
    Toolbar toolbar;
    String mid = "0";
    com.squareup.okhttp.RequestBody body1;
    private Retrofit adapter;
    int status = 0, statusforimage = 0;
    Context context;
    private SharedPreferences preferences;
    String userId, visit_date, app_version, user_type;
    private ProgressDialog pb;
    ArrayList<CoverageBean> coverageList = new ArrayList<>();
    ArrayList<JourneyPlan> specific_uploadStatus;
    ProgressDialog loading;
    public static int uploadedFiles = 0;
    public static int totalFiles = 0;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        userId = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, "");
        context = this;
        db = new Xxon_Database(this);
        db.open();
        isDataValid();
        uploadedFiles = 0;
    }

    private boolean chekDataforCheckout(JourneyPlan current) {
        boolean status = false;
        db.open();
        if (user_type.equalsIgnoreCase("Merchandiser")) {
            if (db.getstoredeploymentList(current.getRegionId().toString(), current.getStoreCategoryId().toString()).size() > 0) {
                if (db.isposm_deployment(current.getStoreId().toString(), current.getVisitDate())) {
                    status = true;
                } else {
                    status = false;
                }
            } else {
                status = true;
            }
        } else {
            if (db.getStoreAuditHeaderData(current.getRegionId().toString(), current.getStoreCategoryId().toString()).size() > 0) {
                if (db.isstore_auditexist(current.getStoreId().toString())) {
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

    void isDataValid() {
        boolean flag_invalid = false;
        String Store_cd = "", previous_date = "";
        JourneyPlan jcp = null;
        ArrayList<CoverageBean> coverage_list = db.getcoverageDataPrevious(visit_date);
        if (coverage_list.size() > 0)
            for (int i = 0; i < coverage_list.size(); i++) {
                jcp = db.getSpecificStoreDataPrevious(visit_date, coverage_list.get(i).getStore_Id());
                if (jcp != null && jcp.getUploadStatus() != null && jcp.getUploadStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                    Store_cd = jcp.getStoreId().toString();
                    previous_date = jcp.getVisitDate();
                    if (chekDataforCheckout(jcp)) {
                        flag_invalid = true;
                        break;
                    } else {
                        db.open();
                        db.updateJaurneyPlanSpecificStoreStatus(Store_cd, jcp.getVisitDate(), CommonString.KEY_STATUS_N);
                        db.updateJaurneyPlanSpecificStoreofLastVisit_date(Store_cd, jcp.getVisitDate());
                        db.deleteSpecificStoreData(Store_cd);
                        flag_invalid = false;
                        break;
                    }
                }
            }

        if (flag_invalid) {
            db.open();
            ArrayList<CoverageBean> specificDATa = db.getSpecificCoverageData(previous_date, Store_cd);
            if (specificDATa.size() > 0) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("UserId", userId);
                    jsonObject.put("StoreId", specificDATa.get(0).getStore_Id());
                    jsonObject.put("Latitude", specificDATa.get(0).getLatitude());
                    jsonObject.put("Longitude", specificDATa.get(0).getLongitude());
                    jsonObject.put("Checkout_Date", specificDATa.get(0).getVisitDate());
                    uploadCoverageIntimeDATA(jsonObject.toString(), specificDATa);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //start upload
                coverageList = db.getcoverageDataPrevious(visit_date);
                if (coverageList.size() > 0) {
                    pb = new ProgressDialog(context);
                    pb.setCancelable(false);
                    pb.setMessage("Uploading Data");
                    pb.show();
                    uploadDataUsingCoverageRecursive(coverageList, 0);
                }
            }

        } else {
            //start upload
            coverageList = db.getcoverageDataPrevious(visit_date);
            if (coverageList.size() > 0) {
                pb = new ProgressDialog(context);
                pb.setCancelable(false);
                pb.setMessage("Uploading Data");
                pb.show();
                uploadDataUsingCoverageRecursive(coverageList, 0);
            } else {
                AlertandMessages.showAlert((Activity) context, "All data and images upload Successfully.", true);
            }
        }
    }

    public void uploadCoverageIntimeDATA(String jsondata, final ArrayList<CoverageBean> specific_CData) {
        try {
            loading = ProgressDialog.show(PreviousDataUploadActivity.this, "Processing", "Please wait...",
                    false, false);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsondata.toString());
            Retrofit adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostApi api = adapter.create(PostApi.class);
            retrofit2.Call<ResponseBody> call;
            if (user_type.equalsIgnoreCase("Merchandiser")) {
                call = api.getCheckout(jsonData);
            } else {
                call = api.getCheckoutAudit(jsonData);
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
                                db.open();
                                db.updateJaurneyPlanSpecificStoreStatus(specific_CData.get(0).getStore_Id(), specific_CData.get(0).getVisitDate(), CommonString.KEY_C);
                                db.updateJaurneyPlanSpecificStoreofLastVisit_date(specific_CData.get(0).getStore_Id(), specific_CData.get(0).getVisitDate());
                                db.updateJaurneyPlanPosmEditableStatus(specific_CData.get(0).getStore_Id(), specific_CData.get(0).getVisitDate(), CommonString.KEY_STATUS_N);
                                loading.dismiss();
                                coverageList = db.getcoverageDataPrevious(visit_date);
                                if (coverageList.size() > 0) {
                                    pb = new ProgressDialog(context);
                                    pb.setCancelable(false);
                                    pb.setMessage("Uploading Data");
                                    pb.show();
                                    uploadDataUsingCoverageRecursive(coverageList, 0);
                                }
                            }
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                            loading.dismiss();
                            AlertandMessages.showAlert(PreviousDataUploadActivity.this, "Check internet conection", true);
                        }
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                    loading.dismiss();
                    if (t.toString() != null) {
                        if (t instanceof SocketTimeoutException || t instanceof IOException || t instanceof Exception)
                            AlertandMessages.showAlert(PreviousDataUploadActivity.this, "Check internet conection", true);
                    } else {
                        AlertandMessages.showAlert(PreviousDataUploadActivity.this, "Check internet conection", true);

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loading.dismiss();
            Crashlytics.logException(e);
            AlertandMessages.showAlert(PreviousDataUploadActivity.this, "Check internet conection", true);
        }
    }


    //upload previous data
    public void uploadDataUsingCoverageRecursive(ArrayList<CoverageBean> coverageList, int coverageIndex) {
        try {
            ArrayList<String> keyList = new ArrayList<>();
            keyList.clear();
            String store_id = coverageList.get(coverageIndex).getStore_Id();
            String status = null;
            db.open();

            specific_uploadStatus = db.getSpecificStoreData(store_id);
            status = specific_uploadStatus.get(0).getUploadStatus();

            pb.setMessage("Uploading store " + (coverageIndex + 1) + "/" + coverageList.size());
            if (!status.equalsIgnoreCase(CommonString.KEY_D)) {
                if (user_type.equalsIgnoreCase("Merchandiser")) {
                    keyList.add("CoverageDetail_latest");
                    keyList.add("Store_Profile_Data");
                    keyList.add("Store_Audit_data");
                    keyList.add("Store_Deployment_Data");
                    keyList.add("Geo_Tag");
                } else {
                    keyList.add("CoverageDetail_latestAudit");
                    keyList.add("Store_Profile_Data");
                    keyList.add("Store_Audit_data");
                    keyList.add("Store_Deployment_Data");
                    keyList.add("Geo_Tag_Audit");
                }
            }

            if (keyList.size() > 0) {
                uploadDataWithoutWait(keyList, 0, coverageList, coverageIndex);
            } else {
                if (++coverageIndex != coverageList.size()) {
                    uploadDataUsingCoverageRecursive(coverageList, coverageIndex);
                } else {
                    ////CHANGESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
                    //  uploadimages();
                    pb.setMessage("uploading images");
                    File dir = new File(CommonString.FILE_PATH);
                    if (getFileNames(dir.listFiles()).size() > 0) {
                        totalFiles = getFileNames(dir.listFiles()).size();
                        uploadImage(visit_date);
                    } else {
                        pb.setMessage("Updating status");
                        updatestatusforu(coverageList, 0, visit_date, CommonString.KEY_U);
                    }
                }

            }
            //endregion

        } catch (Exception e) {
            e.printStackTrace();
            pb.dismiss();
            Crashlytics.logException(e);
            if (e != null) {
                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION + " (" + e.toString() + ")", true);
            } else {
                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION, true);

            }
        }
    }

    public void uploadDataWithoutWait(final ArrayList<String> keyList,
                                      final int keyIndex, final ArrayList<CoverageBean> coverageList,
                                      final int coverageIndex) {
        try {
            status = 0;
            final String[] data_global = {""};
            String jsonString = "";
            int type = 0;
            JSONObject jsonObject;
            //region Creating json data
            switch (keyList.get(keyIndex)) {
                case "CoverageDetail_latest":
                    //region Coverage Data
                    jsonObject = new JSONObject();
                    jsonObject.put("StoreId", coverageList.get(coverageIndex).getStore_Id());
                    jsonObject.put("VisitDate", coverageList.get(coverageIndex).getVisitDate());
                    jsonObject.put("Latitude", coverageList.get(coverageIndex).getLatitude());
                    jsonObject.put("Longitude", coverageList.get(coverageIndex).getLongitude());
                    jsonObject.put("ReasonId", coverageList.get(coverageIndex).getReasonid());
                    jsonObject.put("SubReasonId", "0");
                    jsonObject.put("Remark", coverageList.get(coverageIndex).getRemark());
                    jsonObject.put("ImageName", coverageList.get(coverageIndex).getImage());
                    jsonObject.put("AppVersion", app_version);
                    jsonObject.put("UploadStatus", CommonString.KEY_P);
                    jsonObject.put("Checkout_Image", "");
                    jsonObject.put("UserId", userId);
                    jsonString = jsonObject.toString();
                    type = CommonString.COVERAGE_DETAIL;
                    //endregion
                    break;

                case "CoverageDetail_latestAudit":
                    //region Coverage Data
                    jsonObject = new JSONObject();
                    jsonObject.put("StoreId", coverageList.get(coverageIndex).getStore_Id());
                    jsonObject.put("VisitDate", coverageList.get(coverageIndex).getVisitDate());
                    jsonObject.put("Latitude", coverageList.get(coverageIndex).getLatitude());
                    jsonObject.put("Longitude", coverageList.get(coverageIndex).getLongitude());
                    jsonObject.put("ReasonId", coverageList.get(coverageIndex).getReasonid());
                    jsonObject.put("SubReasonId", "0");
                    jsonObject.put("Remark", coverageList.get(coverageIndex).getRemark());
                    jsonObject.put("ImageName", coverageList.get(coverageIndex).getImage());
                    jsonObject.put("AppVersion", app_version);
                    jsonObject.put("UploadStatus", CommonString.KEY_P);
                    jsonObject.put("Checkout_Image", "");
                    jsonObject.put("UserId", userId);
                    jsonObject.put("Dep_MID", "0");
                    jsonString = jsonObject.toString();
                    type = CommonString.COVERAGE_DETAIL;
                    //endregion
                    break;
                case "Store_Profile_Data":
                    db.open();
                    StoreProfileGetterSetter storePGT = db.getStoreProfileData(coverageList.get(coverageIndex).getStore_Id(), coverageList.get(coverageIndex).getVisitDate());
                    if (storePGT.getProfileCity() != null && !storePGT.getProfileCity().equals("")) {
                        JSONArray storeDetail = new JSONArray();
                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("UserId", userId);
                        jsonObject.put("Store_Id", coverageList.get(coverageIndex).getStore_Id());
                        jsonObject.put("Store_Name", storePGT.getStore_name());
                        jsonObject.put("Address", storePGT.getProfileAddress());
                        jsonObject.put("City", storePGT.getProfileCity());
                        jsonObject.put("Pin_Code", storePGT.getPin_Code());
                        jsonObject.put("Email_Id", storePGT.getEmail_Id());
                        jsonObject.put("Contact_No", storePGT.getProfileContact());
                        jsonObject.put("Retailer_Name", storePGT.getRetailer_name());
                        jsonObject.put("Ageingof_Branding", storePGT.getAgeingof_branding());
                        jsonObject.put("Category", storePGT.getCategory_name());
                        jsonObject.put("Segment", storePGT.getSegment());
                        jsonObject.put("Distributor", storePGT.getDistributor());
                        jsonObject.put("Visiting_card_Image", storePGT.getVisiting_card());
                        jsonObject.put("Remark", storePGT.getProfile_remark());
                        storeDetail.put(jsonObject);
                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Store_Profile_Data");
                        jsonObject.put("JsonData", storeDetail.toString());
                        jsonObject.put("UserId", userId);
                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;

                    }
                    //endregion
                    break;
                //endregion
                case "Store_Audit_data":
                    //region Promotion data
                    db.open();
                    ArrayList<PosmMaster> audit_headerList = db.getinsertedauditHeaderData(coverageList.get(coverageIndex).getStore_Id(), coverageList.get(coverageIndex).getVisitDate());
                    if (audit_headerList.size() > 0) {
                        JSONArray audit_chArray = new JSONArray();
                        JSONArray audut_h_array = new JSONArray();
                        for (int k = 0; k < audit_headerList.size(); k++) {
                            db.open();
                            ArrayList<PosmMaster> audut_child = db.getauditchecklist(coverageList.get(coverageIndex).getStore_Id(), coverageList.get(coverageIndex).getVisitDate(), audit_headerList.get(k).getKey_Id());
                            if (audut_child.size() > 0) {
                                if (audit_headerList.get(k).getPosm_deployment().equals("1")) {
                                    for (int j = 0; j < audut_child.size(); j++) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("MID", coverageList.get(coverageIndex).getMID());
                                        obj.put("UserId", userId);
                                        obj.put("Posm_Id", audit_headerList.get(k).getPosmId().toString());
                                        obj.put("Checklist_Id", audut_child.get(j).getChecklist_Id());
                                        obj.put("Answer_Id", audut_child.get(j).getCurrectans_Ic());
                                        obj.put("KEY_Id", audit_headerList.get(k).getKey_Id());
                                        audit_chArray.put(obj);
                                    }
                                }
                            }
                            String deployment = "0";
                            if (audit_headerList.get(k).getPosm_deployment().equals("1")) {
                                deployment = "1";
                            } else {
                                deployment = "0";
                            }

                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("UserId", userId);
                            obj.put("Deployment", deployment);
                            obj.put("Posm_Id", audit_headerList.get(k).getPosmId().toString());
                            obj.put("Audit_Img_One", audit_headerList.get(k).getDeployment_img_one());
                            obj.put("Audit_Img_Two", audit_headerList.get(k).getDeployment_img_two());
                            obj.put("Reason_Id", audit_headerList.get(k).getCurrect_reason_Id());
                            obj.put("Remark", audit_headerList.get(k).getEdittext_remarkfor_others());
                            obj.put("KEY_Id", audit_headerList.get(k).getKey_Id());
                            if (audit_headerList.get(k).getPosm_deployment().equals("1")) {
                                obj.put("Audit_Checklist", audit_chArray);
                            } else {
                                obj.put("Audit_Checklist", "");
                            }
                            audut_h_array.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Store_Audit_data");
                        jsonObject.put("JsonData", audut_h_array.toString());
                        jsonObject.put("UserId", userId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    //endregion
                    break;

                case "Store_Deployment_Data":
                    ArrayList<PosmMaster> deploymentList = db.getinserteddeployment(coverageList.get(coverageIndex).getStore_Id(), coverageList.get(coverageIndex).getVisitDate());
                    if (deploymentList.size() > 0) {
                        JSONArray topUpArray = new JSONArray();
                        for (int j = 0; j < deploymentList.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("UserId", userId);
                            obj.put("Posm_Id", deploymentList.get(j).getPosmId().toString());
                            obj.put("Deployment", deploymentList.get(j).getPosm_deployment());
                            obj.put("Deployment_Image_One", deploymentList.get(j).getDeployment_img_one());
                            obj.put("Deployment_Image_Two", deploymentList.get(j).getDeployment_img_two());
                            topUpArray.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Store_Deployment_Data");
                        jsonObject.put("JsonData", topUpArray.toString());
                        jsonObject.put("UserId", userId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    break;
                case "Geo_Tag":
                    ArrayList<GeotaggingBeans> geotaglist = db.getinsertGeotaggingData(coverageList.get(coverageIndex).getStore_Id(), CommonString.KEY_STATUS_N);
                    if (geotaglist.size() > 0) {
                        JSONArray topUpArray = new JSONArray();
                        for (int j = 0; j < geotaglist.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put("Store_Cd", geotaglist.get(j).getStore_Id());
                            obj.put("Visit_Date", coverageList.get(coverageIndex).getVisitDate());
                            obj.put("Latitude", geotaglist.get(j).getLatitude());
                            obj.put("Longitude", geotaglist.get(j).getLongitude());
                            obj.put("Geo_Image", geotaglist.get(j).getImage());
                            topUpArray.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", "0");
                        jsonObject.put("Keys", "Geo_Tag");
                        jsonObject.put("JsonData", topUpArray.toString());
                        jsonObject.put("UserId", userId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    break;

                case "Geo_Tag_Audit":
                    ArrayList<GeotaggingBeans> geotaglistaudit = db.getinsertGeotaggingData(coverageList.get(coverageIndex).getStore_Id(), CommonString.KEY_STATUS_N);
                    if (geotaglistaudit.size() > 0) {
                        JSONArray topUpArray = new JSONArray();
                        for (int j = 0; j < geotaglistaudit.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put("Store_Cd", coverageList.get(coverageIndex).getStore_Id());
                            obj.put("Visit_Date", coverageList.get(coverageIndex).getVisitDate());
                            obj.put("Latitude", geotaglistaudit.get(j).getLatitude());
                            obj.put("Longitude", geotaglistaudit.get(j).getLongitude());
                            obj.put("Geo_Image", geotaglistaudit.get(j).getImage());
                            topUpArray.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", "0");
                        jsonObject.put("Keys", "Geo_Tag_Audit");
                        jsonObject.put("JsonData", topUpArray.toString());
                        jsonObject.put("UserId", userId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    break;

            }
            //endregion

            final OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).connectTimeout(20, TimeUnit.SECONDS).build();
            final int[] finalJsonIndex = {keyIndex};
            final String finalKeyName = keyList.get(keyIndex);
            if (jsonString != null && !jsonString.equalsIgnoreCase("")) {
                pb.setMessage("Uploading (" + keyIndex + "/" + keyList.size() + ") \n" + keyList.get(keyIndex) + "\n Store uploading " + (coverageIndex + 1) + "/" + coverageList.size());
                RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
                adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
                PostApi api = adapter.create(PostApi.class);
                Call<ResponseBody> call = null;
                if (type == CommonString.COVERAGE_DETAIL) {
                    if (user_type.equalsIgnoreCase("Merchandiser")) {
                        call = api.getCoverageDetail(jsonData);
                    } else {
                        call = api.getCoverageDetailAudit(jsonData);
                    }
                } else if (type == CommonString.UPLOADJsonDetail) {
                    call = api.getUploadJsonDetail(jsonData);
                }
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody = response.body();
                        String data = null;
                        if (responseBody != null && response.isSuccessful()) {
                            try {
                                data = response.body().string();
                                if (data.equalsIgnoreCase("")) {
                                    pb.dismiss();
                                    data_global[0] = "";
                                    AlertandMessages.showAlert((Activity) context, "Invalid Data :" + " problem occured at " + keyList.get(keyIndex), true);
                                } else {
                                    data = data.substring(1, data.length() - 1).replace("\\", "");
                                    data_global[0] = data;
                                    if (finalKeyName.equalsIgnoreCase("CoverageDetail_latest")) {
                                        try {
                                            coverageList.get(coverageIndex).setMID(Integer.parseInt(data_global[0]));
                                            specific_uploadStatus.get(0).setUploadStatus(CommonString.KEY_P);
                                            db.updateJaurneyPlanSpecificStoreStatus(coverageList.get(coverageIndex).getStore_Id(), coverageList.get(coverageIndex).getVisitDate(), CommonString.KEY_P);
                                            db.updateJaurneyPlanSpecificStoreofLastVisit_date(coverageList.get(coverageIndex).getStore_Id(), coverageList.get(coverageIndex).getVisitDate());
                                            db.updateJaurneyPlanPosmEditableStatus(coverageList.get(coverageIndex).getStore_Id(), coverageList.get(coverageIndex).getVisitDate(), CommonString.KEY_STATUS_N);
                                        } catch (NumberFormatException ex) {
                                            pb.dismiss();
                                            Crashlytics.logException(ex);
                                            AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName, true);
                                        }
                                    } else if (finalKeyName.equalsIgnoreCase("CoverageDetail_latestAudit")) {
                                        try {
                                            coverageList.get(coverageIndex).setMID(Integer.parseInt(data_global[0]));
                                            specific_uploadStatus.get(0).setUploadStatus(CommonString.KEY_P);
                                            db.updateJaurneyPlanSpecificStoreStatus(coverageList.get(coverageIndex).getStore_Id(), coverageList.get(coverageIndex).getVisitDate(), CommonString.KEY_P);
                                            db.updateJaurneyPlanSpecificStoreofLastVisit_date(coverageList.get(coverageIndex).getStore_Id(), coverageList.get(coverageIndex).getVisitDate());
                                            db.updateJaurneyPlanPosmEditableStatus(coverageList.get(coverageIndex).getStore_Id(), coverageList.get(coverageIndex).getVisitDate(), CommonString.KEY_STATUS_N);
                                        } catch (NumberFormatException ex) {
                                            pb.dismiss();
                                            AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName, true);
                                        }
                                    } else if (data_global[0].contains(CommonString.KEY_SUCCESS)) {
                                        if (finalKeyName.equalsIgnoreCase("GEO_TAG")) {
                                        }
                                    } else {
                                        pb.dismiss();
                                        AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName + " : " + data_global[0], true);
                                    }
                                    finalJsonIndex[0]++;
                                    if (finalJsonIndex[0] != keyList.size()) {
                                        uploadDataWithoutWait(keyList, finalJsonIndex[0], coverageList, coverageIndex);
                                    } else {
                                        pb.setMessage("updating status :" + coverageIndex);
                                        //uploading status D for current store from coverageList

                                        specific_uploadStatus.get(0).setUploadStatus(CommonString.KEY_D);
                                        updateStatus(coverageList, coverageIndex, CommonString.KEY_D);

                                    }
                                }

                            } catch (Exception e) {
                                pb.dismiss();
                                Crashlytics.logException(e);
                                AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName, true);
                            }
                        } else {
                            pb.dismiss();
                            AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName, true);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        pb.dismiss();
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    }
                });

            } else {
                finalJsonIndex[0]++;
                if (finalJsonIndex[0] != keyList.size()) {
                    uploadDataWithoutWait(keyList, finalJsonIndex[0], coverageList, coverageIndex);
                } else {
                    pb.setMessage("updating status :" + coverageIndex);
                    //uploading status D for current store from coverageList

                    specific_uploadStatus.get(0).setUploadStatus(CommonString.KEY_D);
                    updateStatus(coverageList, coverageIndex, CommonString.KEY_D);

                }
            }
        } catch (Exception ex) {
            pb.dismiss();
            Crashlytics.logException(ex);
            AlertandMessages.showAlert((Activity) context, ex.toString(), true);
        }
    }

    void updateStatus(final ArrayList<CoverageBean> coverageList, final int coverageIndex,
                      final String status) {
        if (coverageList.get(coverageIndex) != null) {
            try {
                final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .build();

                final int[] tempcoverageIndex = {coverageIndex};
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("StoreId", coverageList.get(coverageIndex).getStore_Id());
                jsonObject.put("VisitDate", coverageList.get(coverageIndex).getVisitDate());
                jsonObject.put("UserId", userId);
                jsonObject.put("Status", status);
                RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                PostApi api = adapter.create(PostApi.class);

                Call<ResponseBody> call;
                if (user_type.equalsIgnoreCase("Merchandiser")) {
                    call = api.getCoverageStatusDetail(jsonData);
                } else {
                    call = api.getCoverageStatusDetailAudit(jsonData);
                }
                pb.setMessage("Uploading store status " + (coverageIndex + 1) + "/" + coverageList.size());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody = response.body();
                        String data = null;
                        if (responseBody != null && response.isSuccessful()) {
                            try {
                                data = response.body().string();
                                if (data.equals("")) {
                                    pb.dismiss();
                                    AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);
                                } else {
                                    data = data.substring(1, data.length() - 1).replace("\\", "");
                                    if (data.contains("1")) {
                                        db.open();

                                        specific_uploadStatus.get(0).setUploadStatus(status);
                                        db.updateJaurneyPlanSpecificStoreStatus(coverageList.get(coverageIndex).getStore_Id(), coverageList.get(coverageIndex).getVisitDate(), status);


                                        tempcoverageIndex[0]++;
                                        if (tempcoverageIndex[0] != coverageList.size()) {
                                            uploadDataUsingCoverageRecursive(coverageList, tempcoverageIndex[0]);
                                        } else {
                                            //  uploadimages();
                                            pb.setMessage("uploading images");
                                            File dir = new File(CommonString.FILE_PATH);
                                            if (getFileNames(dir.listFiles()).size() > 0) {
                                                totalFiles = getFileNames(dir.listFiles()).size();
                                                uploadImage(coverageList.get(coverageIndex).getVisitDate());
                                            } else {
                                                db.open();


                                                db.updateJaurneyPlanSpecificStoreStatus(coverageList.get(coverageIndex).getStore_Id(), coverageList.get(coverageIndex).getVisitDate(), status);

                                                updateStatus(coverageList, coverageIndex, status);
                                            }
                                        }
                                    } else {
                                        pb.dismiss();
                                        AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);
                                    }

                                }
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                                pb.dismiss();
                                AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);
                            }
                        } else {
                            pb.dismiss();
                            AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        pb.dismiss();
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);

                    }
                });

            } catch (JSONException ex) {
                pb.dismiss();
                Crashlytics.logException(ex);
                if (ex != null) {
                    AlertandMessages.showAlert((Activity) context, ex.toString(), true);
                } else {
                    AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INVALID_JSON, true);
                }
            }
        }

    }

    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }

        return arrayFiles;
    }


    private void updatestatusforu(final ArrayList<CoverageBean> coverageList, int index, final String visit_date, final String status) {
        try {
            db.open();
            final int[] indexlocal = {index};
            final boolean[] status_u = {false};
            ArrayList<JourneyPlan> store_data = new ArrayList<>();

            store_data = db.getSpecificStoreData(coverageList.get(index).getStore_Id().toString());
            if (store_data.size() > 0) {
                if (store_data.get(0).getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .build();
                    index++;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("StoreId", store_data.get(0).getStoreId());
                    jsonObject.put("VisitDate", visit_date);
                    jsonObject.put("UserId", userId);
                    jsonObject.put("Status", status);
                    RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                    adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    PostApi api = adapter.create(PostApi.class);
                    Call<ResponseBody> call;
                    if (user_type.equalsIgnoreCase("Merchandiser")) {
                        call = api.getCoverageStatusDetail(jsonData);
                    } else {
                        call = api.getCoverageStatusDetailAudit(jsonData);
                    }

                    pb.setMessage("Uploading store status " + (index) + "/" + coverageList.size());

                    final int finalIndex = index;
                    final ArrayList<JourneyPlan> finalStore_data = store_data;
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            ResponseBody responseBody = response.body();
                            String data = null;
                            if (responseBody != null && response.isSuccessful()) {
                                try {
                                    data = response.body().string();
                                    if (data.equals("")) {
                                        pb.dismiss();
                                        status_u[0] = false;
                                        AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + finalIndex, true);
                                    } else {
                                        data = data.substring(1, data.length() - 1).replace("\\", "");
                                        if (data.contains("1")) {
                                            status_u[0] = true;
                                            db.open();
                                            db.updateJaurneyPlanPosmEditableStatus(finalStore_data.get(0).getStoreId().toString(),
                                                    finalStore_data.get(0).getVisitDate(), CommonString.KEY_STATUS_N);
                                            db.updateJaurneyPlanSpecificStoreStatus(finalStore_data.get(0).getStoreId().toString()
                                                    , finalStore_data.get(0).getVisitDate(), status);
                                            db.deleteSpecificStoreData(finalStore_data.get(0).getStoreId().toString());
                                            indexlocal[0]++;
                                            if (indexlocal[0] != coverageList.size()) {
                                                updatestatusforu(coverageList, indexlocal[0], visit_date, CommonString.KEY_U);
                                            } else {
                                                if (status_u[0] == true) {
                                                    pb.dismiss();
                                                    AlertandMessages.showAlert((Activity) context, "All data and images upload Successfully.", true);
                                                }
                                            }
                                        } else {
                                            status_u[0] = false;
                                            pb.dismiss();
                                            AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + finalIndex, true);
                                        }

                                    }
                                } catch (Exception e) {
                                    status_u[0] = false;
                                    pb.dismiss();
                                    Crashlytics.logException(e);
                                    AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + finalIndex, true);
                                }
                            } else {
                                status_u[0] = false;
                                pb.dismiss();
                                AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + finalIndex, true);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            status_u[0] = false;
                            pb.dismiss();
                            if (t == null) {
                                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                            } else {
                                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE + "(" + t.toString() + ")", true);
                            }
                        }
                    });
                }
            }


        } catch (JSONException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            pb.dismiss();
            if (e != null) {
                AlertandMessages.showAlert((Activity) context, e.toString(), true);
            } else {
                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INVALID_JSON, true);
            }
        }
    }

    public static File saveBitmapToFile(File file) {
        File file2 = file;
        try {
            int inWidth = 0;
            int inHeight = 0;
            InputStream in = new FileInputStream(file2);
            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;
            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;
            // decode full image pre-resized
            in = new FileInputStream(file2);
            options = new BitmapFactory.Options();
            // calc rought re-size (this is no exact resize)
            options.inSampleSize = Math.max(inWidth / 800, inHeight / 500);
            // decode full image
            Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

            // calc exact destination size
            Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0, 0, 800, 500);
            m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
            float[] values = new float[9];
            m.getValues(values);

            // resize bitmap
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap,
                    (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);
            // save image
            try {
                FileOutputStream out = new FileOutputStream(file2);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.e("Image", e.toString(), e);
            }
        } catch (IOException e) {
            Log.e("Image", e.toString(), e);
            Crashlytics.logException(e);
            return file2;
        }
        return file;
    }

    String getParsedDate(String filename) {
        String testfilename = filename;
        testfilename = testfilename.substring(testfilename.indexOf("-") + 1);
        testfilename = testfilename.substring(0, testfilename.indexOf("-"));
        return testfilename;
    }


    void uploadImage(String coverageDate) {
        pb.setMessage("updoading images");
        File f = new File(CommonString.FILE_PATH);
        File file[] = f.listFiles();
        if (file.length > 0) {
            UploadImageRecursive(context, coverageDate, coverageList, 0);
        } else {
            pb.setMessage("Updating status");
            updatestatusforu(coverageList, 0, coverageDate, CommonString.KEY_U);
        }
    }

    public void UploadImageRecursive(final Context context, final String covergeDate, final ArrayList<CoverageBean> coverageList,
                                     final int coverageIndex) {
        try {
            statusforimage = 0;
            int totalfiles = 0;
            String filename = null, foldername = null;
            File f = new File(CommonString.FILE_PATH);
            File file[] = f.listFiles();
            count = file.length;
            if (file.length > 0) {
                filename = "";
                totalfiles = f.listFiles().length;
                pb.setMessage("Uploading images" + "(" + uploadedFiles + "/" + totalFiles + ")");
                for (int i = 0; i < file.length; i++) {
                    if (new File(CommonString.FILE_PATH + file[i].getName()).exists()) {
                        if (file[i].getName().contains("_STOREIMG_") || file[i].getName().contains("_NONWORKING_")) {
                            foldername = "CoverageImages";
                        } else if (file[i].getName().contains("_AUDITONEIMG_") || file[i].getName().contains("_AUDITTWOIMG_")) {
                            foldername = "AuditImages";
                        } else if (file[i].getName().contains("_DEPLONEIMG_") || file[i].getName().contains("_DEPLTWOIMG_")) {
                            foldername = "DeploymentImages";
                        } else if (file[i].getName().contains("_VISITINGIMG_")) {
                            foldername = "StoreProfileImages";
                        } else if (file[i].getName().contains("_GEO_TAG_")) {
                            foldername = "GeoTagImages";
                        } else {
                            foldername = "BulkUpload";
                        }
                        filename = file[i].getName();
                    }
                    break;
                }


                File originalFile = new File(CommonString.FILE_PATH + filename);
                File finalFile = saveBitmapToFile(originalFile);

                if (finalFile == null) {
                    finalFile = originalFile;
                }

                String date;
                if (filename.contains("-")) {
                    date = getParsedDate(filename);
                } else {
                    date = visit_date.replace("/", "");
                }


                com.squareup.okhttp.OkHttpClient okHttpClient = new com.squareup.okhttp.OkHttpClient();
                okHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
                okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
                okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
                com.squareup.okhttp.RequestBody photo = com.squareup.okhttp.RequestBody.create(com.squareup.okhttp.MediaType.parse("application/octet-stream"), finalFile);
                body1 = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addFormDataPart("file", finalFile.getName(), photo)
                        .addFormDataPart("FolderName", foldername)
                        .addFormDataPart("Path", date)
                        .build();

                retrofit.Retrofit adapter = new retrofit.Retrofit.Builder()
                        .baseUrl(CommonString.URLGORIMAG)
                        .client(okHttpClient)
                        .addConverterFactory(new StringConverterFactory())
                        .build();
                PostApiForUpload api = adapter.create(PostApiForUpload.class);
                retrofit.Call<String> call = api.getUploadImageRetrofitOne(body1);

                final File finalFile1 = finalFile;
                call.enqueue(new retrofit.Callback<String>() {
                    @Override
                    public void onResponse(retrofit.Response<String> response) {
                        if (response.code() == 200 && response.message().equals("OK") && response.isSuccess() && response.body().contains("Success")) {
                            finalFile1.delete();
                            statusforimage = 1;
                            uploadedFiles++;
                        } else {
                            statusforimage = 0;
                        }
                        if (statusforimage == 0) {
                            pb.dismiss();
                            if (!((Activity) context).isFinishing()) {
                                AlertandMessages.showAlert((Activity) context, "Image not uploaded." + "\n" + uploadedFiles + " images uploaded out of " + totalFiles, true);
                            }
                        } else {
                            UploadImageRecursive(context, covergeDate, coverageList, coverageIndex);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (t instanceof IOException || t instanceof SocketTimeoutException || t instanceof SocketException) {
                            statusforimage = -1;
                            pb.dismiss();
                            if (!((Activity) context).isFinishing()) {
                                AlertandMessages.showAlert((Activity) context, "Network Error in upload." + "\n" + uploadedFiles + " images uploaded out of " + totalFiles, true);
                            }
                        }
                    }
                });

            } else {
                if (totalFiles == uploadedFiles) {
                    pb.setMessage("Updating Status");
                    //for updating status u
                    specific_uploadStatus.get(0).setUploadStatus(CommonString.KEY_U);
                    updatestatusforu(coverageList, 0, visit_date, CommonString.KEY_U);


                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            if (e != null) {
                AlertandMessages.showAlert((Activity) context, e.toString(), true);
            } else {
                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INVALID_JSON, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (totalFiles == uploadedFiles) {
                AlertandMessages.showAlert((Activity) context, "All images uploaded but status not updated", true);
            } else {
                if (e != null) {
                    AlertandMessages.showAlert((Activity) context, CommonString.KEY_FAILURE + " (" + e.toString() + " )", true);
                } else {
                    AlertandMessages.showAlert((Activity) context, CommonString.KEY_FAILURE, true);
                }
            }
        }

    }
}
