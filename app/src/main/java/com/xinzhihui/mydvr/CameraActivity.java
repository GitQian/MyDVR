package com.xinzhihui.mydvr;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.xinzhihui.mydvr.listener.CameraStatusListener;
import com.xinzhihui.mydvr.model.CameraDev;
import com.xinzhihui.mydvr.model.CameraFactory;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SDCardUtils;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = this.getClass().getName();

    private TextureView mCameraTtv;
    private TextureView mCameraFrontTtv;

    private ImageView recImg;
    private AnimationDrawable animRec;

    private RelativeLayout frontRll;
    private RelativeLayout behindRll;

    private int curCamera;
    private static final int FRONT_CAMERA = 1;
    private static final int BEHIND_CAMERA = 2;
    private static final int ALL_CAMERA = 3;

    private CameraDev curCameraDev;

    private Button mRecordStartBtn;
    private Button mRecordStopBtn;
    private Button mRecordSwitchBtn;
    private Button mVideoDirBtn;

    CameraFactory factory = new CameraFactory();

    private DvrSurfaceTextureListener dvrSurfaceTextureFrontListener;
    private DvrSurfaceTextureListener dvrSurfaceTextureBehindListener;

    private Timer timer = new Timer();
    private TimerTask timerTask;
    private TextView timeTv;
    int timeCount = 0;
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
                animRec.start();

                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        CameraActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeTv.setText("Time:" + timeCount++);
                            }
                        });
                    }
                };
                timer.schedule(timerTask, 0, 1000);
            }

            @Override
            public void onStopPreview() {

            }

            @Override
            public void onStopRecord() {
                Toast.makeText(CameraActivity.this, "停止录制" , Toast.LENGTH_LONG).show();
                animRec.stop();
                recImg.setVisibility(View.GONE);

                timerTask.cancel();
                timeCount = 0;
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

        frontRll.setOnClickListener(this);
        behindRll.setOnClickListener(this);

        mRecordStartBtn.setOnClickListener(this);
        mRecordStopBtn.setOnClickListener(this);
        mRecordSwitchBtn.setOnClickListener(this);
        mVideoDirBtn.setOnClickListener(this);
    }

    private void initView(){
        mCameraTtv = (TextureView) findViewById(R.id.ttv_camera_front);
        mCameraFrontTtv = (TextureView) findViewById(R.id.ttv_camera_behind);
        recImg = (ImageView) findViewById(R.id.img_record_rec);
        animRec = (AnimationDrawable) recImg.getBackground();

        frontRll = (RelativeLayout) findViewById(R.id.rll_front);
        behindRll = (RelativeLayout) findViewById(R.id.rll_behind);

        mRecordStartBtn = (Button) findViewById(R.id.btn_record_start);
        mRecordStopBtn = (Button) findViewById(R.id.btn_record_stop);
        mRecordSwitchBtn = (Button) findViewById(R.id.btn_record_switch);
        mVideoDirBtn = (Button) findViewById(R.id.btn_video_dir);
        timeTv = (TextView) findViewById(R.id.tv_time);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record_start:
                if(!SDCardUtils.isSDCardEnable()) {
                    LogUtil.i(TAG, "SDCard can't use!");
                    Toast.makeText(CameraActivity.this, "SD卡不可用！", Toast.LENGTH_LONG).show();
                }

                dvrSurfaceTextureFrontListener.cameraDev.startRecord( CamcorderProfile.QUALITY_720P);
                break;

            case R.id.btn_record_stop:
                dvrSurfaceTextureFrontListener.cameraDev.stopRecord();
                break;

//            case R.id.btn_record_behind_start:
////                mediaRecorder2 = new MediaRecorder();
////                dvrSurfaceTextureBehindListener.cameraDev.startRecord(mediaRecorder2, new File(SDCardUtils.getSDCardPath() + "MyFrontVideo.mp4"), 2);
//
//                break;

//            case R.id.btn_record_behind_stop:
////                dvrSurfaceTextureBehindListener.cameraDev.stopRecord(mediaRecorder2);
//                break;

            case R.id.rll_front:
                behindRll.setVisibility(View.GONE);
                frontRll.setVisibility(View.VISIBLE);
                curCamera = FRONT_CAMERA;
                curCameraDev = dvrSurfaceTextureFrontListener.cameraDev;
                break;

            case R.id.rll_behind:
                frontRll.setVisibility(View.GONE);
                behindRll.setVisibility(View.VISIBLE);
                curCamera = BEHIND_CAMERA;
                curCameraDev = dvrSurfaceTextureBehindListener.cameraDev;
                break;

            case R.id.btn_record_switch:
                frontRll.setVisibility(View.VISIBLE);
                behindRll.setVisibility(View.VISIBLE);
                curCamera = ALL_CAMERA;
                break;

            case R.id.btn_video_dir:
                Intent intent = new Intent(CameraActivity.this, FileListActivity.class);
                startActivity(intent);
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
