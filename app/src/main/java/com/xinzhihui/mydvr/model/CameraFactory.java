package com.xinzhihui.mydvr.model;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.listener.CameraStatusListener;

/**
 * Created by Administrator on 2016/9/28.
 */
public class CameraFactory {

    public CameraDev createCameraDev(int cameraid, CameraStatusListener statusListener){
        CameraDev cameraDev;
        switch (cameraid) {
            case AppConfig.FRONT_CAMERA:
                cameraDev = new FrontCameraDev(statusListener);
                break;

            case AppConfig.BEHIND_CAMERA:
                cameraDev = new BehindCameraDev(statusListener);
                break;

            case AppConfig.LEFT_CAMERA:
                cameraDev = new FrontCameraDev(statusListener);
                break;

            case AppConfig.RIGHT_CAMERA:
                cameraDev = new FrontCameraDev(statusListener);
                break;

            default:
                cameraDev = new FrontCameraDev(statusListener);
                break;
        }

        return cameraDev;
    }
}
