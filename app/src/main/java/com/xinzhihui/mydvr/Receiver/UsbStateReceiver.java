package com.xinzhihui.mydvr.Receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.CameraActivity;
import com.xinzhihui.mydvr.ICameraManager;
import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.model.CameraDev;
import com.xinzhihui.mydvr.model.CameraFactory;
import com.xinzhihui.mydvr.service.RecordService;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SPUtils;

public class UsbStateReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();
    public RecordService mService = null;
    CameraFactory factory = new CameraFactory();
    public CameraDev cameraDev;

    private ServiceConnection myServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "onServiceConnected ------->");
            RecordService.LoBinder manager = (RecordService.LoBinder) ICameraManager.Stub.asInterface(service);
            mService = manager.getService();
//            mService = ((RecordService.LocalBinder) service).getService();

            if (mService.isRecording(AppConfig.FRONT_CAMERA)) {
                mService.stopRecord(AppConfig.FRONT_CAMERA);
                System.exit(0);
            }

            if (mService.isRecording(AppConfig.BEHIND_CAMERA)) {
                mService.stopRecord(AppConfig.BEHIND_CAMERA);
                System.exit(0);
            }
        }
    };

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
                MyApplication.getContext().bindService(intent1, myServiceConnection, Context.BIND_AUTO_CREATE);
            }

            LogUtil.d("qiansheng", "USB被移除!!!" + path);
        }
    }

}
