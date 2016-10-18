package com.xinzhihui.mydvr.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2016/10/18.
 */
public class MySqliteOpenHelper extends SQLiteOpenHelper{

    private Context mContext;

    private static MySqliteOpenHelper instance;

    //创建表sql
    public static final String lockVideoSql = "create table LockVideoTable(id integer primary key autoincrement, path varchar)";

    private MySqliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    //双null单例获取
    public static MySqliteOpenHelper getInstance(Context context){
        if (instance == null) {
            synchronized (MySqliteOpenHelper.class){
                if (instance == null) {
                    instance = new MySqliteOpenHelper(context, "DVRDb", null, 1);
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //如果数据库已经存在，则onCreate将不会执行（更新数据库应该重写onUpgrade()）
        db.execSQL(lockVideoSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //onUpgrade何时执行？当版本号更新时...
    }


}
