package com.xinzhihui.mydvr;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.xinzhihui.mydvr.utils.CommonUtils;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.SDCardUtils;
import com.xinzhihui.mydvr.utils.SPUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mCameraBtn;

    private Spinner mSpinnerFront;
    private Spinner mSPinnerBehind;
    private Spinner mSpinnerStorage;
    private String[] mPaths;

    private Switch mAppAutoSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraBtn = (Button) findViewById(R.id.btn_camera);
        mCameraBtn.setOnClickListener(this);

        mSpinnerFront = (Spinner) findViewById(R.id.spinner_front);
        mSPinnerBehind = (Spinner) findViewById(R.id.spinner_behind);
        mSpinnerStorage = (Spinner) findViewById(R.id.spinner_storage_path);
        mAppAutoSwitch = (Switch) findViewById(R.id.switch_app_auto);
        mAppAutoSwitch.setChecked((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_APP_AUTO_RUN, true));
        mAppAutoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPUtils.put(MyApplication.getContext(), AppConfig.KEY_APP_AUTO_RUN, true);
                } else {
                    SPUtils.put(MyApplication.getContext(), AppConfig.KEY_APP_AUTO_RUN, false);
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerFront.setAdapter(adapter);
        mSPinnerBehind.setAdapter(adapter);

        mPaths = CommonUtils.getStoragePaths(MainActivity.this, true);
        ArrayAdapter<String> adapterStorage = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mPaths);
        adapterStorage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerStorage.setAdapter(adapterStorage);
        mSpinnerStorage.setSelection(0);

        switch ((int)SPUtils.get(MyApplication.getContext(), "KEY_FRONT_CAMERA_INDEX", 0)) {
            case 0:
                mSpinnerFront.setSelection(0);
                break;
            case 1:
                mSpinnerFront.setSelection(1);
                break;
            case 2:
                mSpinnerFront.setSelection(2);
                break;
            case 3:
                mSpinnerFront.setSelection(3);
                break;
            case 4:
                mSpinnerFront.setSelection(4);
                break;
            case 5:
                mSpinnerFront.setSelection(5);
                break;
            case 6:
                mSpinnerFront.setSelection(6);
                break;
            case 7:
                mSpinnerFront.setSelection(7);
                break;
            case 8:
                mSpinnerFront.setSelection(8);
                break;
        }

        switch ((int)SPUtils.get(MyApplication.getContext(), "KEY_BEHIND_CAMERA_INDEX", 4)) {
            case 0:
                mSPinnerBehind.setSelection(0);
                break;
            case 1:
                mSPinnerBehind.setSelection(1);
                break;
            case 2:
                mSPinnerBehind.setSelection(2);
                break;
            case 3:
                mSPinnerBehind.setSelection(3);
                break;
            case 4:
                mSPinnerBehind.setSelection(4);
                break;
            case 5:
                mSPinnerBehind.setSelection(5);
                break;
            case 6:
                mSPinnerBehind.setSelection(6);
                break;
            case 7:
                mSPinnerBehind.setSelection(7);
                break;
            case 8:
                mSPinnerBehind.setSelection(8);
                break;
        }

        mSpinnerFront.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppConfig.FRONT_CAMERA_INDEX = position;
                SPUtils.put(MyApplication.getContext(), "KEY_FRONT_CAMERA_INDEX", position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSPinnerBehind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppConfig.BEHIND_CAMERA_INDEX = position;
                SPUtils.put(MyApplication.getContext(), "KEY_BEHIND_CAMERA_INDEX", position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        mSpinnerStorage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                AppConfig.ROOT_DIR = mPaths[position];
//
//                AppConfig.DVR_PATH = mPaths[position] + "/DVR";
//                AppConfig.FRONT_VIDEO_PATH = AppConfig.DVR_PATH + "/front/";
//                AppConfig.BEHIND_VIDEO_PATH = AppConfig.DVR_PATH + "/behind/";
//                AppConfig.LEFT_VIDEO_PATH = AppConfig.DVR_PATH + "/left/";
//                AppConfig.RIGHT_VIDEO_PATH = AppConfig.DVR_PATH + "/right/";
//                AppConfig.PICTURE_PATH = AppConfig.DVR_PATH + "/picture/";
//                makeDir(AppConfig.DVR_PATH);
//                makeDir(AppConfig.FRONT_VIDEO_PATH);
//                makeDir(AppConfig.BEHIND_VIDEO_PATH);
//                makeDir(AppConfig.LEFT_VIDEO_PATH);
//                makeDir(AppConfig.RIGHT_VIDEO_PATH);
//                makeDir(AppConfig.PICTURE_PATH);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
//                long allSize = SDCardUtils.getFolderSize(new File(AppConfig.DVR_PATH)) + SDCardUtils.getFreeBytes(AppConfig.ROOT_DIR);
//                if (allSize < 300 * 1024 * 1024) {
//                    LogUtil.d("qiansheng", "DVR can use size:" + allSize);
//                    //能给DVR使用的空间 < 300M 则不让App运行（/DVR空间 + free空间）
//                    //TODO < 300M 则会循环删除，这里可以避免无限循环（怎么删都小于300M）
//                    Dialog alertDialog = new AlertDialog.Builder(MainActivity.this)
//                            .setCancelable(false)
//                            .setTitle("提示")
//                            .setMessage("存储空间不足，请及时清理文件！")
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    finish();
//                                }
//                            })
//                            .create();
//                    alertDialog.show();
//                    break;
//                } else {
//                    LogUtil.d("qiansheng", "DVR can use size:" + allSize);
//                }
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
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
