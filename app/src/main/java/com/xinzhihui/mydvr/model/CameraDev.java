package com.xinzhihui.mydvr.model;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;

import com.xinzhihui.mydvr.listener.CameraStatusListener;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/9/28.
 */
public abstract class CameraDev {

    public int cameraid;
    public CameraStatusListener statusListener;

    private Camera camera;

    private boolean isPreviewing = false;
    private boolean isRecording = false;

    private Handler handler;

    /**
     * 打开摄像头
     * @return Camera对象
     */
    public Camera open(Handler handler){
        camera = Camera.open(cameraid);
        this.handler = handler;
        return camera;
    }

    /**
     * 开始预览
     * @param surface
     */
    public void startPreview(SurfaceTexture surface){
        if (camera != null) {
            try {
                camera.setPreviewTexture(surface);
                camera.startPreview();
                setPreviewing(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止预览，释放资源
     */
    public void stopPreview(){
        camera.stopPreview();
        camera.release();

        setPreviewing(false);
    }

    /**
     * 开始录像
     * @param mediaRecorder
     * @param file
     */
    public void startRecord(MediaRecorder mediaRecorder, File file){
        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        camera.unlock();

        mediaRecorder.reset();

        mediaRecorder.setCamera(camera);

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); //前置
        mediaRecorder.setOutputFormat(camcorderProfile.fileFormat); //先设置输出格式
        mediaRecorder.setVideoEncoder(camcorderProfile.videoCodec); //后设置视频编码格式
        mediaRecorder.setVideoSize(1920, 1080);
        mediaRecorder.setVideoFrameRate(camcorderProfile.videoFrameRate);
        mediaRecorder.setOutputFile(file.getAbsolutePath());

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();

        setRecording(true);
        handler.sendEmptyMessage(1);
        statusListener.onStartRecord();
    }

    /**
     * 停止录像
     * @param mediaRecorder
     */
    public void stopRecord(MediaRecorder mediaRecorder){
        mediaRecorder.stop();
        mediaRecorder.release();

        setRecording(false);
    }

    public void setPreviewing(boolean previewing) {
        isPreviewing = previewing;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public boolean isPreviewing() {
        return isPreviewing;
    }

    public boolean isRecording() {
        return isRecording;
    }
}
