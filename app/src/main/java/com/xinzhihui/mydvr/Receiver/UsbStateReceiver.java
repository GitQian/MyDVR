package com.xinzhihui.mydvr.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.CameraActivity;
import com.xinzhihui.mydvr.utils.LogUtil;

public class UsbStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("qiansheng", "USB USB!!!");
        if (intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)) {
            String path = intent.getData().getPath();

            if (path.equalsIgnoreCase(AppConfig.ROOT_DIR)) {
                //如果拔除的存储设备为当前正在使用的设备，则退出!!!
                CameraActivity cameraActivity = (CameraActivity) context;
                if (cameraActivity.mService.isRecording(AppConfig.FRONT_CAMERA_INDEX) || cameraActivity.mService.isRecording(AppConfig.BEHIND_CAMERA_INDEX)) {
//                    Toast.makeText(MyApplication.getContext(), "存储设备已拔除，暂停录制！！！", Toast.LENGTH_LONG).show();
                    System.exit(0);
                }
            }

            LogUtil.d("qiansheng", "USB被移除!!!" + path);
        }
    }

}
