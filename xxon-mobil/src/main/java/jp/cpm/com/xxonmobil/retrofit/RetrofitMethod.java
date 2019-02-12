package jp.cpm.com.xxonmobil.retrofit;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;


import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import jp.cpm.com.xxonmobil.Database.Xxon_Database;
import jp.cpm.com.xxonmobil.R;
import jp.cpm.com.xxonmobil.constant.AlertandMessages;
import jp.cpm.com.xxonmobil.constant.CommonString;
import okhttp3.RequestBody;
import retrofit.Call;
import retrofit.Callback;

import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by deepakp on 10/4/2017.
 */

public class RetrofitMethod {

    RequestBody body1;
    PostApi api;
    Call<String> call;
    private Retrofit adapter;
    int status = 0;
    int count = 0;
    public static int uploadedFiles = 0;
    public static int totalFiles = 0;
    boolean isvalid = false, statusUpdated = true;
    Context context;
    String visitDate, userID, uploadStatus;
    int storeId = 0;
    Xxon_Database db;
    ProgressDialog pd;
    public int listSize = 0;
    // ArrayList<JourneyPlan> storeList, storeList_deviation;

    public RetrofitMethod(Context context) {
        this.context = context;
        pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setTitle("Please wait");
        pd.setMessage("Uploading images");
        pd.show();
    }

    public RetrofitMethod() {
    }

    public RetrofitMethod(Context context, String msg) {
        this.context = context;
        pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setTitle("Please wait");
        pd.setMessage(msg);
        pd.show();
    }


    public RetrofitMethod(String visitDate, String userId, Context context) {
        this.visitDate = visitDate;
        this.userID = userId;
        this.context = context;
        pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setTitle("Please wait");
        pd.setMessage("Uploading images");
        pd.show();
    }


    public void UploadBackup(final String filename, String foldername, String folderPath) {
        try {
            status = 0;
            final File finalFile = new File(folderPath + filename);
            isvalid = false;
          /*  RequestBody photo = RequestBody.create(MediaType.parse("application/octet-stream"), finalFile);
            body1 = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("file", finalFile.getName(), photo)
                    .addFormDataPart("Foldername", foldername)
                    .build();
*/

            adapter = new Retrofit.Builder()
                    .baseUrl(CommonString.URL)
                    .addConverterFactory(new StringConverterFactory())
                    .build();
            PostApi api = adapter.create(PostApi.class);
            Call<String> call = api.getUploadDataBaseBackup(body1);
            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Response<String> response) {
                    if (response.isSuccess() && response.body().contains("Success")) {
                        finalFile.delete();
                        pd.dismiss();
                        AlertandMessages.showToastMsg(context, context.getString(R.string.data_uploaded_successfully));

                    } else {
                        pd.dismiss();
                        AlertandMessages.showAlert((Activity) context, context.getString(R.string.database_not_uploaded), true);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    if (t instanceof IOException || t instanceof SocketTimeoutException || t instanceof SocketException) {
                        pd.dismiss();
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION, true);
                    } else {
                        pd.dismiss();
                        AlertandMessages.showAlert((Activity) context, context.getString(R.string.errordatabase_not_uploaded), true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pd.dismiss();
            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_EXCEPTION, true);
        }
    }

    String getParsedDate(String filename) {
        String testfilename = filename;
        testfilename = testfilename.substring(testfilename.indexOf("-") + 1);
        testfilename = testfilename.substring(0, testfilename.indexOf("-"));
        return testfilename;
    }

}
