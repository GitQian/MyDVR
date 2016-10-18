package com.xinzhihui.mydvr;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.xinzhihui.mydvr.fragment.LockVideoFragment;
import com.xinzhihui.mydvr.fragment.VideoFileFragment;

/**
 * 采用分类形式显示文件
 */
public class FileList2Activity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, VideoFileFragment.OnFragmentInteractionListener {

    private RadioButton mLockVideoBtn;
    private RadioButton mFrontVideoBtn;
    private RadioButton mBehindVideoBtn;
    private RadioButton mPictureBtn;

    FragmentTransaction mFragmentTransaction = null;
    private Fragment mLockVideoFragment;
    private Fragment mFrontVideoFragment;
    private Fragment mBehindVideoFragment;
    private Fragment mPictureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list2);

        initView();
    }

    private void initView() {
        mLockVideoBtn = (RadioButton) findViewById(R.id.rbtn_lock_video);
        mFrontVideoBtn = (RadioButton) findViewById(R.id.rbtn_front_video);
        mBehindVideoBtn = (RadioButton) findViewById(R.id.rbtn_behind_video);
        mPictureBtn = (RadioButton) findViewById(R.id.rbtn_all_picture);

        mLockVideoBtn.setOnCheckedChangeListener(this);
        mFrontVideoBtn.setOnCheckedChangeListener(this);
        mBehindVideoBtn.setOnCheckedChangeListener(this);
        mPictureBtn.setOnCheckedChangeListener(this);

        mLockVideoFragment = LockVideoFragment.newInstance();
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.fragment_container, mLockVideoFragment);
        mFragmentTransaction.commitAllowingStateLoss();
    }

    public void hideFragments(FragmentTransaction fragmentTransaction) {

        if (mLockVideoFragment != null) {
            fragmentTransaction.hide(mLockVideoFragment);
        }

        if (mFrontVideoFragment != null) {
            fragmentTransaction.hide(mFrontVideoFragment);
        }

        if (mBehindVideoFragment != null) {
            fragmentTransaction.hide(mBehindVideoFragment);
        }
        if (mPictureFragment != null) {
            fragmentTransaction.hide(mPictureFragment);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragments(mFragmentTransaction);

        switch (buttonView.getId()) {
            case R.id.rbtn_lock_video:
                if (null == mLockVideoFragment) {
                    mLockVideoFragment = LockVideoFragment.newInstance();
                    mFragmentTransaction.add(R.id.fragment_container, mLockVideoFragment);
                } else {
                    mFragmentTransaction.show(mLockVideoFragment);
                }
                break;

            case R.id.rbtn_front_video:
                if (null == mFrontVideoFragment) {
                    mFrontVideoFragment = VideoFileFragment.newInstance("FrontVideo", AppConfig.FRONT_VIDEO_PATH);
                    mFragmentTransaction.add(R.id.fragment_container, mFrontVideoFragment);
                } else {
                    mFragmentTransaction.show(mFrontVideoFragment);
                }
                break;

            case R.id.rbtn_behind_video:
                if (mBehindVideoFragment == null) {
                    mBehindVideoFragment = VideoFileFragment.newInstance("BehindVideo", AppConfig.BEHIND_VIDEO_PATH);
                    mFragmentTransaction.add(R.id.fragment_container, mBehindVideoFragment);
                } else {
                    mFragmentTransaction.show(mBehindVideoFragment);
                }
                break;

            case R.id.rbtn_all_picture:
                if (null == mPictureFragment) {
                    mPictureFragment = VideoFileFragment.newInstance("Picture", AppConfig.PICTURE_PATH);
                    mFragmentTransaction.add(R.id.fragment_container, mPictureFragment);
                } else {
                    mFragmentTransaction.show(mPictureFragment);
                }
                break;

            default:
                break;
        }
        mFragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO Fragment回调方法
    }
}
