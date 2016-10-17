package com.xinzhihui.mydvr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SPUtils;

public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private Switch mSoundSwitch;
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

            default:
                break;
        }
    }
}
