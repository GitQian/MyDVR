package com.xinzhihui.mydvr.model;

import android.hardware.Camera;
import android.media.MediaRecorder;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.utils.ACache;
import com.xinzhihui.mydvr.utils.DateTimeUtil;
import com.xinzhihui.mydvr.utils.SPUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/28.
 */
public class BehindCameraDev extends CameraDev {

    public BehindCameraDev(int cameraIndexId) {
        this.cameraIndexId = cameraIndexId;
        this.cameraId = AppConfig.BEHIND_CAMERA;
    }

    @Override
    public Camera.Parameters initRreviewParameters(Camera.Parameters parameters) {
        //获取并存储摄像头支持的分辨率
        ACache aCache = ACache.get(MyApplication.getContext());
        ArrayList<String> sizeList = new ArrayList<String>();
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            sizeList.add(size.width + "x" + size.height);
        }
        aCache.put("BehindSolution", sizeList);

        //设置Picture大小
        parameters.setPictureSize(parameters.getSupportedPictureSizes().get(0).width, parameters.getSupportedPictureSizes().get(0).height);

        //设置已选Preview分辨率
        int soluWhere = (Integer) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_BEHIND_SOLUTION_WHERE, Integer.valueOf(0));
        if (soluWhere >=  parameters.getSupportedPreviewSizes().size()) {
            soluWhere = 0;
            SPUtils.put(MyApplication.getContext(), AppConfig.KEY_BEHIND_SOLUTION_WHERE, soluWhere);
        }
        parameters.setPreviewSize(parameters.getSupportedPictureSizes().get(soluWhere).width, parameters.getSupportedPictureSizes().get(soluWhere).height);   //后视镜分辨率1600*480，如果设为1920*1080会绿屏！
        return parameters;
    }

    @Override
    public File makeFile() {
        File dir = new File(AppConfig.BEHIND_VIDEO_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(AppConfig.BEHIND_VIDEO_PATH + "Behind_" + DateTimeUtil.getCurrentDateTimeReplaceSpace() + ".mp4");
        return file;
    }

    @Override
    public MediaRecorder initRecorderParameters(Camera camera, MediaRecorder mediaRecorder, File file) {

        mediaRecorder.reset();

        mediaRecorder.setCamera(camera);

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); //前置
        boolean isSound = false;
        isSound = (Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_BEHIND_SOUND, true);
        if (isSound) {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        }
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //先设置输出格式
        mediaRecorder.setVideoFrameRate(30);

        //获取已选择的分辨率
        int soluWhere = (Integer) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_BEHIND_SOLUTION_WHERE, Integer.valueOf(0));
        ACache aCache = ACache.get(MyApplication.getContext());
        ArrayList<String> sizeList = (ArrayList<String>) aCache.getAsObject("BehindSolution");
        if (soluWhere >=  sizeList.size()) {
            soluWhere = 0;
            SPUtils.put(MyApplication.getContext(), AppConfig.KEY_BEHIND_SOLUTION_WHERE, soluWhere);
        }
        String str = sizeList.get(soluWhere);
        int width = Integer.valueOf(str.split("x")[0]);
        int height = Integer.valueOf(str.split("x")[1]);

        mediaRecorder.setVideoSize(width, height);

        mediaRecorder.setVideoEncodingBitRate(6000000);  //6M

        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); //后设置视频编码格式
        if (isSound) {
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        }

        mediaRecorder.setOutputFile(file.getAbsolutePath());

        int where = (Integer) SPUtils.get(MyApplication.getContext(), "BehindTimeSize", 0);
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
