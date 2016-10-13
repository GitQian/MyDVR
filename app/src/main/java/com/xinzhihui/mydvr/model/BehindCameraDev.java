package com.xinzhihui.mydvr.model;

import android.hardware.Camera;
import android.media.MediaRecorder;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.utils.DateTimeUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/9/28.
 */
public class BehindCameraDev extends CameraDev{

    public BehindCameraDev(int cameraId){
        this.cameraid = cameraId;
    }

    @Override
    public MediaRecorder initRecorderParameters(Camera camera, MediaRecorder mediaRecorder) {

        File dir = new File(AppConfig.BEHIND_VIDEO_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(AppConfig.BEHIND_VIDEO_PATH + DateTimeUtil.getCurrentDateTimeReplaceSpace() + ".mp4");

        mediaRecorder.reset();

        mediaRecorder.setCamera(camera);

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); //前置
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //先设置输出格式
        mediaRecorder.setVideoFrameRate(30);

        mediaRecorder.setVideoSize(640, 360);

        mediaRecorder.setVideoEncodingBitRate(6000000);  //6M

        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); //后设置视频编码格式

        mediaRecorder.setOutputFile(file.getAbsolutePath());

        mediaRecorder.setMaxDuration(AppConfig.MAX_DURATION);
        return mediaRecorder;
    }
}
