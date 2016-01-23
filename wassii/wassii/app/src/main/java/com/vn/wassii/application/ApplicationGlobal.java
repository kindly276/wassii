package com.vn.wassii.application;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by thaond on 12/23/2015.
 */
public class ApplicationGlobal extends Application {
    protected File extStorageAppBasePath;

    protected File extStorageAppCachePath;


    @Override
    public File getCacheDir()
    {
        // NOTE: this method is used in Android 2.2 and higher

        if (extStorageAppCachePath != null)
        {
            // Use the external storage for the cache
            return extStorageAppCachePath;
        }
        else
        {
            // /data/data/com.devahead.androidwebviewcacheonsd/cache
            return super.getCacheDir();
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();

        // Check if the external storage is writeable
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            // Retrieve the base path for the application in the external storage
            File externalStorageDir = Environment.getExternalStorageDirectory();

            if (externalStorageDir != null)
            {
                // {SD_PATH}/Android/data/com.devahead.androidwebviewcacheonsd
                extStorageAppBasePath = new File(externalStorageDir.getAbsolutePath() +
                        File.separator + "Android" + File.separator + "data" +
                        File.separator + getPackageName());
            }

            if (extStorageAppBasePath != null)
            {
                // {SD_PATH}/Android/data/com.devahead.androidwebviewcacheonsd/cache
                extStorageAppCachePath = new File(extStorageAppBasePath.getAbsolutePath() +
                        File.separator + "cache/webview");

                boolean isCachePathAvailable = true;

                if (!extStorageAppCachePath.exists())
                {
                    // Create the cache path on the external storage
                    isCachePathAvailable = extStorageAppCachePath.mkdirs();
                }

                if (!isCachePathAvailable)
                {
                    // Unable to create the cache path
                    extStorageAppCachePath = null;
                }
            }
        }
        Picasso.Builder builder = new Picasso.Builder(this);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setCache(new Cache(getExternalCacheDir(this), Integer.MAX_VALUE));
        OkHttpDownloader okHttpDownloader = new OkHttpDownloader(okHttpClient);
        builder.downloader(okHttpDownloader);
        Picasso built = builder.build();
        //built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);



    }
    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            File myFile = context.getExternalCacheDir();
            if(myFile != null)
            {
                return myFile;
            }
            else
            {
                final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/image";
                return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
            }
        }
        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/image";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }
    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }
}