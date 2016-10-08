package com.xinzhihui.mydvr.asynctask;

import android.os.AsyncTask;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.utils.DateTimeUtil;

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
        File picture = new File(path + "/" + DateTimeUtil.getCurrentNumberDateTime() + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(picture.getPath());
            fos.write(params[0]);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
