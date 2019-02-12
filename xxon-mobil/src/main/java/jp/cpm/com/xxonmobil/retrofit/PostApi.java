package jp.cpm.com.xxonmobil.retrofit;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import jp.cpm.com.xxonmobil.constant.CommonString;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.Call;


/**
 * Created by jeevanp on 19-05-2017.
 */


//using interface for post data
public interface PostApi {
    @retrofit2.http.POST(CommonString.KEY_LOGIN_DETAILS)
    retrofit2.Call<ResponseBody> getLogindetail(@retrofit2.http.Body okhttp3.RequestBody request);

  @retrofit2.http.POST("UploadAttendanceDetail")
    retrofit2.Call<ResponseBody> getAttendanceDetails(@retrofit2.http.Body okhttp3.RequestBody request);

    @retrofit2.http.POST("DownloadAll")
    Call<String> getDownloadAll(@Body RequestBody request);

    @retrofit2.http.POST("DownloadAll")
    Call<ResponseBody> getDownloadAllUSINGLOGIN(@Body RequestBody request);


    @retrofit2.http.POST("CoverageDetail_latest")
    retrofit2.Call<ResponseBody> getCoverageDetail(@retrofit2.http.Body okhttp3.RequestBody request);

    @retrofit2.http.POST("CoverageDetail_latestAudit")
    retrofit2.Call<ResponseBody> getCoverageDetailAudit(@retrofit2.http.Body okhttp3.RequestBody request);


    @retrofit2.http.POST("UploadJsonDetail")
    retrofit2.Call<ResponseBody> getUploadJsonDetail(@retrofit2.http.Body okhttp3.RequestBody request);

    @retrofit2.http.POST("CoverageStatusDetail")
    retrofit2.Call<ResponseBody> getCoverageStatusDetail(@retrofit2.http.Body okhttp3.RequestBody request);

@retrofit2.http.POST("CoverageStatusDetailAudit")
    retrofit2.Call<ResponseBody> getCoverageStatusDetailAudit(@retrofit2.http.Body okhttp3.RequestBody request);


    @retrofit2.http.POST("CheckoutDetail")
    retrofit2.Call<ResponseBody> getCheckout(@retrofit2.http.Body okhttp3.RequestBody request);
 @retrofit2.http.POST("CheckoutDetailAudit")
    retrofit2.Call<ResponseBody> getCheckoutAudit(@retrofit2.http.Body okhttp3.RequestBody request);

    @retrofit2.http.POST("DeleteCoverage")
    retrofit2.Call<ResponseBody> deleteCoverageData(@retrofit2.http.Body okhttp3.RequestBody request);

  @retrofit2.http.POST("DeleteCoverageAudit")
    retrofit2.Call<ResponseBody> deleteCoverageDataAudit(@retrofit2.http.Body okhttp3.RequestBody request);

    @POST("Uploadimageswithpath")
    retrofit.Call<String> getUploadDataBaseBackup(@Body RequestBody body1);
    @POST("UploadJsonDetail")
    Call<JsonObject> getGeotag(@Body RequestBody request);

}

