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

public class SettingFrontFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private Switch mSoundSwitch;
    private Switch mAutoSwitch;

    public SettingFrontFragment() {
        // Required empty public constructor
    }

    public static SettingFrontFragment newInstance() {
        SettingFrontFragment fragment = new SettingFrontFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting_front, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mSoundSwitch = (Switch) view.findViewById(R.id.switch_setting_front_sound);
        mSoundSwitch.setOnCheckedChangeListener(this);
        mSoundSwitch.setChecked((Boolean) SPUtils.get(MyApplication.getContext(), "isFrontSound", true));

        mAutoSwitch = (Switch) view.findViewById(R.id.switch_setting_front_auto);
        mAutoSwitch.setOnCheckedChangeListener(this);
        mAutoSwitch.setChecked((Boolean) SPUtils.get(MyApplication.getContext(), "isFrontAuto", true));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_setting_front_sound:
                if (isChecked) {
                    SPUtils.put(MyApplication.getContext(), "isFrontSound", true);
                } else {
                    SPUtils.put(MyApplication.getContext(), "isFrontSound", false);
                }
                break;

            case R.id.switch_setting_front_auto:
                if (isChecked) {
                    SPUtils.put(MyApplication.getContext(), "isFrontAuto", true);
                } else {
                    SPUtils.put(MyApplication.getContext(), "isFrontAuto", false);
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
