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
    private static final int FRONT_CAMERA = 0;
    private static final int BEHIND_CAMERA = 1;
    private static final int ALL_CAMERA = 10086;

    private CameraDev curCameraDev;

    private Button mRecordStartBtn;
    private Button mRecordStopBtn;
    private Button mTakePhotoBtn;
    private Button mRecordSwitchBtn;
    private Button mVideoDirBtn;

    CameraFactory factory = new CameraFactory();

    private DvrSurfaceTextureListener dvrSurfaceTextureFrontListener;
    private DvrSurfaceTextureListener dvrSurfaceTextureBehindListener;

    private Timer timer = new Timer();
    private TimerTask timerTask;
    private TextView timeTv;
    int timeCount = 0;

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (msg.arg1 == 0) {
                        //TODO camera 0 stop
                        Toast.makeText(CameraActivity.this, "停止录制" , Toast.LENGTH_LONG).show();
                        animRec.stop();
                        recImg.setVisibility(View.GONE);
                        timeTv.setVisibility(View.GONE);
                        mRecordStartBtn.setClickable(true);
                        mRecordStopBtn.setClickable(false);

                    }else if (msg.arg1 == 1){
                        //TODO camera 1 start
//                        Toast.makeText(CameraActivity.this, "正在录制" , Toast.LENGTH_LONG).show();
                        recImg.setVisibility(View.VISIBLE);
                        timeTv.setVisibility(View.VISIBLE);
                        mRecordStartBtn.setClickable(false);
                        mRecordStopBtn.setClickable(true);
                        animRec.start();

                        timeTv.setText(DateTimeUtil.formatLongToTimeStr(msg.arg2 * 1000));

                    }
                    break;

                case 1:
                    if (msg.arg1 == 0) {
                        //stop

                    }else if (msg.arg1 == 1){
                        //start

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
            if (mService.getCameraDev(0) == null) {
                //先得到服务，则为制空，待界面起来置入实例；先得到界面，则为置入实例
                LogUtil.d(TAG, "onServiceConnected setCameraDev --------->");
                mService.addCameraDev(dvrSurfaceTextureFrontListener.mCameraId, dvrSurfaceTextureFrontListener.cameraDev);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();

        dvrSurfaceTextureFrontListener = new DvrSurfaceTextureListener(0);

        dvrSurfaceTextureBehindListener = new DvrSurfaceTextureListener(1);

        mCameraTtv.setSurfaceTextureListener(dvrSurfaceTextureFrontListener);
        mCameraFrontTtv.setSurfaceTextureListener(dvrSurfaceTextureBehindListener);

        frontRll.setOnClickListener(this);
        behindRll.setOnClickListener(this);

        mRecordStartBtn.setOnClickListener(this);
        mRecordStopBtn.setOnClickListener(this);
        mTakePhotoBtn.setOnClickListener(this);
        mRecordSwitchBtn.setOnClickListener(this);
        mVideoDirBtn.setOnClickListener(this);

        //先startService再bindService;
        Intent intent = new Intent(CameraActivity.this, RecordService.class);
        startService(intent);
        bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
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
        mTakePhotoBtn = (Button) findViewById(R.id.btn_camera_takephoto);
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

//                dvrSurfaceTextureFrontListener.cameraDev.startRecord();
                mService.getCameraDev(dvrSurfaceTextureFrontListener.mCameraId).startRecord();
                break;

            case R.id.btn_record_stop:
//                dvrSurfaceTextureFrontListener.cameraDev.stopRecord();
//                dvrSurfaceTextureBehindListener.cameraDev.stopRecord();
                mService.getCameraDev(dvrSurfaceTextureFrontListener.mCameraId).stopRecord();
                break;

            case R.id.btn_camera_takephoto:
                //先停止录像
                dvrSurfaceTextureFrontListener.cameraDev.stopRecord();
                dvrSurfaceTextureFrontListener.cameraDev.takePhoto();
                break;


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
        bindService(intent,myServiceConnection, Context.BIND_AUTO_CREATE);
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
    public class DvrSurfaceTextureListener implements TextureView.SurfaceTextureListener{

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
            if (mService != null ){
                //服务还没绑定
                if (mService.isRecording(mCameraId)) {
                    //后台正在录制
                    LogUtil.d(TAG, "onSurfaceTextureAvailable --------> Cmera:" + mCameraId + " is Recording");
                    if (null == mService.getCameraDev(mCameraId) ) {
                        LogUtil.e(TAG, "onSurfaceTextureAvailable ------> cameraDev is null!!!!");
                    }
                    mService.startRender(mCameraId, surface);
                    LogUtil.d(TAG, "onSurfaceTextureAvailable -------> Camera:" + mCameraId + " is recording to starRender");

                    cameraDev = mService.getCameraDev(mCameraId);
                    cameraDev.mHandler = mHandler;  //更新handler
                }else{
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
            }else {
                if (cameraDev.mediaRecorder != null) {
                    //处理back返回卡死问题
//                    cameraStatusListener.onStopRecord();
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
