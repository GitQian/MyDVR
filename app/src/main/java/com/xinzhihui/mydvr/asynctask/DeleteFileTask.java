package com.xinzhihui.mydvr.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.xinzhihui.mydvr.AppConfig;
import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.db.LockVideoDAL;
import com.xinzhihui.mydvr.utils.FileOrderUtils;
import com.xinzhihui.mydvr.utils.SDCardUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */

public class DeleteFileTask extends AsyncTask<String[], String, String> {

    @Override
    protected String doInBackground(String[]... params) {
        //TODO 在DVR根目录，对文件进行排序
        while (SDCardUtils.getFreeBytes(AppConfig.DVR_PATH) < 300 * 1024 * 1024) {
            List<File> listFile = FileOrderUtils.orderByDate(params[0]);

            LockVideoDAL lockVideoDAL = new LockVideoDAL(MyApplication.getContext());
            List<String> listPath = lockVideoDAL.getAllLockVideo();

            int index = 0;
            while (listPath.contains(listFile.get(index).getAbsolutePath())) {
                //跳过锁定文件，表删！
                index++;
            }
            //删除掉最久一个
            if (index < listFile.size()) {
                listFile.get(index).delete();
            }

            for (File file : listFile) {
                Log.d("qiansheng", file.getAbsolutePath());
            }
        }
        return null;
    }
}
