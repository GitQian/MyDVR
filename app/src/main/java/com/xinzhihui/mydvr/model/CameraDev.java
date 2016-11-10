package com.xinzhihui.mydvr.model;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.asynctask.DeleteFileTask;
import com.xinzhihui.mydvr.asynctask.SavePictureTask;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SDCardUtils;
import com.xinzhihui.mydvr.utils.SPUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/9/28.
 */
public abstract class CameraDev {
    private final String TAG = getClass().getName();

    public int cameraIndexId;
    public int cameraId;

    public Camera camera = null;
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
            camera = Camera.open(cameraIndexId);
            LogUtil.d(TAG, "open -------> cameraId:" + cameraIndexId);
        } catch (Exception e) {
            LogUtil.e(TAG, "open -------> cameraId:" + cameraIndexId + "error!!!!");
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
                if (cameraIndexId == AppConfig.FRONT_CAMERA_INDEX) {
                    if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_FRONT_WATER, true)) {
                        Class<?> c = camera.getClass();
                        Method startWaterMark = c.getMethod("startWaterMark");
                        startWaterMark.invoke(camera);
                        setPreviewing(true);
                    }
                } else if (cameraIndexId == AppConfig.BEHIND_CAMERA_INDEX) {
                    if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_BEHIND_WATER, true)) {
                        Class<?> c = camera.getClass();
                        Method startWaterMark = c.getMethod("startWaterMark");
                        startWaterMark.invoke(camera);
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
            LogUtil.e(TAG, "stopPreView ----------->camera:" + cameraIndexId + "is null");
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
        //TODO 漏秒情况下，可统一在startRecord处检测存储空间是否充足---低于30M触发
        if (SDCardUtils.getFreeBytes(AppConfig.DVR_PATH) < 300 * 1024 * 1024) {
            new DeleteFileTask().execute(new String[]{AppConfig.FRONT_VIDEO_PATH, AppConfig.BEHIND_VIDEO_PATH});
        } else {
            LogUtil.d(TAG, "Free storge enough! Size byte:" + SDCardUtils.getFreeBytes(AppConfig.DVR_PATH));
        }

        mVideoFile = makeFile();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //TODO android 6.0
            Class<?> c = null;
            try {
                c = Class.forName("android.media.MediaRecorder");
                Constructor<?> con = c.getConstructor(int.class);
                mediaRecorder = (MediaRecorder) con.newInstance(1);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "new MediaRecorder(int type) error!!!");
            }

        } else {
            //android 4.4
            mediaRecorder = new MediaRecorder();
        }

        camera.unlock();

        initRecorderParameters(camera, mediaRecorder, mVideoFile);

        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                switch (what) {
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Class<?> c = mediaRecorder.getClass();
                                Method setNextSaveFile = null;
                                try {
                                    setNextSaveFile = c.getMethod("setNextSaveFile", String.class);
                                    setNextSaveFile.invoke(mediaRecorder,makeFile().getAbsolutePath());

                                    setRecording(true);
                                    setLocked(false);
                                    sendMessage(mHandler, cameraId, 0, 0);  //stop
                                    sendMessage(mHandler, cameraId, 1, 0);  //start
                                    mTimeCount = 1;

                                    //TODO 漏秒情况下，可统一在startRecord处检测存储空间是否充足---低于30M触发
                                    if (SDCardUtils.getFreeBytes(AppConfig.DVR_PATH) < 300 * 1024 * 1024) {
                                        new DeleteFileTask().execute(new String[]{AppConfig.FRONT_VIDEO_PATH, AppConfig.BEHIND_VIDEO_PATH});
                                    } else {
                                        LogUtil.d(TAG, "Free storge enough! Size byte:" + SDCardUtils.getFreeBytes(AppConfig.DVR_PATH));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LogUtil.e("qiansheng", "setNextSaveFile Error!!!");
                                }
                            }
                        }).start();
////                        LogUtil.e("qiansheng", "OnInfo thread id:" + Thread.currentThread().getId());
//                        stopRecord();
//                        startRecord();
                        break;
                    default:
                        break;
                }
            }
        });

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            LogUtil.e(TAG, "startRecord ------> cameraDev:" + cameraIndexId + " " + "startRecord!!!");
        } catch (IOException e) {
            LogUtil.e(TAG, "startRecord ------> cameraDev:" + cameraIndexId + " " + "startRecord error!!!");
            e.printStackTrace();
            return;
        }


        setRecording(true);

        sendMessage(mHandler, cameraId, 1, 0);
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
//                LogUtil.e("qiansheng", "TimerTask thread id:" + Thread.currentThread().getId());
                sendMessage(mHandler, cameraId, 2, mTimeCount);

                if (mTimeCount >= 60) {
                    mTimeCount = 0;
                    sendMessage(mHandler, cameraId, 2, mTimeCount);
                    LogUtil.e("qiansheng", "Runnable thread id:" + Thread.currentThread().getId());
                    Class<?> c = mediaRecorder.getClass();
                    Method setNextSaveFile = null;
                    try {
                        setNextSaveFile = c.getMethod("setNextSaveFile", String.class);
                        setNextSaveFile.invoke(mediaRecorder,makeFile().getAbsolutePath());

//                      mTimerTask.cancel();
//                      mTimeCount = 0;
//                      endMessage(mHandler, cameraId, 2, mTimeCount);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e("qiansheng", "setNextSaveFile Error!!!");
                    }
                }else {
                    sendMessage(mHandler, cameraId, 2, mTimeCount);
                }
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
            mTimeCount = 1;
            sendMessage(mHandler, cameraId, 0, 0);
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
