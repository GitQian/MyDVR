package com.xinzhihui.mydvr.model;

import com.xinzhihui.mydvr.AppConfig;

/**
 * Created by Administrator on 2016/9/28.
 */
public class CameraFactory {

    public CameraDev createCameraDev(int cameraid){
        CameraDev cameraDev;
        switch (cameraid) {
            case AppConfig.FRONT_CAMERA:
                cameraDev = new FrontCameraDev();
                break;

            case AppConfig.BEHIND_CAMERA:
                cameraDev = new BehindCameraDev();
                break;

            case AppConfig.LEFT_CAMERA:
                cameraDev = new FrontCameraDev();
                break;

            case AppConfig.RIGHT_CAMERA:
                cameraDev = new FrontCameraDev();
                break;

            default:
                cameraDev = new FrontCameraDev();
                break;
        }

        return cameraDev;
    }
}
