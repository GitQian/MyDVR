package com.xinzhihui.mydvr;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.xinzhihui.mydvr.utils.DateTimeUtil;
import com.xinzhihui.mydvr.utils.StringUtils;

public class VideoPlayerActivity extends Activity implements OnClickListener {

    public static final String ACTION_EXTRA_PLAY_LIST = "extra_play_list";
    public static final String ACTION_EXTRA_PLAY_INDEX = "extra_play_index";

    private static final int MSG_UPDATE_PROGRESS = 1;
    private static final int MSG_HIDE_CONTROLLER = 2;

    private static final int HIDE_TIME = 5000;

    private VideoView mVideoView;

    private List<String> mPlayList;
    private int mPlayIndex;
    private Context mContext;
    private int mPositionWhenPaused = -1;

    private View mTitleLayout;
    private TextView mVideoName;

    private View mControllerLayout;
    private TextView mCurrentTime;
    private TextView mTotalTime;
    private SeekBar mProgress;
    private Button mPlayBtn;
    private Button mNextBtn;
    private Button mPrevBtn;

    private float mLastMotionX;
    private float mLastMotionY;
    private int startX;
    private int startY;
    private final int threshold = 20;
    private boolean isClick = true;

    private int mWidth;
    private int mHeight;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_PROGRESS:
                    if (mVideoView.getCurrentPosition() > 0) {
                        mCurrentTime.setText(DateTimeUtil.formatLongToTimeStr(mVideoView.getCurrentPosition()));
                        int progress = (int) (mVideoView.getCurrentPosition() * 100L / mVideoView.getDuration());
                        mProgress.setProgress(progress);
                        if (mVideoView.getCurrentPosition() > mVideoView.getDuration() - 100) {
                            mCurrentTime.setText("00:00:00");
                            mProgress.setProgress(0);
                        }
                        mProgress.setSecondaryProgress(mVideoView.getBufferPercentage());
                    } else {
                        mCurrentTime.setText("00:00:00");
                        mProgress.setProgress(0);
                    }
                    break;
                case MSG_HIDE_CONTROLLER:
                    showOrHide();
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_video_player);

        mContext = this;

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mWidth = metric.widthPixels; // 屏幕宽度（像素）
        mHeight = metric.heightPixels; // 屏幕高度（像素）

        initView();

        initData();
    }

    protected void initView() {

        mTitleLayout = findViewById(R.id.lay_video_player_title);
        mVideoName = (TextView) findViewById(R.id.tv_video_player_name);

        mControllerLayout = findViewById(R.id.lay_media_controller);
        mCurrentTime = (TextView) findViewById(R.id.tv_media_current_time);
        mTotalTime = (TextView) findViewById(R.id.tv_media_total_time);
        mProgress = (SeekBar) findViewById(R.id.sb_media_progress);
        mProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mPlayBtn = (Button) findViewById(R.id.btn_media_play);
        mPlayBtn.setOnClickListener(this);
        mPrevBtn = (Button) findViewById(R.id.btn_media_prev);
        mPrevBtn.setOnClickListener(this);
        mPrevBtn.setClickable(true);
        mNextBtn = (Button) findViewById(R.id.btn_media_next);
        mNextBtn.setOnClickListener(this);
        mNextBtn.setClickable(true);

        mVideoView = (VideoView) this.findViewById(R.id.v_vidio_player);
        mVideoView.setOnTouchListener(mTouchListener);
        mVideoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

//				if (mPlayIndex == mPlayList.size() - 1) {
//					mPlayIndex = 0;
//				} else {
//					mPlayIndex++;
//				}
//
//				 play();

                mProgress.setProgress(0);
                mCurrentTime.setText("00:00:00");
                mTotalTime.setText("00:00:00");
                finish();
            }
        });

        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mProgress.setProgress(0);
                mCurrentTime.setText("00:00:00");
                mTotalTime.setText(DateTimeUtil.formatLongToTimeStr(mVideoView.getDuration()));

                mHandler.removeCallbacks(hideRunnable);
                mHandler.postDelayed(hideRunnable, HIDE_TIME);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
                    }
                }, 0, 1000);
            }
        });

        mVideoView.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(mContext, "该文件播放失败，未知错误！", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    protected void initData() {
        Intent intent = getIntent();
        mPlayList = intent.getStringArrayListExtra(ACTION_EXTRA_PLAY_LIST);
        mPlayIndex = intent.getIntExtra(ACTION_EXTRA_PLAY_INDEX, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        play();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 播放视频
     */
    private void play() {
        //本地播放
        File file = new File(mPlayList.get(mPlayIndex));
        mVideoName.setText(StringUtils.getPathSubName(mPlayList.get(mPlayIndex)));
        mPlayBtn.setBackgroundResource(R.drawable.selector_media_pause);
        if (file.exists()) {
            mVideoView.setVideoPath(file.getAbsolutePath());
            mVideoView.requestFocus();
            mVideoView.start();
        } else {
            Toast.makeText(mContext, "播放地址不存在!", Toast.LENGTH_SHORT).show();
        }
        //远程播放
//		mVideoName.setText(StringUtils.getPathSubName(mPlayList.get(mPlayIndex)));
//		mPlayBtn.setBackgroundResource(R.drawable.selector_media_pause);
//		String path = mPlayList.get(mPlayIndex);
//		if(!TextUtils.isEmpty(path)){
//			mVideoView.setVideoPath(path);
//			mVideoView.requestFocus();
//			mVideoView.start();
//		}else {
//			Toast.makeText(mContext, "播放地址不存在", Toast.LENGTH_SHORT).show();
//		}
    }

    /**
     * 滑动视频快退
     *
     * @param delataX
     */
    private void backward(float delataX) {
        int current = mVideoView.getCurrentPosition();
        int backwardTime = (int) (delataX / mWidth * mVideoView.getDuration());
        int currentTime = current - backwardTime;
        mVideoView.seekTo(currentTime);
        mProgress.setProgress((int) (currentTime * 100L / mVideoView.getDuration()));
        mCurrentTime.setText(DateTimeUtil.formatLongToTimeStr(currentTime));
    }

    /**
     * 滑动视频快进
     *
     * @param delataX
     */
    private void forward(float delataX) {
        int current = mVideoView.getCurrentPosition();
        int forwardTime = (int) (delataX / mWidth * mVideoView.getDuration());
        int currentTime = current + forwardTime;
        mVideoView.seekTo(currentTime);
        mProgress.setProgress((int) (currentTime * 100L / mVideoView.getDuration()));
        mCurrentTime.setText(DateTimeUtil.formatLongToTimeStr(currentTime));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_video_player_back) {
            finish();
        } else if (v == mPlayBtn) {
            if (mVideoView.isPlaying()) {
                mPlayBtn.setBackgroundResource(R.drawable.selector_media_play);
                mVideoView.pause();
            } else {
                mPlayBtn.setBackgroundResource(R.drawable.selector_media_pause);
                mVideoView.start();
            }
        } else if (v == mPrevBtn) {
            if (mPlayIndex == 0) {
                mPlayIndex = mPlayList.size() - 1;
            } else {
                mPlayIndex--;
            }

            play();
        } else if (v == mNextBtn) {
            if (mPlayIndex == mPlayList.size() - 1) {
                mPlayIndex = 0;
            } else {
                mPlayIndex++;
            }

            play();
        }
    }

    private final Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            showOrHide();
        }
    };

    private void showOrHide() {
        if (mTitleLayout.getVisibility() == View.VISIBLE) {
            mTitleLayout.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_record_leave_from_top);
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mTitleLayout.setVisibility(View.GONE);

                }
            });
            mTitleLayout.startAnimation(animation);

            mControllerLayout.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_record_leave_from_bottom);
            animation1.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mControllerLayout.setVisibility(View.GONE);
                }
            });
            mControllerLayout.startAnimation(animation1);
        } else {
            mTitleLayout.setVisibility(View.VISIBLE);
            mTitleLayout.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_record_entry_from_top);
            mTitleLayout.startAnimation(animation);

            mControllerLayout.setVisibility(View.VISIBLE);
            mControllerLayout.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_record_entry_from_bottom);
            mControllerLayout.startAnimation(animation1);
            mHandler.removeCallbacks(hideRunnable);
            mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }
    }

    private final OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeCallbacks(hideRunnable);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                int time = progress * mVideoView.getDuration() / 100;
                mVideoView.seekTo(time);
            }
        }
    };

    private final OnTouchListener mTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final float x = event.getX();
            final float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastMotionX = x;
                    mLastMotionY = y;
                    startX = (int) x;
                    startY = (int) y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - mLastMotionX;
                    float absDeltaX = Math.abs(deltaX);
                    if (deltaX > 0) {
                        // forward(absDeltaX);
                    } else if (deltaX < 0) {
                        // backward(absDeltaX);
                    }
                    mLastMotionX = x;
                    mLastMotionY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(x - startX) > threshold || Math.abs(y - startY) > threshold) {
                        isClick = false;
                    }
                    mLastMotionX = 0;
                    mLastMotionY = 0;
                    startX = 0;
                    if (isClick) {
                        showOrHide();
                    }
                    isClick = true;
                    break;

                default:
                    break;
            }
            return true;
        }

    };
}
