package jp.cpm.com.xxonmobil.uploadimagesservice;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.squareup.okhttp.MultipartBuilder;

import java.io.File;
import java.util.ArrayList;

import jp.cpm.com.xxonmobil.constant.CommonString;
import jp.cpm.com.xxonmobil.retrofit.PostApiForImage;
import jp.cpm.com.xxonmobil.retrofit.StringConverterFactory;

import static jp.cpm.com.xxonmobil.upload.UploadDataActivity.saveBitmapToFile;

/**
 * Created by jeevanp on 5/25/2018.
 */


public class AllImagesUploadService {
    int statusforimage=0;
    public static void uploadIMAGES(Context context) {
        if (checkNetIsAvailable(context)) {
            File dir = new File(CommonString.FILE_PATH);
            ArrayList<String> list1 = new ArrayList();
            list1 = getFileNames(dir.listFiles());
            if (list1.size() > 0) {
                uploadImageRecursiveWithIndex(0, list1, context);
            }
        }
    }

    public static void uploadImageRecursiveWithIndex(final int index, final ArrayList<String> filelist, final Context context) {
        final int[] indexlocal = {index};
        String file = null, foldername = null;
        file = filelist.get(indexlocal[0]);

        if (new File(CommonString.FILE_PATH + file).exists()) {
            if (file.contains("_STOREIMG_") || file.contains("_NONWORKING_") || file.contains("_STOREC_OUTIMG_")) {
                foldername = "CoverageImages";
            } else if (file.contains("_AUDITIMG_")) {
                foldername = "AuditImages";
            } else if (file.contains("_AUDITIMG_")) {
                foldername = "AuditImages";
            } else if (file.contains("_SOFTMERCHIMG_")) {
                foldername = "SoftPosmImages";
            } else if (file.contains("_SEMIPMERCHIMG_ONE_") || file.contains("_SEMIPMERCHIMG_TWO_") || file.contains("_SEMIPMERCHIMG_THREE_")) {
                foldername = "PermanantPosmImages";
            } else if (file.contains("_IPOSING_")) {
                foldername = "IPOSImages";
            } else if (file.contains("_RXTING_")) {
                foldername = "RXTImages";
            } else if (file.contains("_MARKETINFOING_")) {
                foldername = "MarketInfoImages";
            } else if (file.contains("_TRAININGIMG_")) {
                foldername = "TrainingImages";
            } else if (file.contains("_GeoTag_")) {
                foldername = "GeoTagImages";
            }
        }

        File originalFile = new File(CommonString.FILE_PATH + file);
        //bitmap
        final File finalFile = saveBitmapToFile(originalFile);
        final com.squareup.okhttp.RequestBody photo = com.squareup.okhttp.RequestBody.create(
                com.squareup.okhttp.MediaType.parse("application/octet-stream"), finalFile);
        com.squareup.okhttp.RequestBody body1 = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", finalFile.getName(), photo)
                .addFormDataPart("FolderName", foldername)
                .build();
        retrofit.Retrofit adapter = new retrofit.Retrofit.Builder()
                .baseUrl(CommonString.URLGORIMAG)
                .addConverterFactory(new StringConverterFactory())
                .build();

        //interface
        PostApiForImage api = adapter.create(PostApiForImage.class);

        retrofit.Call<String> call = api.getUploadImage(body1);
        call.enqueue(new retrofit.Callback<String>() {
            @Override
            public void onResponse(retrofit.Response<String> response) {
                if (response.code() == 200 && response.message().equals("OK") && response.isSuccess() && response.body().contains("Success")) {
                    finalFile.delete();
                    indexlocal[0]++;
                    if (indexlocal[0] == filelist.size()) {
                        Intent intent = new Intent(context, BackgroundService.class);
                        context.stopService(intent);
                    } else {
                        uploadImageRecursiveWithIndex(indexlocal[0], filelist, context);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


    static ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }

    public static boolean checkNetIsAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


/*
    public void UploadImageRecursive(final Context context,final String covergeDate) {
        try {
            int totalfiles = 0;
            String filename = null, foldername = null;
            File f = new File(CommonString.FILE_PATH);
            File file[] = f.listFiles();
            if (file.length > 0) {
                filename = "";
                totalfiles = f.listFiles().length;
                //  totalFiles = f.listFiles().length;
                for (int i = 0; i < file.length; i++) {
                    if (new File(CommonString.FILE_PATH + file[i].getName()).exists()) {

                        if (file[i].getName().contains("_STOREIMG_") || file[i].getName().contains("_NONWORKING_")
                                || file[i].getName().contains("_STOREC_OUTIMG_")) {
                            foldername = "CoverageImages";
                        } else if (file[i].getName().contains("_AUDITIMG_")) {
                            foldername = "AuditImages";
                        } else if (file[i].getName().contains("_AUDITIMG_")) {
                            foldername = "AuditImages";
                        } else if (file[i].getName().contains("_SOFTMERCHIMG_")) {
                            foldername = "SoftPosmImages";
                        } else if (file[i].getName().contains("_SEMIPMERCHIMG_ONE_") || file[i].getName().
                                contains("_SEMIPMERCHIMG_TWO_") || file[i].getName().contains("_SEMIPMERCHIMG_THREE_")) {
                            foldername = "PermanantPosmImages";
                        } else if (file[i].getName().contains("_IPOSING_")) {
                            foldername = "IPOSImages";
                        } else if (file[i].getName().contains("_RXTING_")) {
                            foldername = "RXTImages";
                        } else if (file[i].getName().contains("_MARKETINFOING_")) {
                            foldername = "MarketInfoImages";
                        } else if (file[i].getName().contains("_TRAININGIMG_")) {
                            foldername = "TrainingImages";
                        } else if (file[i].getName().contains("_GeoTag_")) {
                            foldername = "GeoTagImages";
                        }
                        filename = file[i].getName();
                    }
                    break;
                }


                File originalFile = new File(CommonString.FILE_PATH + filename);
                final File finalFile = saveBitmapToFile(originalFile);


                com.squareup.okhttp.OkHttpClient okHttpClient = new com.squareup.okhttp.OkHttpClient();
                okHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
                okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
                okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);

                com.squareup.okhttp.RequestBody photo = com.squareup.okhttp.RequestBody.create
                        (com.squareup.okhttp.MediaType.parse("application/octet-stream"), finalFile);
                com.squareup.okhttp.RequestBody body1 = new MultipartBuilder()
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

                call.enqueue(new retrofit.Callback<String>() {
                    @Override
                    public void onResponse(retrofit.Response<String> response) {
                        if (response.code()==200 && response.message().equals("OK") &&  response.isSuccess() && response.body().contains("Success")) {
                            finalFile.delete();
                            statusforimage = 1;
                            uploadedFiles++;
                        } else {
                            statusforimage = 0;
                        }
                        if (statusforimage == 0) {

                        } else {
                            UploadImageRecursive(context,covergeDate);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (t instanceof IOException || t instanceof SocketTimeoutException || t instanceof SocketException) {
                            statusforimage = -1;
                        }
                    }
                });


            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INVALID_JSON, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
*/

}
