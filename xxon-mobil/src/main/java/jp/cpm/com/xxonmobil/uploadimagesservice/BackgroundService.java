package jp.cpm.com.xxonmobil.uploadimagesservice;

import android.app.*;
import android.content.*;
import android.os.*;

import java.io.File;
import java.util.ArrayList;

import jp.cpm.com.xxonmobil.constant.CommonString;

import static jp.cpm.com.xxonmobil.uploadimagesservice.AllImagesUploadService.getFileNames;

/**
 * Created by jeevanp on 5/25/2018.
 */

public class BackgroundService extends Service {
    private boolean isRunning;
    private Context context;
    Thread backgroundThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            // Do something here
            File dir = new File(CommonString.FILE_PATH);
            ArrayList<String> list1 = new ArrayList();
            list1 = getFileNames(dir.listFiles());
            if (!AllImagesUploadService.checkNetIsAvailable(context)) {
                stopSelf();
            } else if (AllImagesUploadService.checkNetIsAvailable(context) && list1.size() > 0) {
                AllImagesUploadService.uploadIMAGES(context);
            } else {
                stopSelf();
            }
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }
}




















 /*   File dir = new File(CommonString.FILE_PATH);
    ArrayList<String> list1 = new ArrayList();
        list1 = getFileNames(dir.listFiles());
                if (!AllImagesUploadService.checkNetIsAvailable(context) && list1.size()==0){
                stopSelf();
                }else if (!AllImagesUploadService.checkNetIsAvailable(context)){
                stopSelf();
                }*/