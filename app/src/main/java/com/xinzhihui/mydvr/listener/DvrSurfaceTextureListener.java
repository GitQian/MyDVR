//package com.xinzhihui.mydvr.listener;
//
//import android.graphics.SurfaceTexture;
//import android.hardware.Camera;
//import android.view.TextureView;
//
//import com.xinzhihui.mydvr.utils.LogUtil;
//
//import java.io.IOException;
//
///**
// * Created by qiansheng on 2016/9/28.
// */
//public class DvrSurfaceTextureListener implements TextureView.SurfaceTextureListener{
//
//    private final String TAG = this.getClass().getName();
//    private int mCameraId;
//    public static Camera mCamera;
//    public DvrSurfaceTextureListener(int cameraId) {
//        mCameraId = cameraId;
//    }
//    @Override
//    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//        try {
//            mCamera = Camera.open(mCameraId);
//            mCamera.setPreviewTexture(surface);
//            mCamera.startPreview();
//        } catch (IOException e) {
//            e.printStackTrace();
//            LogUtil.e(TAG, "Failed to use Camera:" + mCameraId);
//        }
//    }
//
//    @Override
//    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//
//    }
//
//    @Override
//    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        mCamera.stopPreview();
//        mCamera.release();
//        return true;
//    }
//
//    @Override
//    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//
//    }
//}
