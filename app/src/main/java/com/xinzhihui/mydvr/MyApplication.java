package com.xinzhihui.mydvr;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.xinzhihui.mydvr.utils.LogUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/9/29.
 */
public class MyApplication extends Application {

    private final String TAG = getClass().getName();
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initPath(); //初始化文件目录
    }

    private void initPath(){
        makeDir(AppConfig.DVR_PATH);
        makeDir(AppConfig.FRONT_VIDEO_PATH);
        makeDir(AppConfig.BEHIND_VIDEO_PATH);
        makeDir(AppConfig.LEFT_VIDEO_PATH);
        makeDir(AppConfig.RIGHT_VIDEO_PATH);
        makeDir(AppConfig.PICTURE_PATH);
        LogUtil.i(TAG, "Make Dir -------->");
    }

    private void makeDir(String path) {
        File dir = new File(path);
        if (dir.isFile()) {
            dir.delete();
        }
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public static Context getContext() {
        return mContext;
    }
}
