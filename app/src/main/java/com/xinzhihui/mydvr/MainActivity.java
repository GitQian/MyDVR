package com.xinzhihui.mydvr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.xinzhihui.mydvr.utils.SPUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mCameraBtn;

    private Spinner mSpinnerFront;
    private Spinner mSPinnerBehind;

    private Switch mAppAutoSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraBtn = (Button) findViewById(R.id.btn_camera);
        mCameraBtn.setOnClickListener(this);

        mSpinnerFront = (Spinner) findViewById(R.id.spinner_front);
        mSPinnerBehind = (Spinner) findViewById(R.id.spinner_behind);
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

        switch (AppConfig.FRONT_CAMERA_INDEX) {
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

        switch (AppConfig.BEHIND_CAMERA_INDEX) {
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSPinnerBehind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppConfig.BEHIND_CAMERA_INDEX = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}
