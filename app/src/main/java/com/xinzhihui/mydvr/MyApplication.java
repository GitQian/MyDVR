package com.xinzhihui.mydvr;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SPUtils;

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

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);

        initPath(); //初始化文件目录
        initSetting();

        if (checkCameraHardware(this)) {
            LogUtil.d(TAG, "have camera!!");
        } else {
            LogUtil.e(TAG, "no camera!!!");
        }

        LogUtil.d(TAG, "camera number:" + Camera.getNumberOfCameras());
    }

    private void initPath() {
        makeDir(AppConfig.DVR_PATH);
        makeDir(AppConfig.FRONT_VIDEO_PATH);
        makeDir(AppConfig.BEHIND_VIDEO_PATH);
        makeDir(AppConfig.LEFT_VIDEO_PATH);
        makeDir(AppConfig.RIGHT_VIDEO_PATH);
        makeDir(AppConfig.PICTURE_PATH);
        LogUtil.i(TAG, "Make Dir -------->");
    }

    private void initSetting() {
        if ((Boolean) SPUtils.get(mContext, "isFirstApp", true)) {
            SPUtils.put(mContext, "isFirstApp", false);
            //TODO 初始化设置
            SPUtils.put(mContext, AppConfig.KEY_IS_FRONT_SOUND, false);   //默认录制声音
            SPUtils.put(mContext, AppConfig.KEY_IS_BEHIND_SOUND, false);

            SPUtils.put(mContext, AppConfig.KEY_IS_FRONT_WATER, true);   //水印
            SPUtils.put(mContext, AppConfig.KEY_IS_BEHIND_WATER, true);

            SPUtils.put(mContext, AppConfig.KEY_IS_FRONT_AUTO, false);   //录制自启动
            SPUtils.put(mContext, AppConfig.KEY_IS_BEHIND_AUTO, false);

            SPUtils.put(mContext, AppConfig.KEY_APP_AUTO_RUN, true);     //开机自启动
        }
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

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static Context getContext() {
        return mContext;
    }
}
