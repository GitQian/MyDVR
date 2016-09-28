package com.xinzhihui.mydvr.model;

import com.xinzhihui.mydvr.listener.CameraStatusListener;

/**
 * Created by Administrator on 2016/9/28.
 */
public class BehindCameraDev extends CameraDev{

    public BehindCameraDev(CameraStatusListener statusListener){
        this.cameraid = 1;
        this.statusListener = statusListener;
    }
}
