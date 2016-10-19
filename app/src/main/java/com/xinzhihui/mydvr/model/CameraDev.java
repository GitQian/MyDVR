package com.xinzhihui.mydvr.model;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;

import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.asynctask.SavePictureTask;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SPUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/9/28.
 */
public abstract class CameraDev {
    private final String TAG = getClass().getName();

    public int cameraid;

    public Camera camera;
    public MediaRecorder mediaRecorder;

    public File mVideoFile;
    public boolean isLocked = false;

    private boolean isPreviewing = false;
    private boolean isRecording = false;

    public Handler mHandler = null;
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;
    private int mTimeCount = 0;


    /**
     * 打开摄像头
     * @return Camera对象
     */
    public Camera open(){
        if (camera!=null) {
            camera.release();
            camera = null;
        }
        try {
            camera = Camera.open(cameraid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 开始预览
     * @param surface
     */
    public void startPreview(SurfaceTexture surface){
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.getSupportedPreviewSizes().get(0);
                for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                    LogUtil.d("qiansheng", "width:" + size.width + "height:" + size.height);
                }
                for (Camera.Size size : parameters.getSupportedPictureSizes()) {
                    LogUtil.d("qiansheng", "PictureWidth:" + size.width + "PictureHeight:" + size.height);
                }
//                parameters.setPreviewSize(parameters.getSupportedPreviewSizes().get(0).width, parameters.getSupportedPreviewSizes().get(0).height);
                parameters.setPictureSize(parameters.getSupportedPictureSizes().get(0).width, parameters.getSupportedPictureSizes().get(0).height);

                parameters.setPreviewSize(1280, 720);   //后视镜分辨率1600*480，如果设为1920*1080会绿屏！

                camera.setParameters(parameters);

                camera.setPreviewTexture(surface);
                camera.startPreview();

                Class<?> c = camera.getClass();
                Method startRender = c.getMethod("startWaterMark");
                startRender.invoke(camera);
                setPreviewing(true);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止预览，释放资源
     */
    public void stopPreview(){
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;

            setPreviewing(false);
        }else {
            LogUtil.i(TAG, "stopPreView ----------->camera:" + cameraid +  "is null");
        }
    }

    public abstract File makeFile();
    public abstract MediaRecorder initRecorderParameters(Camera camera, MediaRecorder mediaRecorder, File file, boolean isSound);
    /**
     * 开始录像
     * @param
     */
    public void startRecord(){
        if (isRecording()) {
            return;
        }
        if (camera == null) {
            LogUtil.d("qiansheng", "startRecord ------>camera is null!!!!!");
            return;
        }
        mVideoFile = makeFile();

        mediaRecorder = new MediaRecorder();
        camera.unlock();

        boolean isSound = false;
        isSound = (Boolean) SPUtils.get(MyApplication.getContext(), "isSound", true);
        initRecorderParameters(camera, mediaRecorder, mVideoFile, isSound);

        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                switch (what) {
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                        stopRecord();
                        startRecord();
                        break;
                    default:
                        break;
                }
            }
        });

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }



        setRecording(true);

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = cameraid;  //msg.what = cameraid;
                msg.arg1 = 1;  //start
                msg.arg2 = mTimeCount;  //time
                if (mHandler != null) {
                    mHandler.sendMessage(msg);
                }
                mTimeCount ++;
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);

    }

    /**
     * 停止录像
     * @param
     */
    public void stopRecord(){
        if (mediaRecorder != null) {
            try {
                mediaRecorder.setOnInfoListener(null);
                mediaRecorder.stop();
                mediaRecorder.release();

            mediaRecorder = null;

                setRecording(false);
            } catch (IllegalStateException e) {
                e.printStackTrace();
                LogUtil.e(TAG, "stopRecord *************>mediaRecorder stop failed!!!");
            }

            setLocked(false);
            mTimerTask.cancel();
            mTimeCount = 0;
            if (mHandler != null) {
                Message msg = new Message();
                msg.what = cameraid; //msg.what = cameraid;
                msg.arg1 = 0; //stop
                mHandler.sendMessage(msg);
            }
        }else {
            setRecording(false);
            LogUtil.i(TAG, "stopRecord --------->mediaRecorder is null!");
        }

    }

    public void killRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
    }

    public void takePhoto() {
        //TODO 可以设置一个参数，通过参数确定拍照完毕之后是否录像！
        if (camera != null) {
            camera.autoFocus(null);
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    new SavePictureTask().execute(data);
//                    camera.startPreview();
//                    startRecord();
                }
            });
        }
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

    public File getmVideoFile() {
        return mVideoFile;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

}
