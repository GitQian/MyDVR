package com.xinzhihui.mydvr.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.CameraActivity;
import com.xinzhihui.mydvr.MainActivity;
import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.utils.SPUtils;

public class BootBroadcastReceiver extends BroadcastReceiver {
    public BootBroadcastReceiver() {
    }

    static final String action_boot = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(action_boot)) {
            boolean isAuto = (Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_APP_AUTO_RUN, true);
            if (isAuto) {
                Intent startIntent = new Intent(context, MainActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startIntent);
            }
        }

    }
}
