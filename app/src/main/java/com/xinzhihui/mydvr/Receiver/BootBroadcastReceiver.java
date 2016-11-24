package com.xinzhihui.mydvr.Receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.ICameraManager;
import com.xinzhihui.mydvr.MainActivity;
import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.model.CameraDev;
import com.xinzhihui.mydvr.model.CameraFactory;
import com.xinzhihui.mydvr.service.RecordService;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SPUtils;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getName();
    public RecordService mService = null;
    CameraFactory factory = new CameraFactory();
    public CameraDev cameraDev;

    static final String action_boot = "android.intent.action.BOOT_COMPLETED";

    public BootBroadcastReceiver() {
    }

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

            if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_FRONT_AUTO, false)) {
                if (mService.getCameraDev(AppConfig.FRONT_CAMERA) == null) {
                    cameraDev = factory.createCameraDev(AppConfig.FRONT_CAMERA);
                    mService.addCameraDev(AppConfig.FRONT_CAMERA, cameraDev);
                }
                if (!mService.isRecording(AppConfig.FRONT_CAMERA)) {
                    mService.open(AppConfig.FRONT_CAMERA);
                    mService.startPreView(AppConfig.FRONT_CAMERA, null);
                    mService.startRecord(AppConfig.FRONT_CAMERA);
                }
            }

            if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_BEHIND_AUTO, false)) {
                cameraDev = factory.createCameraDev(AppConfig.BEHIND_CAMERA);
                if (mService.getCameraDev(AppConfig.BEHIND_CAMERA) == null) {
                    mService.addCameraDev(AppConfig.BEHIND_CAMERA, cameraDev);
                }
                if (!mService.isRecording(AppConfig.BEHIND_CAMERA)) {
                    mService.open(AppConfig.BEHIND_CAMERA);
                    mService.startPreView(AppConfig.BEHIND_CAMERA, null);
                    mService.startRecord(AppConfig.BEHIND_CAMERA);
                }
            }
        }
    };


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(action_boot)) {
            boolean isAuto = (Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_APP_AUTO_RUN, true);
            if (isAuto) {
                Intent startIntent = new Intent(context, MainActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startIntent);
            }

            if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_FRONT_AUTO, false)
                    || (Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_BEHIND_AUTO, false)) {

                Intent intent1 = new Intent(context, RecordService.class);
                context.startService(intent1);

                MyApplication.getContext().bindService(intent1, myServiceConnection, Context.BIND_AUTO_CREATE);
            }
        }

    }

}
