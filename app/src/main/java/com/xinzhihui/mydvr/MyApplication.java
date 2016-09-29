package com.xinzhihui.mydvr;

import android.app.Application;

import java.io.File;

/**
 * Created by Administrator on 2016/9/29.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initPath(); //初始化文件目录
    }

    private void initPath(){
        makeDir(AppConfig.DVR_PATH);
        makeDir(AppConfig.FRONT_VIDEO_PATH);
        makeDir(AppConfig.BEHIND_VIDEO_PATH);
        makeDir(AppConfig.LEFT_VIDEO_PATH);
        makeDir(AppConfig.RIGHT_VIDEO_PATH);
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
}
