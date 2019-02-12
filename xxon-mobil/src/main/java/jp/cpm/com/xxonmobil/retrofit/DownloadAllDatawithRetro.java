package jp.cpm.com.xxonmobil.retrofit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.gettersetter.ReferenceVariablesForDownloadActivity;
import jp.cpm.com.xxonmobil.gsonGetterSetter.AllObjectGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.JCPGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.NonWorkingReasonGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.PosmMasterGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.ReportGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.TableStructure;
import jp.cpm.com.xxonmobil.gsonGetterSetter.TableStructureGetterSetter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jeevanp on 15-12-2017.
 */

public class DownloadAllDatawithRetro extends ReferenceVariablesForDownloadActivity {
    boolean isvalid;
    private Retrofit adapter;
    Context context;
    public int listSize = 0;
    int status = 0;
    Xxon_Database db;
    ProgressDialog pd;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String _UserId, date, app_ver;
    int from;

    public DownloadAllDatawithRetro(Context context) {
        this.context = context;
    }

    public DownloadAllDatawithRetro(Context context, Xxon_Database db, ProgressDialog pd, int from) {

        this.context = context;
        this.db = db;
        this.pd = pd;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        this.from = from;
        db.open();
    }


    public void downloadDataUniversalWithoutWait(final ArrayList<String> jsonStringList, final ArrayList<String> KeyNames, int downloadindex, int type, final String visit_date) {
        status = 0;
        isvalid = false;
        final String[] data_global = {""};
        String jsonString = "", KeyName = "";
        int jsonIndex = 0;

        if (jsonStringList.size() > 0) {
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();
            jsonString = jsonStringList.get(downloadindex);
            KeyName = KeyNames.get(downloadindex);
            jsonIndex = downloadindex;

            pd.setMessage("Downloading (" + downloadindex + "/" + listSize + ") \n" + KeyName + "");
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
            adapter = new Retrofit.Builder().baseUrl(CommonString.URL).client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            PostApi api = adapter.create(PostApi.class);
            Call<String> call = api.getDownloadAll(jsonData);
            final int[] finalJsonIndex = {jsonIndex};
            final String finalKeyName = KeyName;

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String responseBody = response.body();
                    String data = null;
                    if (responseBody != null && response.isSuccessful()) {
                        try {
                            data = response.body();
                            if (data.equals("")) {
                                data_global[0] = "";
                            } else {
                                data_global[0] = data;
                                if (finalKeyName.equalsIgnoreCase("Table_Structure")) {
                                    editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                    editor.apply();
                                    tableStructureObj = new Gson().fromJson(data, TableStructureGetterSetter.class);
                                    String isAllTableCreated = createTable(tableStructureObj);
                                    if (isAllTableCreated != CommonString.KEY_SUCCESS) {
                                        pd.dismiss();
                                        AlertandMessages.showAlert((Activity) context, isAllTableCreated + " not created", true);
                                    }
                                } else {
                                    editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                    editor.apply();
                                    switch (finalKeyName) {
                                        case "Mapping_User_City":
                                            if (!data.contains("No Data")) {
                                                mapping_cityObject = new Gson().fromJson(data, AllObjectGetterSetter.class);
                                                if (mapping_cityObject != null && !db.insertmappinguserCity(mapping_cityObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Mapping user city data not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;

                                        case "Non_Working_Reason":
                                            if (!data.contains("No Data")) {
                                                nonWorkingObj = new Gson().fromJson(data, NonWorkingReasonGetterSetter.class);
                                                if (nonWorkingObj != null && !db.insertNonWorkingData(nonWorkingObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Non Working Reason not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;
                                        case "Posm_Master":
                                            if (!data.contains("No Data")) {
                                                posmMObject = new Gson().fromJson(data, PosmMasterGetterSetter.class);
                                                if (posmMObject != null && !db.insertPosmMaster(posmMObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Posm Master not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;
                                        case "Non_Posm_Reason":
                                            if (!data.contains("No Data")) {
                                                nonposm_reason = new Gson().fromJson(data, PosmMasterGetterSetter.class);
                                                if (nonposm_reason != null && !db.insertnonposmReason(nonposm_reason)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Non Posm Reason not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;
                                        case "BeatWise_Report":
                                            if (!data.contains("No Data")) {
                                                beatwisereportObject = new Gson().fromJson(data, ReportGetterSetter.class);
                                                if (beatwisereportObject != null) {
                                                    db.insertbeatwisereportDat(beatwisereportObject);
                                                }
                                            }
                                            break;

                                        case "Posm_Checklist":
                                            if (!data.contains("No Data")) {
                                                posm_checklistObject = new Gson().fromJson(data, PosmMasterGetterSetter.class);
                                                if (posm_checklistObject != null && !db.insertposm_checklist(posm_checklistObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Posm Checklist not saved");
                                                }
                                            } else {
                                                throw new java.lang.Exception();
                                            }
                                            break;

                                        case "Mapping_Posm_Checklist":
                                            if (!data.contains("No Data")) {
                                                mapping_posmchecklistObject = new Gson().fromJson(data, PosmMasterGetterSetter.class);
                                                if (mapping_posmchecklistObject != null && !db.insertmappingchecklistposm(mapping_posmchecklistObject)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Mapping Posm Checklist not saved");
                                                }
                                            }
                                            break;



                                        case CommonString.KEY_MAPPING_POSM:
                                            if (!data.contains("No Data")) {
                                                mappingposm = new Gson().fromJson(data, JCPGetterSetter.class);
                                                if (mappingposm != null) {
                                                    db.insertmappingposm(mappingposm);
                                                }
                                            }

                                            break;


                                        case "DSRwise_Report":
                                            if (!data.contains("No Data")) {
                                                DSRwisereportObject = new Gson().fromJson(data, ReportGetterSetter.class);
                                                if (DSRwisereportObject != null) {
                                                    db.insertDSRWiseReportData(DSRwisereportObject);
                                                }
                                            }

                                            break;

                                        case CommonString.KEY_UPLOADED_POSM_DATA:
                                            if (!data.contains("No Data")) {
                                                uploadedposmdata = new Gson().fromJson(data, JCPGetterSetter.class);
                                                if (uploadedposmdata != null) {
                                                    db.insertuploadedposmData(uploadedposmdata);
                                                }
                                            }
                                            break;

                                    }
                                }
                            }

                            finalJsonIndex[0]++;
                            if (finalJsonIndex[0] != KeyNames.size()) {
                                editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                editor.apply();
                                downloadDataUniversalWithoutWait(jsonStringList, KeyNames, finalJsonIndex[0], CommonString.DOWNLOAD_ALL_SERVICE, visit_date);
                            } else {
                                editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
                                editor.apply();
                                pd.setMessage("Downloading Images");
                                new DownloadImageTask().execute();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                            editor.apply();
                            pd.dismiss();
                            AlertandMessages.showAlert((Activity) context, finalKeyName + " Data not found ", true);
                        }
                    } else {
                        editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                        editor.apply();
                        pd.dismiss();
                        AlertandMessages.showAlert((Activity) context, "Error in downloading Data at " + finalKeyName, true);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    isvalid = true;
                    pd.dismiss();
                    if (t.toString() != null) {
                        if (t instanceof SocketTimeoutException || t instanceof IOException || t instanceof SocketException)
                            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    } else {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    }
                }
            });
        } else {
            editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
            editor.apply();
            // pd.dismiss();
            // AlertandMessages.showAlert((Activity) context, "All data downloaded Successfully", true);
            pd.setMessage("Downloading Images");
            new DownloadImageTask().execute();
        }
    }

    String createTable(TableStructureGetterSetter tableGetSet) {
        List<TableStructure> tableList = tableGetSet.getTableStructure();
        for (int i = 0; i < tableList.size(); i++) {
            String table = tableList.get(i).getSqlText();
            if (db.createtable(table) == 0) {
                return table;
            }
        }
        return CommonString.KEY_SUCCESS;
    }

    class DownloadImageTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                downloadImages();
                return CommonString.KEY_SUCCESS;
            } catch (FileNotFoundException ex) {
                return CommonString.KEY_FAILURE;
            } catch (IOException ex) {
                return CommonString.KEY_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && s.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                pd.dismiss();
                AlertandMessages.showAlert((Activity) context, "All data downloaded Successfully", true);
            } else {
                pd.dismiss();
                AlertandMessages.showAlert((Activity) context, "Error in downloading", true);
            }

        }

    }

    void downloadImages() throws IOException, FileNotFoundException {
    }
}
