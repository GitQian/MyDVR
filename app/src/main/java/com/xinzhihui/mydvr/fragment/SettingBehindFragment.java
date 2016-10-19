package com.xinzhihui.mydvr.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.R;
import com.xinzhihui.mydvr.utils.SPUtils;

public class SettingBehindFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private Switch mSoundSwitch;
    private Switch mWaterSwitch;
    private Switch mAutoSwitch;

    public SettingBehindFragment() {
        // Required empty public constructor
    }

    public static SettingBehindFragment newInstance() {
        SettingBehindFragment fragment = new SettingBehindFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting_behind, container, false);
        initView(view);
        return view;
    }


    private void initView(View view) {
        mSoundSwitch = (Switch) view.findViewById(R.id.switch_setting_behind_sound);
        mSoundSwitch.setOnCheckedChangeListener(this);
        mSoundSwitch.setChecked((Boolean) SPUtils.get(MyApplication.getContext(), "isBehindSound", true));

        mWaterSwitch = (Switch) view.findViewById(R.id.switch_setting_behind_water);
        mWaterSwitch.setOnCheckedChangeListener(this);
        mWaterSwitch.setChecked((Boolean) SPUtils.get(MyApplication.getContext(), "isBehindWater", true));

        mAutoSwitch = (Switch) view.findViewById(R.id.switch_setting_behind_auto);
        mAutoSwitch.setOnCheckedChangeListener(this);
        mAutoSwitch.setChecked((Boolean) SPUtils.get(MyApplication.getContext(), "isBehindAuto", false));
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_setting_behind_sound:
                if (isChecked) {
                    SPUtils.put(MyApplication.getContext(), "isBehindSound", true);
                } else {
                    SPUtils.put(MyApplication.getContext(), "isBehindSound", false);
                }
                break;

            case R.id.switch_setting_behind_water:
                if (isChecked) {
                    SPUtils.put(MyApplication.getContext(), "isBehindWater", true);
                } else {
                    SPUtils.put(MyApplication.getContext(), "isBehindWater", false);
                }
                break;

            case R.id.switch_setting_behind_auto:
                if (isChecked) {
                    SPUtils.put(MyApplication.getContext(), "isBehindAuto", true);
                } else {
                    SPUtils.put(MyApplication.getContext(), "isBehindAuto", false);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
