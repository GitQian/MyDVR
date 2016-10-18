package com.xinzhihui.mydvr.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/10/18.
 */
public class LockVideoDAL {
    private SQLiteDatabase db;
    private Context mContext;

    public LockVideoDAL(Context context) {
        mContext = context;
        db = MySqliteOpenHelper.getInstance(context).getWritableDatabase();
    }

    //增加
    public int addLockVideo(String path) {
        ContentValues values = new ContentValues();
        values.put("path", path);
        db.insert("LockVideoTable", null, values);
        return 0;
    }

    //删除
    public int deleteLockVideo(String path) {
        db.delete("LockVideoTable", "path = ?", new String[]{path});
        return 0;
    }

    //获取所有
    public List<String> getAllLockVideo() {
        Cursor cursor = db.query("LockVideoTable", new String[]{"path"}, null, null, null, null, null);
        List<String> pathList = new ArrayList<String>();
        String path = null;
        if (cursor.moveToFirst()) {
            do {
                path = cursor.getString(cursor.getColumnIndex("path"));
                pathList.add(path);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return pathList;
    }
}
