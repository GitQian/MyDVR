package com.xinzhihui.mydvr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mCameraBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraBtn = (Button) findViewById(R.id.btn_camera);
        mCameraBtn.setOnClickListener(this);
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
