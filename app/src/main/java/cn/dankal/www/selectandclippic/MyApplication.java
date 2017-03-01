package cn.dankal.www.selectandclippic;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Dankal on 16/8/8.
 */
public class MyApplication extends Application {

//    public static String BasePath;

    @Override
    public void onCreate() {
        super.onCreate();
//        BasePath = Environment.getExternalStorageDirectory().toString() + "/" + getPackageName() + "/";

//        BasePath=getExternalCacheDir().getPath()+"/";
    }


}
