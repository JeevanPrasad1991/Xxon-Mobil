package jp.cpm.com.xxonmobil.chunkfile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.cpm.com.xxonmobil.constant.CommonString;

/**
 * Created by jeevanp on 24-01-2018.
 */

public class ChunkFileClass {
ArrayList<String>chunkList=new ArrayList<>();
    public List<String> chunk() {
        try {
            File dir = new File(CommonString.FILE_PATH);
            ArrayList<String> list1 = new ArrayList();
            list1 = getFileNames(dir.listFiles());
            File file = new File(CommonString.FILE_PATH + list1.get(0));
            InputStream is = new FileInputStream(file);
            int length = (int) file.length();
            int take = 262144;//size of your chunk
            byte[] bytes = new byte[take];
            int offset = 0;
            int a = 0;
            do {
                a = is.read(bytes, 0, take);
                offset += a;
                //And you can add here each chunk created in to a list, etc, etc.
                //encode to base 64 this is extra :)
                String str = Base64.encodeToString(bytes, Base64.DEFAULT);
                chunkList.add(str);
            } while (offset < length);
            is.close();
            is = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chunkList;
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
                Log.e("Image", e.toString(), e);
            }
        } catch (IOException e) {
            Log.e("Image", e.toString(), e);
            return file2;
        }
        return file;
    }

    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }

}
