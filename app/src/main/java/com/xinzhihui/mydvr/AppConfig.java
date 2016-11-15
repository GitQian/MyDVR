package com.xinzhihui.mydvr;

import com.xinzhihui.mydvr.utils.SDCardUtils;

/**
 * Created by Administrator on 2016/9/28.
 */
public class AppConfig {

    //默认两路
    public static final int CAMERA_AMOUNT = 2;

    /***************
     * 名义 id
     ************/
    //前路设备（数组下标-0）
    public static final int FRONT_CAMERA = 0;
    //后路设备
    public static final int BEHIND_CAMERA = 1;
    //左路设备
    public static final int LEFT_CAMERA = 20;
    //右路设备
    public static final int RIGHT_CAMERA = 30;

    /***************
     * 真实 id
     ****************/
    public static int FRONT_CAMERA_INDEX = 0;
    public static int BEHIND_CAMERA_INDEX = 1;
    public static int LEFT_CAMERA_INDEX = 20;
    public static int RIGHT_CAMERA_INDEX = 30;

    // 硬件层的前后左右摄像头顺序 在Camera.open(id)对应id的值;
    public static final int FRONT_CAMERA_DEV_INDEX = 0;

    public static final int BEHIND_CAMERA_DEV_INDEX = 2;

    public static final int LEFT_CAMERA_DEV_INDEX = 4;

    public static final int RIGHT_CAMERA_DEV_INDEX = 5;


    //文件路径
    public static String DVR_PATH = SDCardUtils.getSDCardPath() + "DVR";
    public static String FRONT_VIDEO_PATH = DVR_PATH + "/front/";
    public static String BEHIND_VIDEO_PATH = DVR_PATH + "/behind/";
    public static String LEFT_VIDEO_PATH = DVR_PATH + "/left/";
    public static String RIGHT_VIDEO_PATH = DVR_PATH + "/right/";
    public static String PICTURE_PATH = DVR_PATH + "/picture/";

    //录制时长
    public static final int DEFAULT_MAX_DURATION = 1 * 60 * 1000;
    public static final int ONE_MINUTE_DURATION = 1 * 60 * 1000;
    public static final int THREE_MINUTE_DURATION = 1 * 90 * 1000;
    public static final int FIVE_MINUTE_DURATION = 1 * 120 * 1000;

    //SharedPreferences Key
    public static final String KEY_IS_FRONT_SOUND = "isFrontSound";
    public static final String KEY_IS_BEHIND_SOUND = "isBehindSound";
    public static final String KEY_IS_FRONT_WATER = "isFrontWater";
    public static final String KEY_IS_BEHIND_WATER = "isBehindWater";
    public static final String KEY_IS_FRONT_AUTO = "isFrontAuto";
    public static final String KEY_IS_BEHIND_AUTO = "isBehindAuto";

    public static final String KEY_FRONT_SOLUTION_WHERE = "FrontSolutionWhere";
    public static final String KEY_BEHIND_SOLUTION_WHERE = "BehindSolutionWhere";

    public static final String KEY_APP_AUTO_RUN = "isAppAutoRun";
}
