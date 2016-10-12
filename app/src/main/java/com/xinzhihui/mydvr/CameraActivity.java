package com.xinzhihui.mydvr;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinzhihui.mydvr.model.CameraDev;
import com.xinzhihui.mydvr.model.CameraFactory;
import com.xinzhihui.mydvr.service.RecordService;
import com.xinzhihui.mydvr.utils.DateTimeUtil;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SDCardUtils;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    private TextureView mCameraTtv;
    private TextureView mCameraFrontTtv;

    private ImageView mRecFrontImg;
    private AnimationDrawable mAnimFrontRec;
    private ImageView mRecBehindImg;
    private AnimationDrawable mAnimBehindRec;
    private TextView mTimeFrontTv;
    private TextView mTimetBehindTv;

    private RelativeLayout mFrontRll;
    private RelativeLayout mBehindRll;

    private int mCurCameraId;
    private static final int FRONT_CAMERA = 0;
    private static final int BEHIND_CAMERA = 1;
    private static final int ALL_CAMERA = 10086;

    private CameraDev mCurCameraDev;

    private Button mRecordStartBtn;
    private Button mRecordStopBtn;
    private Button mTakePhotoBtn;
    private Button mRecordSwitchBtn;
    private Button mVideoDirBtn;

    CameraFactory factory = new CameraFactory();

    private DvrSurfaceTextureListener dvrSurfaceTextureFrontListener;
    private DvrSurfaceTextureListener dvrSurfaceTextureBehindListener;

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case FRONT_CAMERA:
                    if (msg.arg1 == 0) {
                        //TODO camera 0 stop
                        Toast.makeText(CameraActivity.this, "停止录制", Toast.LENGTH_LONG).show();
                        mAnimFrontRec.stop();
                        mRecFrontImg.setVisibility(View.GONE);
                        mTimeFrontTv.setVisibility(View.GONE);
                        mRecordStartBtn.setClickable(true);
                        mRecordStopBtn.setClickable(false);

                    } else if (msg.arg1 == 1) {
                        //TODO camera 1 start
//                        Toast.makeText(CameraActivity.this, "正在录制" , Toast.LENGTH_LONG).show();
                        mRecFrontImg.setVisibility(View.VISIBLE);
                        mTimeFrontTv.setVisibility(View.VISIBLE);
                        mRecordStartBtn.setClickable(false);
                        mRecordStopBtn.setClickable(true);
                        mAnimFrontRec.start();

                        mTimeFrontTv.setText(DateTimeUtil.formatLongToTimeStr(msg.arg2 * 1000));

                    }
                    break;

                case BEHIND_CAMERA:
                    if (msg.arg1 == 0) {
                        //stop
                        Toast.makeText(CameraActivity.this, "behind停止录制", Toast.LENGTH_LONG).show();
                        mAnimBehindRec.stop();
                        mRecBehindImg.setVisibility(View.GONE);
                        mTimetBehindTv.setVisibility(View.GONE);
                        mRecordStartBtn.setClickable(true);
                        mRecordStopBtn.setClickable(false);

                    } else if (msg.arg1 == 1) {
                        //start
                        mRecBehindImg.setVisibility(View.VISIBLE);
                        mTimetBehindTv.setVisibility(View.VISIBLE);
                        mRecordStartBtn.setClickable(false);
                        mRecordStopBtn.setClickable(true);
                        mAnimBehindRec.start();

                        mTimetBehindTv.setText(DateTimeUtil.formatLongToTimeStr(msg.arg2 * 1000));
                    }
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    private RecordService mService = null;
    private ServiceConnection myServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "onServiceConnected");
            mService = ((RecordService.LocalBinder) service).getService();
            if (null == mService.getCameraDev(FRONT_CAMERA) && null == mService.getCameraDev(BEHIND_CAMERA)) {
                //TODO 先得到服务，则为制空，待界面起来置入实例；先得到界面，则为置入实例(各路多要判断)
                LogUtil.d(TAG, "onServiceConnected setCameraDev --------->");
                mService.addCameraDev(FRONT_CAMERA, dvrSurfaceTextureFrontListener.cameraDev);
                mService.addCameraDev(BEHIND_CAMERA, dvrSurfaceTextureBehindListener.cameraDev);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();

        dvrSurfaceTextureFrontListener = new DvrSurfaceTextureListener(FRONT_CAMERA);

        dvrSurfaceTextureBehindListener = new DvrSurfaceTextureListener(BEHIND_CAMERA);

        mCameraTtv.setSurfaceTextureListener(dvrSurfaceTextureFrontListener);
        mCameraFrontTtv.setSurfaceTextureListener(dvrSurfaceTextureBehindListener);

        mFrontRll.setOnClickListener(this);
        mBehindRll.setOnClickListener(this);

        mRecordStartBtn.setOnClickListener(this);
        mRecordStopBtn.setOnClickListener(this);
        mTakePhotoBtn.setOnClickListener(this);
        mRecordSwitchBtn.setOnClickListener(this);
        mVideoDirBtn.setOnClickListener(this);

        //先startService再bindService;
        Intent intent = new Intent(CameraActivity.this, RecordService.class);
        startService(intent);
        bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);

        mCurCameraDev = dvrSurfaceTextureFrontListener.cameraDev;
    }

    private void initView() {
        mCameraTtv = (TextureView) findViewById(R.id.ttv_camera_front);
        mCameraFrontTtv = (TextureView) findViewById(R.id.ttv_camera_behind);
        mRecFrontImg = (ImageView) findViewById(R.id.img_front_rec);
        mAnimFrontRec = (AnimationDrawable) mRecFrontImg.getBackground();
        mRecBehindImg = (ImageView) findViewById(R.id.img_behind_rec);
        mAnimBehindRec = (AnimationDrawable) mRecBehindImg.getBackground();

        mFrontRll = (RelativeLayout) findViewById(R.id.rll_front);
        mBehindRll = (RelativeLayout) findViewById(R.id.rll_behind);

        mRecordStartBtn = (Button) findViewById(R.id.btn_record_start);
        mRecordStopBtn = (Button) findViewById(R.id.btn_record_stop);
        mTakePhotoBtn = (Button) findViewById(R.id.btn_camera_takephoto);
        mRecordSwitchBtn = (Button) findViewById(R.id.btn_record_switch);
        mVideoDirBtn = (Button) findViewById(R.id.btn_video_dir);
        mTimeFrontTv = (TextView) findViewById(R.id.tv_front_time);
        mTimetBehindTv = (TextView) findViewById(R.id.tv_behind_time);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record_start:
                if (!SDCardUtils.isSDCardEnable()) {
                    LogUtil.i(TAG, "SDCard can't use!");
                    Toast.makeText(CameraActivity.this, "SD卡不可用！", Toast.LENGTH_LONG).show();
                }

//                dvrSurfaceTextureFrontListener.cameraDev.startRecord();
                mService.getCameraDev(mCurCameraId).startRecord();
                break;

            case R.id.btn_record_stop:
//                dvrSurfaceTextureFrontListener.cameraDev.stopRecord();
//                dvrSurfaceTextureBehindListener.cameraDev.stopRecord();
                mService.getCameraDev(mCurCameraId).stopRecord();
                break;

            case R.id.btn_camera_takephoto:
                //先停止录像
                dvrSurfaceTextureFrontListener.cameraDev.stopRecord();
                dvrSurfaceTextureFrontListener.cameraDev.takePhoto();
                break;


            case R.id.rll_front:
                mBehindRll.setVisibility(View.GONE);
                mFrontRll.setVisibility(View.VISIBLE);
                mCurCameraId = FRONT_CAMERA;
                mCurCameraDev = dvrSurfaceTextureFrontListener.cameraDev;
                break;

            case R.id.rll_behind:
                mFrontRll.setVisibility(View.GONE);
                mBehindRll.setVisibility(View.VISIBLE);
                mCurCameraId = BEHIND_CAMERA;
                mCurCameraDev = dvrSurfaceTextureBehindListener.cameraDev;
                break;

            case R.id.btn_record_switch:
                mFrontRll.setVisibility(View.VISIBLE);
                mBehindRll.setVisibility(View.VISIBLE);
                mCurCameraId = ALL_CAMERA;
                break;

            case R.id.btn_video_dir:
                //先停止录像
//                dvrSurfaceTextureFrontListener.cameraDev.stopRecord();
                Intent intent = new Intent(CameraActivity.this, FileListActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //再次bindService，得到service实例
        Intent intent = new Intent(CameraActivity.this, RecordService.class);
        bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //解除绑定，服务仍在运行(停止服务必须先解除绑定！！！)
        unbindService(myServiceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "CameraActivity onDestroy ------>");
        if (mService != null && !dvrSurfaceTextureFrontListener.cameraDev.isRecording()) {
            //TODO 检查所有设备是否有正在录像的
            Intent intent = new Intent(CameraActivity.this, RecordService.class);
            stopService(intent);
            mService = null;
        }
    }

    /**
     * 内部类，实现SurfaceTextureListener
     */
    public class DvrSurfaceTextureListener implements TextureView.SurfaceTextureListener {

        private final String TAG = this.getClass().getName();
        private int mCameraId;
        //        private CameraStatusListener cameraStatusListener;
        public CameraDev cameraDev;

        public DvrSurfaceTextureListener(int cameraId) {
            mCameraId = cameraId;
//            this.cameraStatusListener = cameraStatusListener;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            LogUtil.d(TAG, "onSurfaceTextureAvailable ------->");
            if (mService != null) {
                //服务还没绑定
                if (mService.isRecording(mCameraId)) {
                    //后台正在录制
                    LogUtil.d(TAG, "onSurfaceTextureAvailable --------> Cmera:" + mCameraId + " is Recording");
                    if (null == mService.getCameraDev(mCameraId)) {
                        LogUtil.e(TAG, "onSurfaceTextureAvailable ------> cameraDev is null!!!!");
                    }
                    mService.startRender(mCameraId, surface);
                    LogUtil.d(TAG, "onSurfaceTextureAvailable -------> Camera:" + mCameraId + " is recording to starRender");

                    cameraDev = mService.getCameraDev(mCameraId);
                    cameraDev.mHandler = mHandler;  //更新handler
                } else {
                    //后台没有录制（进入之后不录制...再次进入）（绑定服务在先，就会进入这）------第一次进入情况2（绑定在先）
                    LogUtil.d(TAG, "onSurfaceTextureAvailable --------> mService not Recording");
                    cameraDev = factory.createCameraDev(mCameraId);
                    cameraDev.open();
                    cameraDev.startPreview(surface);

                    mService.addCameraDev(mCameraId, cameraDev);  //service和cameraDev关联
                    cameraDev.mHandler = mHandler;   //更新设置handler
                }
            } else {
                //第一次进入(与绑定服务有同步问题，可能会进入)----第一次进入情况1（绑定在后）
                LogUtil.d(TAG, "onSurfaceTextureAvailable --------> mService is null");
                cameraDev = factory.createCameraDev(mCameraId);
                cameraDev.open();
                cameraDev.startPreview(surface);

                cameraDev.mHandler = mHandler;     //设置handler
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            LogUtil.d(TAG, "onSurfaceTextureDestroyed --------->");

            if (mService.isRecording(mCameraId)) {
                LogUtil.d(TAG, "onSurfaceTextureDestroyed ---------> Camera:" + mCameraId + " is Recording to stopRender");
                mService.stopRender(mCameraId);
            } else {
                if (cameraDev.mediaRecorder != null) {
                    //处理back返回卡死问题
//                    cameraStatusListener.onStopRecord();
                    //stopRecord后 mediaRecorder 置空了
                    cameraDev.killRecord();
                }
                cameraDev.stopPreview();
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }
}
