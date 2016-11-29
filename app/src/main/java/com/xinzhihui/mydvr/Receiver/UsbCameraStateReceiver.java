package com.xinzhihui.mydvr.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xinzhihui.mydvr.utils.LogUtil;

public class UsbCameraStateReceiver extends BroadcastReceiver {

    public UsbCameraStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Only Android 4.4 can receive UsbCameraState broadCast!!!
        String name = intent.getStringExtra("UsbCameraName");
        int state = intent.getIntExtra("UsbCameraState", 5);
        LogUtil.e("qiansheng", "Usb Camera InOrOut!!!" + name + ":" + state);

    }

}
