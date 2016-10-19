package com.xinzhihui.mydvr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.xinzhihui.mydvr.fragment.SettingBehindFragment;
import com.xinzhihui.mydvr.fragment.SettingFrontFragment;

public class Setting2Activity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private RadioButton mFrontBtn;
    private RadioButton mBehindBtn;

    FragmentTransaction mFragmentTransaction = null;
    private Fragment mSetFrontFragment;
    private Fragment mSetBehindFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);

        initView();
    }

    private void initView(){
        mFrontBtn = (RadioButton) findViewById(R.id.rbtn_setting_front);
        mBehindBtn = (RadioButton) findViewById(R.id.rbtn_setting_behind);

        mFrontBtn.setOnCheckedChangeListener(this);
        mBehindBtn.setOnCheckedChangeListener(this);

        mSetFrontFragment = SettingFrontFragment.newInstance();
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.fragment_setting_container, mSetFrontFragment);
        mFragmentTransaction.commitAllowingStateLoss();
    }

    public void hideFragments(FragmentTransaction fragmentTransaction) {

        if (mSetFrontFragment != null) {
            fragmentTransaction.hide(mSetFrontFragment);
        }

        if (mSetBehindFragment != null) {
            fragmentTransaction.hide(mSetBehindFragment);
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
            case R.id.rbtn_setting_front:
                if (mSetFrontFragment == null) {
                    mSetFrontFragment = SettingFrontFragment.newInstance();
                    mFragmentTransaction.add(R.id.fragment_setting_container, mSetFrontFragment);
                }else {
                    mFragmentTransaction.show(mSetFrontFragment);
                }

                break;

            case R.id.rbtn_setting_behind:
                if (mSetBehindFragment == null) {
                    mSetBehindFragment = SettingBehindFragment.newInstance();
                    mFragmentTransaction.add(R.id.fragment_setting_container, mSetBehindFragment);
                }else {
                    mFragmentTransaction.show(mSetBehindFragment);
                }
                break;

            default:
                break;
        }
        mFragmentTransaction.commitAllowingStateLoss();
    }
}
