package com.xinzhihui.mydvr;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.xinzhihui.mydvr.fragment.VideoFileFragment;

/**
 * 采用分类形式显示文件
 */
public class FileList2Activity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, VideoFileFragment.OnFragmentInteractionListener {

    private RadioButton mLockVideoBtn;
    private RadioButton mNormalVideoBtn;
    private RadioButton mPictureBtn;

    FragmentTransaction mFragmentTransaction = null;
    private Fragment mLockVideoFragment;
    private Fragment mNormalVideoFragment;
    private Fragment mPictureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list2);

        initView();
    }

    private void initView() {
        mLockVideoBtn = (RadioButton) findViewById(R.id.rbtn_lock_video);
        mNormalVideoBtn = (RadioButton) findViewById(R.id.rbtn_normal_video);
        mPictureBtn = (RadioButton) findViewById(R.id.rbtn_all_picture);

        mLockVideoBtn.setOnCheckedChangeListener(this);
        mNormalVideoBtn.setOnCheckedChangeListener(this);
        mPictureBtn.setOnCheckedChangeListener(this);

        mLockVideoFragment = VideoFileFragment.newInstance("LockVideo", AppConfig.FRONT_VIDEO_PATH);
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.fragment_container, mLockVideoFragment);
        mFragmentTransaction.commitAllowingStateLoss();
    }

    public void hideFragments(FragmentTransaction fragmentTransaction) {

        if (mLockVideoFragment != null) {
            fragmentTransaction.hide(mLockVideoFragment);
        }

        if (mNormalVideoFragment != null) {
            fragmentTransaction.hide(mNormalVideoFragment);
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
                    mLockVideoFragment = VideoFileFragment.newInstance("LockVideo", AppConfig.FRONT_VIDEO_PATH);
                    mFragmentTransaction.add(R.id.fragment_container, mLockVideoFragment);
                } else {
                    mFragmentTransaction.show(mLockVideoFragment);
                }
                break;

            case R.id.rbtn_normal_video:
                if (null == mNormalVideoFragment) {
                    mNormalVideoFragment = VideoFileFragment.newInstance("NormalVideo", AppConfig.BEHIND_VIDEO_PATH);
                    mFragmentTransaction.add(R.id.fragment_container, mNormalVideoFragment);
                } else {
                    mFragmentTransaction.show(mNormalVideoFragment);
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
