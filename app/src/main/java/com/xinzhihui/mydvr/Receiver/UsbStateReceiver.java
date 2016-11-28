package com.xinzhihui.mydvr.Receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.ICameraManager;
import com.xinzhihui.mydvr.service.RecordService;
import com.xinzhihui.mydvr.utils.LogUtil;

import java.util.List;

public class UsbStateReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();
    public RecordService mService = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("qiansheng", "USB USB!!!");
        LogUtil.d("qiansheng", "Action:" + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)) {
            String path = intent.getData().getPath();

            LogUtil.d("qiansheng", "ROOT_DIR:" + AppConfig.ROOT_DIR + "Paht:" + path);
            if (AppConfig.ROOT_DIR.startsWith(path)) {
                //如果拔除的存储设备为当前正在使用的设备，则退出!!!
                Intent intent1 = new Intent(context, RecordService.class);
//                MyApplication.getContext().bindService(intent1, myServiceConnection, Context.BIND_AUTO_CREATE);

                IBinder binder = peekService(context, intent1);
                RecordService.LoBinder manager = (RecordService.LoBinder) ICameraManager.Stub.asInterface(binder);
                if (manager == null) {
                    return;
                }
                mService = manager.getService();

                if (mService.isRecording(AppConfig.FRONT_CAMERA)) {
                    mService.stopRecord(AppConfig.FRONT_CAMERA);
//                    mService.startRender(AppConfig.FRONT_CAMERA, null);
                    if (!isForeground(context, "com.xinzhihui.mydvr.CameraActivity")) {
                        //TODO 界面不在前台，还应该releaseCamera，避免下次进入应用camer.open出问题，导致预览界面白屏
                        mService.getCameraDev(AppConfig.FRONT_CAMERA).releaseCameraAndPreview();
                    }

//                    mService.open(AppConfig.FRONT_CAMERA);
//                    System.exit(0);
                }

                if (mService.isRecording(AppConfig.BEHIND_CAMERA)) {
                    mService.stopRecord(AppConfig.BEHIND_CAMERA);
                    if (!isForeground(context, "com.xinzhihui.mydvr.CameraActivity")) {
                        mService.getCameraDev(AppConfig.BEHIND_CAMERA).releaseCameraAndPreview();
                    }
//                    System.exit(0);
                }
            }

            LogUtil.d("qiansheng", "USB被移除!!!" + path);
        }
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     */
    private boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }

}
