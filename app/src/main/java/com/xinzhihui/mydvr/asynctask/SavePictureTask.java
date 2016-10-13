package com.xinzhihui.mydvr.asynctask;

import android.os.AsyncTask;
import android.widget.Toast;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.utils.DateTimeUtil;
import com.xinzhihui.mydvr.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2016/10/8.
 */
public class SavePictureTask extends AsyncTask<byte[], String, String> {
    @Override
    protected String doInBackground(byte[]... params) {
        String path = AppConfig.PICTURE_PATH;
        File out = new File(path);
        if (!out.exists()) {
            out.mkdirs();
        }
        File picture = new File(path + "/" + DateTimeUtil.getCurrentDateTimeReplaceSpace() + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(picture.getPath());
            fos.write(params[0]);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(MyApplication.getContext(), "拍照完成", Toast.LENGTH_SHORT).show();
    }
}
