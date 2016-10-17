package com.xinzhihui.mydvr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SPUtils;

public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private Switch mSoundSwitch;
    private Switch mAutoSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
    }

    private void initView(){
        mSoundSwitch = (Switch) findViewById(R.id.switch_setting_sound);
        mSoundSwitch.setOnCheckedChangeListener(this);
        mSoundSwitch.setChecked((Boolean) SPUtils.get(MyApplication.getContext(),"isSound", true));

        mAutoSwitch = (Switch) findViewById(R.id.switch_setting_auto);
        mAutoSwitch.setOnCheckedChangeListener(this);
        mAutoSwitch.setChecked((Boolean) SPUtils.get(MyApplication.getContext(), "isAuto", true));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_setting_sound:
                if (isChecked) {
                    SPUtils.put(MyApplication.getContext(), "isSound", true);
                    LogUtil.d("qiansheng", "onCheckedChanged ---------> isSond is true!");
                }else {
                    SPUtils.put(MyApplication.getContext(), "isSound", false);
                    LogUtil.d("qiansheng", "onCheckedChanged ---------> isSond is false!");
                }
                break;

            case R.id.switch_setting_auto:
                if (isChecked) {
                    SPUtils.put(MyApplication.getContext(), "isAuto", true);
                }else {
                    SPUtils.put(MyApplication.getContext(), "isAuto", false);
                }
                break;

            default:
                break;
        }
    }
}
