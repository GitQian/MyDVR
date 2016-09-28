package com.xinzhihui.mydvr.model;

import android.hardware.Camera;

import com.xinzhihui.mydvr.listener.CameraStatusListener;

/**
 * Created by Administrator on 2016/9/28.
 */
public class FrontCameraDev extends CameraDev{

    public FrontCameraDev(CameraStatusListener statusListener){
        this.cameraid = 0;
        this.statusListener = statusListener;
    }

}
