package com.xinzhihui.mydvr;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinzhihui.mydvr.Receiver.UsbStateReceiver;
import com.xinzhihui.mydvr.db.LockVideoDAL;
import com.xinzhihui.mydvr.model.CameraDev;
import com.xinzhihui.mydvr.model.CameraFactory;
import com.xinzhihui.mydvr.service.RecordService;
import com.xinzhihui.mydvr.utils.DateTimeUtil;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SDCardUtils;
import com.xinzhihui.mydvr.utils.SPUtils;

import java.io.File;
import java.lang.ref.WeakReference;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    private TextureView mCameraTtv;
    private TextureView mCameraFrontTtv;

    private ImageView mRecFrontImg;
    private AnimationDrawable mAnimFrontRec;
    private ImageView mRecBehindImg;
    private AnimationDrawable mAnimBehindRec;
    private TextView mTimeFrontTv;
    private TextView mTimeBehindTv;

    private RelativeLayout mFrontRll;
    private RelativeLayout mBehindRll;

    private int mCurCameraId;

    private static final int ALL_CAMERA = 10086;

    private CameraDev mCurCameraDev;

    private LinearLayout mRecordCtrlLly;
    private Button mTakePhotoBtn;
    private Button mRecordSwitchBtn;
    private Button mVideoDirBtn;
    private Button mRecordCtrlBtn;
    private Button mRecordSettingBtn;
    private Button mRecordFileLockBtn;

    CameraFactory factory = new CameraFactory();

    private DvrSurfaceTextureListener dvrSurfaceTextureFrontListener;
    private DvrSurfaceTextureListener dvrSurfaceTextureBehindListener;

    private boolean isFrontAuto;
    private boolean isBehindAuto;

    private UsbStateReceiver usbStateReceiver;

    Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<CameraActivity> mActivity;

        public MyHandler(CameraActivity activity) {
            mActivity = new WeakReference<CameraActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity.get() == null) {
                return;
            }
            switch (msg.what) {
                case AppConfig.FRONT_CAMERA:
                    if (msg.arg1 == 0) {
                        //TODO camera 0 stop
                        Toast.makeText(MyApplication.getContext(), "停止录制", Toast.LENGTH_LONG).show();

                        mActivity.get().mAnimFrontRec.stop();
                        mActivity.get().mRecFrontImg.setVisibility(View.GONE);
                        mActivity.get().mTimeFrontTv.setVisibility(View.GONE);
                        if (mActivity.get().mFrontRll.getVisibility() == View.VISIBLE) {
                            //如果还处于前置摄像头界面，则更新（否则通过点击具体摄像头界面更新）
                            mActivity.get().mRecordFileLockBtn.setBackgroundResource(R.drawable.btn_record_lock_off);
                            mActivity.get().mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_closed);
                        }
//                        mRecordStartBtn.setClickable(true);
//                        mRecordStopBtn.setClickable(false);

                    } else if (msg.arg1 == 1) {
                        //TODO camera 1 start
//                        Toast.makeText(CameraActivity.this, "正在录制" , Toast.LENGTH_LONG).show();
                        mActivity.get().mRecFrontImg.setVisibility(View.VISIBLE);
                        mActivity.get().mTimeFrontTv.setVisibility(View.VISIBLE);
//                        mRecordStartBtn.setClickable(false);
//                        mRecordStopBtn.setClickable(true);
                        //TODO 这里是有问题的！多次启动动画（内存）
                        mActivity.get().mAnimFrontRec.start();
                        mActivity.get().mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_started);

                    } else if (msg.arg1 == 2) {
                        //TODO Time Update
                        mActivity.get().mTimeFrontTv.setText(DateTimeUtil.formatLongToTimeStr(msg.arg2 * 1000));
                    }
                    break;

                case AppConfig.BEHIND_CAMERA:
                    if (msg.arg1 == 0) {
                        //stop
                        Toast.makeText(MyApplication.getContext(), "behind停止录制", Toast.LENGTH_LONG).show();
                        mActivity.get().mAnimBehindRec.stop();
                        mActivity.get().mRecBehindImg.setVisibility(View.GONE);
                        mActivity.get().mTimeBehindTv.setVisibility(View.GONE);
                        if (mActivity.get().mBehindRll.getVisibility() == View.VISIBLE) {
                            //如果还处于前置摄像头界面，则更新（否则通过点击具体摄像头界面更新）
                            mActivity.get().mRecordFileLockBtn.setBackgroundResource(R.drawable.btn_record_lock_off);
                            mActivity.get().mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_closed);
                        }
//                        mRecordStartBtn.setClickable(true);
//                        mRecordStopBtn.setClickable(false);

                    } else if (msg.arg1 == 1) {
                        //start
                        mActivity.get().mRecBehindImg.setVisibility(View.VISIBLE);
                        mActivity.get().mTimeBehindTv.setVisibility(View.VISIBLE);
//                        mRecordStartBtn.setClickable(false);
//                        mRecordStopBtn.setClickable(true);
                        mActivity.get().mAnimBehindRec.start();

                        mActivity.get().mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_started);
                    } else if (msg.arg1 == 2) {
                        //update time
                        mActivity.get().mTimeBehindTv.setText(DateTimeUtil.formatLongToTimeStr(msg.arg2 * 1000));
                    }
                    break;

                default:
                    break;
            }
        }
    }

    public RecordService mService = null;
    private ServiceConnection myServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "onServiceConnected ------->");
            mService = ((RecordService.LocalBinder) service).getService();
            if (null == mService.getCameraDev(AppConfig.FRONT_CAMERA) && null == mService.getCameraDev(AppConfig.BEHIND_CAMERA)) {
                //TODO 先得到服务，则为制空，待界面起来置入实例；先得到界面，则为置入实例(各路多要判断)
                LogUtil.d(TAG, "onServiceConnected ---------> addCameraDev");
                mService.addCameraDev(AppConfig.FRONT_CAMERA, dvrSurfaceTextureFrontListener.cameraDev);
                mService.addCameraDev(AppConfig.BEHIND_CAMERA, dvrSurfaceTextureBehindListener.cameraDev);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();

        isFrontAuto = (Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_FRONT_AUTO, true);
        isBehindAuto = (Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_BEHIND_AUTO, false);

        dvrSurfaceTextureFrontListener = new DvrSurfaceTextureListener(AppConfig.FRONT_CAMERA);

        dvrSurfaceTextureBehindListener = new DvrSurfaceTextureListener(AppConfig.BEHIND_CAMERA);

        mCameraTtv.setSurfaceTextureListener(dvrSurfaceTextureFrontListener);
        mCameraFrontTtv.setSurfaceTextureListener(dvrSurfaceTextureBehindListener);

        mFrontRll.setOnClickListener(this);
        mBehindRll.setOnClickListener(this);

        mRecordCtrlLly.setVisibility(View.GONE);
        mTakePhotoBtn.setOnClickListener(this);
        mRecordSwitchBtn.setOnClickListener(this);
        mVideoDirBtn.setOnClickListener(this);
        mRecordCtrlBtn.setOnClickListener(this);
        mRecordSettingBtn.setOnClickListener(this);
        mRecordFileLockBtn.setOnClickListener(this);

        //先startService再bindService;
        Intent intent = new Intent(CameraActivity.this, RecordService.class);
        startService(intent);
        bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);

        mCurCameraDev = dvrSurfaceTextureFrontListener.cameraDev;  //当做初始化

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");
        usbStateReceiver = new UsbStateReceiver();
        registerReceiver(usbStateReceiver, filter);
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

        mRecordCtrlLly = (LinearLayout) findViewById(R.id.lly_record_ctrl);
        mTakePhotoBtn = (Button) findViewById(R.id.btn_camera_takephoto);
        mRecordSwitchBtn = (Button) findViewById(R.id.btn_record_switch);
        mVideoDirBtn = (Button) findViewById(R.id.btn_video_dir);
        mRecordCtrlBtn = (Button) findViewById(R.id.btn_record_ctrl);
        mRecordSettingBtn = (Button) findViewById(R.id.btn_record_setting);
        mRecordFileLockBtn = (Button) findViewById(R.id.btn_record_lock);

        mTimeFrontTv = (TextView) findViewById(R.id.tv_front_time);
        mTimeBehindTv = (TextView) findViewById(R.id.tv_behind_time);

//        mCurCameraDev = dvrSurfaceTextureFrontListener.cameraDev;  //当做初始化
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record_ctrl:
                if (mService.getCameraDev(mCurCameraId).isRecording()) {
                    //通过mService获取到的是已经更新过的，安全！
                    mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_closed);
//                    mCurCameraDev.stopRecord();
                    mService.stopRecord(mCurCameraId);
                } else {
                    //通过mService获取到的当前CameraDev是最新的（已更新过），mCurCameraDev没有及时更新
                    if (mService.startRecord(mCurCameraId)) {
                        mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_started);
                    }
                }
                break;

            case R.id.btn_camera_takephoto:
                mService.takePhoto(mCurCameraId);
                break;


            case R.id.rll_front:
                mBehindRll.setVisibility(View.GONE);
                mFrontRll.setVisibility(View.VISIBLE);
                mRecordCtrlLly.setVisibility(View.VISIBLE);
                mCurCameraId = AppConfig.FRONT_CAMERA;
                mCurCameraDev = dvrSurfaceTextureFrontListener.cameraDev;
                updateRecordCtrlBtn(mCurCameraDev);
                break;

            case R.id.rll_behind:
                mFrontRll.setVisibility(View.GONE);
                mBehindRll.setVisibility(View.VISIBLE);
                mRecordCtrlLly.setVisibility(View.VISIBLE);
                mCurCameraId = AppConfig.BEHIND_CAMERA;
                mCurCameraDev = dvrSurfaceTextureBehindListener.cameraDev;
                updateRecordCtrlBtn(mCurCameraDev);
                break;

            case R.id.btn_record_switch:
                mFrontRll.setVisibility(View.VISIBLE);
                mBehindRll.setVisibility(View.VISIBLE);
                mRecordCtrlLly.setVisibility(View.GONE);
                mCurCameraId = ALL_CAMERA;
                break;

            case R.id.btn_record_lock:
                if (mService.getCameraDev(mCurCameraId).isRecording()) {
                    String path = mService.getCameraDev(mCurCameraId).getmVideoFile().getAbsolutePath();
                    if (mService.getCameraDev(mCurCameraId).isLocked()) {
                        //处于锁定状态
                        Toast.makeText(CameraActivity.this, "解锁", Toast.LENGTH_SHORT).show();
                        LockVideoDAL lockVideoDAL = new LockVideoDAL(MyApplication.getContext());
                        lockVideoDAL.deleteLockVideo(path);
                        mRecordFileLockBtn.setBackgroundResource(R.drawable.selector_record_lock_off);
                        mService.getCameraDev(mCurCameraId).setLocked(false);
                    } else {
                        //处于未锁定状态
                        Toast.makeText(CameraActivity.this, "上锁", Toast.LENGTH_SHORT).show();
                        LockVideoDAL lockVideoDAL = new LockVideoDAL(MyApplication.getContext());
                        lockVideoDAL.addLockVideo(path);
                        mRecordFileLockBtn.setBackgroundResource(R.drawable.selector_record_lock_on);
                        mService.getCameraDev(mCurCameraId).setLocked(true);
                    }
                } else {
                    Toast.makeText(CameraActivity.this, "不在录制状态！", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_video_dir:
                //先停止录像
//                dvrSurfaceTextureFrontListener.cameraDev.stopRecord();
                if (!SDCardUtils.isPathEnable(AppConfig.DVR_PATH)) {
                    Toast.makeText(CameraActivity.this, "存储路径不存在！", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(CameraActivity.this, FileList2Activity.class);
                startActivity(intent);
                break;

            case R.id.btn_record_setting:
                Dialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("进入设置将会停止当前录像！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mService.stopAllRecord();
                                Intent settingIntent = new Intent(CameraActivity.this, Setting2Activity.class);
                                startActivity(settingIntent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                alertDialog.show();

                break;

            default:
                break;
        }
    }

    private void updateRecordCtrlBtn(CameraDev cameraDev) {
        if (cameraDev.isRecording()) {
            mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_started);
            //TODO 判断该文件是否被标记 锁定
            if (cameraDev.isLocked()) {
                mRecordFileLockBtn.setBackgroundResource(R.drawable.selector_record_lock_on);
            } else {
                mRecordFileLockBtn.setBackgroundResource(R.drawable.selector_record_lock_off);
            }
        } else {
            mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_closed);
            mRecordFileLockBtn.setBackgroundResource(R.drawable.selector_record_lock_off);
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
        isFrontAuto = false;  //避免其他界面返回时开始录像
        isBehindAuto = false;
        super.onPause();
        //解除绑定，服务仍在运行(停止服务必须先解除绑定！！！)
        unbindService(myServiceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "CameraActivity onDestroy ------>");
        if (mService != null && !mService.isRecording()) {
            //TODO 检查所有设备是否有正在录像的
            Intent intent = new Intent(CameraActivity.this, RecordService.class);
            stopService(intent);
            mService = null;
        }

        unregisterReceiver(usbStateReceiver);
        mHandler.removeCallbacksAndMessages(null);

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
//                    if (mCameraId == AppConfig.FRONT_CAMERA && isFrontAuto) {
//                        cameraDev.startRecord();  //自启动录像
//                    } else if (mCameraId == AppConfig.BEHIND_CAMERA && isBehindAuto) {
//                        cameraDev.startRecord();
//                    }
                }
            } else {
                //第一次进入(与绑定服务有同步问题，可能会进入)----第一次进入情况1（绑定在后）
                LogUtil.d(TAG, "onSurfaceTextureAvailable --------> mService is null");
                cameraDev = factory.createCameraDev(mCameraId);
                cameraDev.open();
                cameraDev.startPreview(surface);

                cameraDev.mHandler = mHandler;     //设置handler
//                if (mCameraId == AppConfig.FRONT_CAMERA && isFrontAuto) {
//                    cameraDev.startRecord();  //自启动录像
//                } else if (mCameraId == AppConfig.BEHIND_CAMERA && isBehindAuto) {
//                    cameraDev.startRecord();
//                }
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            LogUtil.d(TAG, "onSurfaceTextureDestroyed --------->");
            if (mService == null) {
                //activity onDestroy先执行
                cameraDev.killRecord();
                cameraDev.stopPreview();
                return true;
            }

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

    private boolean isStorageEnough() {
        long allSize = SDCardUtils.getFolderSize(new File(AppConfig.DVR_PATH)) + SDCardUtils.getFreeBytes(AppConfig.ROOT_DIR);
        //给DVR预留400M，否则不让录制
        if (allSize < 400 * 1024 * 1024) {
            LogUtil.d("qiansheng", "DVR can use size:" + allSize);
            return false;
        } else {
            LogUtil.d("qiansheng", "DVR can use size:" + allSize);
            return true;
        }
    }
}
