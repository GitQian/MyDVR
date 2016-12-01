package com.xinzhihui.mydvr.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.R;
import com.xinzhihui.mydvr.utils.AppUtils;
import com.xinzhihui.mydvr.utils.CommonUtils;
import com.xinzhihui.mydvr.utils.SPUtils;

import java.io.File;
/**
 * Created by Administrator on 2016/11/17.
 */

/**
 * 锁视频列表Fragment
 */
public class SettingGeneralFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Switch mSetAutoRunSwitch;
    private RelativeLayout mSetStorageRly;
    private String[] mPaths;

    private TextView mVersionsTv;

    public SettingGeneralFragment() {
        // Required empty public constructor
    }

    public static SettingGeneralFragment newInstance() {
        SettingGeneralFragment fragment = new SettingGeneralFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting_general, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mSetAutoRunSwitch = (Switch) view.findViewById(R.id.switch_setting_general_auto);
        mSetStorageRly = (RelativeLayout) view.findViewById(R.id.rly_setting_general_storage);
        mVersionsTv = (TextView) view.findViewById(R.id.tv_app_versions);
        mVersionsTv.setText("版本信息：V_" + AppUtils.getVersionName(MyApplication.getContext()));

        mPaths = CommonUtils.getStoragePaths(this.getActivity(), true);
        mSetAutoRunSwitch.setChecked((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_APP_AUTO_RUN, true));

        mSetAutoRunSwitch.setOnCheckedChangeListener(this);
        mSetStorageRly.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_setting_general_auto:
                if (isChecked) {
                    SPUtils.put(MyApplication.getContext(), AppConfig.KEY_APP_AUTO_RUN, true);
                } else {
                    SPUtils.put(MyApplication.getContext(), AppConfig.KEY_APP_AUTO_RUN, false);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rly_setting_general_storage:
                mPaths = CommonUtils.getStoragePaths(this.getActivity(), true);
                int storageWhere = (Integer) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_STORAGE_WHERE, Integer.valueOf(0));  //已选编号

                Dialog sizeDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("选择存储路径")
                        .setSingleChoiceItems(mPaths, storageWhere, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SPUtils.put(MyApplication.getContext(), AppConfig.KEY_STORAGE_WHERE, which);
                                SPUtils.put(MyApplication.getContext(), AppConfig.KEY_STORAGE_PATH, mPaths[which]);
                                AppConfig.ROOT_DIR = mPaths[which];

                                AppConfig.DVR_PATH = mPaths[which] + "/DVR";
                                AppConfig.FRONT_VIDEO_PATH = AppConfig.DVR_PATH + "/front/";
                                AppConfig.BEHIND_VIDEO_PATH = AppConfig.DVR_PATH + "/behind/";
                                AppConfig.LEFT_VIDEO_PATH = AppConfig.DVR_PATH + "/left/";
                                AppConfig.RIGHT_VIDEO_PATH = AppConfig.DVR_PATH + "/right/";
                                AppConfig.PICTURE_PATH = AppConfig.DVR_PATH + "/picture/";
                                makeDir(AppConfig.DVR_PATH);
                                makeDir(AppConfig.FRONT_VIDEO_PATH);
                                makeDir(AppConfig.BEHIND_VIDEO_PATH);
                                makeDir(AppConfig.LEFT_VIDEO_PATH);
                                makeDir(AppConfig.RIGHT_VIDEO_PATH);
                                makeDir(AppConfig.PICTURE_PATH);
                            }
                        })
                        .create();
                sizeDialog.show();
                break;

            default:
                break;
        }
    }

    private void makeDir(String path) {
        File dir = new File(path);
        if (dir.isFile()) {
            dir.delete();
        }
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
}
