package com.xinzhihui.mydvr;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xinzhihui.mydvr.listener.CameraStatusListener;
import com.xinzhihui.mydvr.model.CameraDev;
import com.xinzhihui.mydvr.model.CameraFactory;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SDCardUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = this.getClass().getName();
    private int[] arrays = new int[4];

    private TextureView mCameraTtv;
    public  Camera mCamera;

    private Button mRecordBtn;
    private Button mRecordStopBtn;

    private File videoFile;
    private MediaRecorder mediaRecorder;

    CameraFactory factory = new CameraFactory();
    CameraDev cameraDev;

    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
//                    Toast.makeText(CameraActivity.this, "正在录制" + cameraDev.isRecording(), Toast.LENGTH_LONG).show();
                    break;

                case 2:

                    break;
                default:
                    break;
            }
            return false;
        }
    });;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCameraTtv = (TextureView) findViewById(R.id.ttv_camera);
        mCameraTtv.setSurfaceTextureListener(new DvrSurfaceTextureListener(0));

        mRecordBtn = (Button) findViewById(R.id.btn_record);
        mRecordBtn.setOnClickListener(this);

        mRecordStopBtn = (Button) findViewById(R.id.btn_record_stop);
        mRecordStopBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record:
                if(!SDCardUtils.isSDCardEnable()) {
                    LogUtil.i(TAG, "SDCard can't use!");
                    Toast.makeText(CameraActivity.this, "SD卡不可用！", Toast.LENGTH_LONG).show();
                }

                mediaRecorder = new MediaRecorder();
                videoFile = new File(SDCardUtils.getSDCardPath() + "MyVideo.mp4");

//                startRecord(mCamera, mediaRecorder, videoFile);

                cameraDev.startRecord(mediaRecorder, videoFile);

                break;

            case R.id.btn_record_stop:
//                stopRecord(mediaRecorder);
                cameraDev.stopRecord(mediaRecorder);
                break;

            default:
                break;
        }
    }

    public void startRecord(Camera camera, MediaRecorder mediaRecorder, File file){
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
    }

    public void stopRecord(MediaRecorder mediaRecorder){
        mediaRecorder.stop();
        mediaRecorder.release();
    }

    private void initCamera(Camera camera) {
        if (camera == null) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        //请通过parameters.getSupportedPreviewSizes();设置预览大小,否则设置了一个摄像头不支持大小,将会报错.
        parameters.setPreviewSize(1920, 1080);//如果设置了一个不支持的大小,会崩溃.坑2

        //请通过parameters.getSupportedPictureSizes();设置拍照图片大小,这一步对于录像来说是非必须的.
        //parameters.setPictureSize(1920, 1080);
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            //设置对焦模式
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        camera.setParameters(parameters);
    }

    /**
     * 内部类，实现SurfaceTextureListener
     */
    public class DvrSurfaceTextureListener implements TextureView.SurfaceTextureListener{

        private final String TAG = this.getClass().getName();
        private int mCameraId;

        public DvrSurfaceTextureListener(int cameraId) {
            mCameraId = cameraId;
        }
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//            try {
//                factory = new CameraFactory();
                 cameraDev = factory.createCameraDev(mCameraId, new CameraStatusListener() {
                     @Override
                     public void onStartPreview() {

                     }

                     @Override
                     public void onStartRecord() {
                         Toast.makeText(CameraActivity.this, "正在录制!!!" + cameraDev.isRecording(), Toast.LENGTH_LONG).show();
                     }

                     @Override
                     public void onStopPreview() {

                     }

                     @Override
                     public void onStopRecord() {

                     }
                 });
                cameraDev.open(mHandler);
                cameraDev.startPreview(surface);


//                mCamera = Camera.open(mCameraId);
////                initCamera(mCamera);
//                mCamera.setPreviewTexture(surface);
//                mCamera.startPreview();
//            } catch (IOException e) {
//                e.printStackTrace();
//                LogUtil.e(TAG, "Failed to use Camera:" + mCameraId);
//            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//            mCamera.stopPreview();
//            mCamera.release();
            cameraDev.stopPreview();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }
}
