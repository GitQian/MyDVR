package com.xinzhihui.mydvr.model;

import android.hardware.Camera;
import android.media.MediaRecorder;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.utils.ACache;
import com.xinzhihui.mydvr.utils.DateTimeUtil;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SPUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/28.
 */
public class FrontCameraDev extends CameraDev {

    public FrontCameraDev(int cameraId) {
        this.cameraid = cameraId;
    }

    @Override
    public File makeFile() {
        File dir = new File(AppConfig.FRONT_VIDEO_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(AppConfig.FRONT_VIDEO_PATH + "Front_" + DateTimeUtil.getCurrentDateTimeReplaceSpace() + ".mp4");
        return file;
    }

    @Override
    public MediaRecorder initRecorderParameters(Camera camera, MediaRecorder mediaRecorder, File file) {

        mediaRecorder.reset();

        mediaRecorder.setCamera(camera);

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); //前置
        boolean isSound = false;
        isSound = (Boolean) SPUtils.get(MyApplication.getContext(), "isFrontSound", true);
        if (isSound) {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        }
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //先设置输出格式
        mediaRecorder.setVideoFrameRate(30);

        //获取已选择的分辨率
        int soluWhere = (Integer) SPUtils.get(MyApplication.getContext(), "FrontSolutionWhere", Integer.valueOf(0));
        ACache aCache = ACache.get(MyApplication.getContext());
        ArrayList<String> sizeList = (ArrayList<String>) aCache.getAsObject("FrontSolution");
        String str = sizeList.get(soluWhere);
        int width = Integer.valueOf(str.split("x")[0]);
        int height = Integer.valueOf(str.split("x")[1]);
        LogUtil.d("qiansheng", "recordWidth:" + width + " " + "recordHeight:" + height);

        mediaRecorder.setVideoSize(width, height);

        mediaRecorder.setVideoEncodingBitRate(6000000);  //6M

        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); //后设置视频编码格式
        if (isSound) {
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        }

        mediaRecorder.setOutputFile(file.getAbsolutePath());

        int where = (Integer) SPUtils.get(MyApplication.getContext(), "FrontTimeSize", 0);
        int duration = AppConfig.DEFAULT_MAX_DURATION;
        switch (where) {
            case 0:
                duration = AppConfig.DEFAULT_MAX_DURATION;
                break;
            case 1:
                duration = AppConfig.THREE_MINUTE_DURATION;
                break;
            case 2:
                duration = AppConfig.FIVE_MINUTE_DURATION;
                break;
        }
        mediaRecorder.setMaxDuration(duration);
        return mediaRecorder;
    }
}
