package com.xinzhihui.mydvr.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.xinzhihui.mydvr.model.CameraDev;
import com.xinzhihui.mydvr.utils.LogUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RecordService extends Service {
    private final String TAG = getClass().getName();

    private List<CameraDev> cameraDevList;

    private final IBinder mBinder = new LocalBinder();

    public RecordService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cameraDevList = new ArrayList<CameraDev>();
        cameraDevList.add(null);
        cameraDevList.add(null);
        LogUtil.d(TAG, "RecordService onCreate --------->");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "RecordService onStartCommand --------->");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        LogUtil.d(TAG, "RecordService onBind --------->");
        return mBinder;
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "RecordService onUnbind --------->");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "RecordService onDestroy --------->");
    }


    public class LocalBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }
    }


    public void open(int cameraId) {
        cameraDevList.get(cameraId).open();
    }

    public void startPreView(int cameraId, SurfaceTexture surface) {
        cameraDevList.get(cameraId).startPreview(surface);
    }

    public void stopPreView(int cameraId) {
        cameraDevList.get(cameraId).stopPreview();
    }

    public void startRecord(int cameraId) {
        cameraDevList.get(cameraId).startRecord();
    }

    public void stopRecord(int cameraId) {
        cameraDevList.get(cameraId).stopRecord();
    }

    public void startRender(int cameraId, SurfaceTexture surface) {
        try {
            CameraDev cameraDev = cameraDevList.get(cameraId);
            cameraDev.camera.setPreviewTexture(surface);
            //TODO 反射调用startRender
            Class<?> c = cameraDev.camera.getClass();
            Method startRender = c.getMethod("startRender");
            startRender.invoke(cameraDev.camera);
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

    public void stopRender(int cameraId) {
        CameraDev cameraDev = cameraDevList.get(cameraId);
        //TODO 反射调用stopRender
        Class<?> c = cameraDev.camera.getClass();
        Method startRender = null;
        try {
            startRender = c.getMethod("stopRender");
            startRender.invoke(cameraDev.camera);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void killRecord(int cameraId) {
        cameraDevList.get(cameraId).killRecord();
    }

    public void takePhoto(int cameraId) {
        cameraDevList.get(cameraId).takePhoto();
    }

    public void stopAllRecord() {
        for (CameraDev cameraDev : cameraDevList) {
            if (cameraDev != null) {
                if (cameraDev.isRecording()) {
                    cameraDev.stopRecord();
                }
            }
        }
    }

    public CameraDev getCameraDev(int cameraId) {
        if (cameraDevList == null) {
            return null;
        }
        return cameraDevList.get(cameraId);
    }

    public void addCameraDev(int cameraId, CameraDev cameraDev) {
//        cameraDevList.add(cameraDev);
        cameraDevList.set(cameraId, cameraDev);
    }

    public boolean isRecording(int cameraId) {
        if (cameraDevList.get(cameraId) == null) {
            return false;
        } else {
            return cameraDevList.get(cameraId).isRecording();
        }
    }


}
