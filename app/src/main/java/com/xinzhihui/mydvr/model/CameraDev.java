package com.xinzhihui.mydvr.model;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.asynctask.SavePictureTask;
import com.xinzhihui.mydvr.utils.ACache;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SPUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
     *
     * @return Camera对象
     */
    public Camera open() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        try {
            camera = Camera.open(cameraid);
            LogUtil.d(TAG, "open -------> cameraId:" + cameraid);
        } catch (Exception e) {
            LogUtil.e(TAG, "open -------> cameraId:" + cameraid + "error!!!!");
            e.printStackTrace();
        }
        return camera;
    }

    public abstract Camera.Parameters initRreviewParameters(Camera.Parameters parameters);

    /**
     * 开始预览
     *
     * @param surface
     */
    public void startPreview(SurfaceTexture surface) {
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();

                parameters = initRreviewParameters(parameters);

                camera.setParameters(parameters);

                camera.setPreviewTexture(surface);
                camera.startPreview();
                LogUtil.d(TAG, "startPreview -------> have started");

                //水印
                if (cameraid == AppConfig.FRONT_CAMERA) {
                    if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_FRONT_WATER, true)) {
                        Class<?> c = camera.getClass();
                        Method startRender = c.getMethod("startWaterMark");
                        startRender.invoke(camera);
                        setPreviewing(true);
                    }
                } else if (cameraid == AppConfig.BEHIND_CAMERA) {
                    if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_BEHIND_WATER, true)) {
                        Class<?> c = camera.getClass();
                        Method startRender = c.getMethod("startWaterMark");
                        startRender.invoke(camera);
                        setPreviewing(true);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                LogUtil.e(TAG, "startPreview -------> not have startWaterMark method!!!");
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
    public void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;

            setPreviewing(false);
            LogUtil.d(TAG, "stopPreView ------->have stoped");
        } else {
            LogUtil.e(TAG, "stopPreView ----------->camera:" + cameraid + "is null");
        }
    }

    public abstract File makeFile();

    public abstract MediaRecorder initRecorderParameters(Camera camera, MediaRecorder mediaRecorder, File file);

    /**
     * 开始录像
     *
     * @param
     */
    public void startRecord() {
        if (isRecording()) {
            return;
        }
        if (camera == null) {
            LogUtil.d(TAG, "startRecord ------>camera is null!!!!!");
            return;
        }
        mVideoFile = makeFile();

        mediaRecorder = new MediaRecorder();
        camera.unlock();

        initRecorderParameters(camera, mediaRecorder, mVideoFile);

        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                switch (what) {
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
//                        Class<?> c = mediaRecorder.getClass();
//                        Method startRender = null;
//                        try {
//                            startRender = c.getMethod("setNextSaveFile", String.class);
//                            startRender.invoke(mediaRecorder,mVideoFile.getAbsolutePath());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            LogUtil.e("qiansheng", "setNextSaveFile Error!!!");
//                        }
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
            LogUtil.e(TAG, "startRecord ------> cameraDev:" + cameraid + " " + "startRecord!!!");
        } catch (IOException e) {
            LogUtil.e(TAG, "startRecord ------> cameraDev:" + cameraid + " " + "startRecord error!!!");
            e.printStackTrace();
        }


        setRecording(true);

        sendMessage(mHandler, cameraid, 1, 0);
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                sendMessage(mHandler, cameraid, 2, mTimeCount);
                mTimeCount++;
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);

    }

    private void sendMessage(Handler handler, int msgWhat, int msgArg1, int msgArg2) {
        if (handler != null) {
            Message msg = new Message();
            msg.what = msgWhat;
            msg.arg1 = msgArg1;
            msg.arg2 = msgArg2;
            handler.sendMessage(msg);
        }
    }

    /**
     * 停止录像
     *
     * @param
     */
    public void stopRecord() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.setOnInfoListener(null);
                mediaRecorder.stop();
                mediaRecorder.release();

                mediaRecorder = null;

                setRecording(false);
                LogUtil.d(TAG, "stopRecord -------> have stoped");
            } catch (IllegalStateException e) {
                e.printStackTrace();
                LogUtil.e(TAG, "stopRecord -------->mediaRecorder stop failed!!!");
            }

            setLocked(false);
            mTimerTask.cancel();
            mTimeCount = 0;
            sendMessage(mHandler, cameraid, 0, 0);
        } else {
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
