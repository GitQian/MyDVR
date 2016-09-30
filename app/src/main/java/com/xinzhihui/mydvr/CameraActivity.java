package com.xinzhihui.mydvr;

import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xinzhihui.mydvr.listener.CameraStatusListener;
import com.xinzhihui.mydvr.model.CameraDev;
import com.xinzhihui.mydvr.model.CameraFactory;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SDCardUtils;

import java.io.File;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = this.getClass().getName();

    private TextureView mCameraTtv;
    private TextureView mCameraFrontTtv;

    private Button mRecordFrontStartBtn;
    private Button mRecordFrontStopBtn;
    private ImageView recImg;
    private RelativeLayout frontRll;
    private RelativeLayout behindRll;
    private Button mSwitchBtn;

    private int curCamera;
    private static final int FRONT_CAMERA = 1;
    private static final int BEHIND_CAMERA = 2;

    CameraFactory factory = new CameraFactory();

    private DvrSurfaceTextureListener dvrSurfaceTextureFrontListener;
    private DvrSurfaceTextureListener dvrSurfaceTextureBehindListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();

        dvrSurfaceTextureFrontListener = new DvrSurfaceTextureListener(0, new CameraStatusListener() {
            @Override
            public void onStartPreview() {
                Toast.makeText(CameraActivity.this, "正在实时预览" , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStartRecord() {
                Toast.makeText(CameraActivity.this, "正在录制" , Toast.LENGTH_LONG).show();
                recImg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopPreview() {

            }

            @Override
            public void onStopRecord() {
                Toast.makeText(CameraActivity.this, "停止录制" , Toast.LENGTH_LONG).show();
                recImg.setVisibility(View.GONE);
            }
        });

        dvrSurfaceTextureBehindListener = new DvrSurfaceTextureListener(1, new CameraStatusListener() {
            @Override
            public void onStartPreview() {

            }

            @Override
            public void onStartRecord() {

            }

            @Override
            public void onStopPreview() {

            }

            @Override
            public void onStopRecord() {

            }
        });

        mCameraTtv.setSurfaceTextureListener(dvrSurfaceTextureFrontListener);
        mCameraFrontTtv.setSurfaceTextureListener(dvrSurfaceTextureBehindListener);

        mRecordFrontStartBtn.setOnClickListener(this);
        mRecordFrontStopBtn.setOnClickListener(this);

        frontRll.setOnClickListener(this);
        mSwitchBtn.setOnClickListener(this);
    }

    private void initView(){
        mCameraTtv = (TextureView) findViewById(R.id.ttv_camera_front);
        mCameraFrontTtv = (TextureView) findViewById(R.id.ttv_camera_behind);
        mRecordFrontStartBtn = (Button) findViewById(R.id.btn_record_front_start);
        mRecordFrontStopBtn = (Button) findViewById(R.id.btn_record_front_stop);
        recImg = (ImageView) findViewById(R.id.img_record_rec);
        frontRll = (RelativeLayout) findViewById(R.id.rll_front);
        behindRll = (RelativeLayout) findViewById(R.id.rll_behind);
        mSwitchBtn = (Button) findViewById(R.id.btn_switch_shuanglu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record_front_start:
                if(!SDCardUtils.isSDCardEnable()) {
                    LogUtil.i(TAG, "SDCard can't use!");
                    Toast.makeText(CameraActivity.this, "SD卡不可用！", Toast.LENGTH_LONG).show();
                }

                dvrSurfaceTextureFrontListener.cameraDev.startRecord( CamcorderProfile.QUALITY_720P);
                break;

            case R.id.btn_record_front_stop:
                dvrSurfaceTextureFrontListener.cameraDev.stopRecord();
                break;

            case R.id.btn_record_behind_start:
//                mediaRecorder2 = new MediaRecorder();
//                dvrSurfaceTextureBehindListener.cameraDev.startRecord(mediaRecorder2, new File(SDCardUtils.getSDCardPath() + "MyFrontVideo.mp4"), 2);

                break;

            case R.id.btn_record_behind_stop:
//                dvrSurfaceTextureBehindListener.cameraDev.stopRecord(mediaRecorder2);
                break;

            case R.id.rll_front:
                behindRll.setVisibility(View.GONE);
                break;

            case R.id.btn_switch_shuanglu:
                behindRll.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    /**
     * 内部类，实现SurfaceTextureListener
     */
    public class DvrSurfaceTextureListener implements TextureView.SurfaceTextureListener{

        private final String TAG = this.getClass().getName();
        private int mCameraId;
        private CameraStatusListener cameraStatusListener;
        public CameraDev cameraDev;

        public DvrSurfaceTextureListener(int cameraId, CameraStatusListener cameraStatusListener) {
            mCameraId = cameraId;
            this.cameraStatusListener = cameraStatusListener;
        }
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                 cameraDev = factory.createCameraDev(mCameraId, cameraStatusListener);
                cameraDev.open();
                cameraDev.startPreview(surface);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            cameraDev.stopPreview();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }
}
