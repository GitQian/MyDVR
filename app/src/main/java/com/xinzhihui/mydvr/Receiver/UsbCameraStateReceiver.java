package com.xinzhihui.mydvr.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.listener.CameraStatusListener;
import com.xinzhihui.mydvr.utils.LogUtil;

public class UsbCameraStateReceiver extends BroadcastReceiver {

    private CameraStatusListener mCameraStatusListener;
    public UsbCameraStateReceiver(CameraStatusListener cameraStatusListener) {
        mCameraStatusListener = cameraStatusListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Only Android 4.4 can receive UsbCameraState broadCast!!!
        String name = intent.getStringExtra("UsbCameraName");
        int state = intent.getIntExtra("UsbCameraState", 5);
        LogUtil.e("qiansheng", "Usb Camera InOrOut!!!" + name + ":" + state);
        if (state == 0) {
            //拔除
            int cameraIndex = -1;
            if (AppConfig.FRONT_CAMERA_INDEX == Integer.valueOf(name.substring(name.length() -1))) {
                cameraIndex = AppConfig.FRONT_CAMERA;
            }else if (AppConfig.BEHIND_CAMERA_INDEX == Integer.valueOf(name.substring(name.length() -1))) {
                cameraIndex = AppConfig.BEHIND_CAMERA;
            }
            mCameraStatusListener.onPlugOut(cameraIndex);
        }else if (state == 1){
            //插入
            int cameraIndex = -1;
            if (AppConfig.FRONT_CAMERA_INDEX == Integer.valueOf(name.substring(name.length() -1))) {
                cameraIndex = AppConfig.FRONT_CAMERA;
            }else if (AppConfig.BEHIND_CAMERA_INDEX == Integer.valueOf(name.substring(name.length() -1))) {
                cameraIndex = AppConfig.BEHIND_CAMERA;
            }
            mCameraStatusListener.onPlugIn(cameraIndex);
        }
    }

}
