package com.xinzhihui.mydvr.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.util.Log;

/**
 * 自定义Log类，只有当LEVEL常量的值小于或等于对应日志级别值的时候，才会将日志打印出来.
 */
public class LogUtil {
    private static final String TAG = "LogUtil";
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    private static final int NOTHING = 6;
    private static int LEVEL = VERBOSE;
    private static boolean mSaveToFile = false;// 控制是否将日志保存到文件
    private static String mLogPath;

    /**
     * 关闭日志功能
     */
    public static void closeLog() {
        LEVEL = NOTHING;
    }

    /**
     * 将日志保存到文件
     *
     * @param logPath :日志文件路径
     */
    public static void saveToFile(String logPath) {
        mSaveToFile = true;
        mLogPath = logPath;
    }

    /**
     * 关闭将日志保存到文件功能
     */
    public static void closeSaveToFile() {
        mSaveToFile = false;
    }

    /**
     * 屏蔽部分日志输出
     *
     * @param level ：日志输出级别，从VERBOSE，DEBUG等中选择
     */
    public static void shieldPartialLog(int level) {
        LEVEL = level;
    }

    public static void v(String tag, String msg) {
        if (LEVEL <= VERBOSE) {
            Log.v(tag, msg);
            if (mSaveToFile) {
                printMsgToFile("VERBOSE_" + tag, msg);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (LEVEL <= DEBUG) {
            Log.d(tag, msg);
            if (mSaveToFile) {
                printMsgToFile("DEBUG_" + tag, msg);
            }
        }
    }

    public static void i(String tag, String msg) {
        if (LEVEL <= INFO) {
            Log.i(tag, msg);
            if (mSaveToFile) {
                printMsgToFile("INFO_" + tag, msg);
            }
        }
    }

    public static void w(String tag, String msg) {
        if (LEVEL <= WARN) {
            Log.w(tag, msg);
            if (mSaveToFile) {
                printMsgToFile("WARN_" + tag, msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (LEVEL <= ERROR) {
            Log.e(tag, msg);
            if (mSaveToFile) {
                printMsgToFile("ERROR_" + tag, msg);
            }
        }
    }

    /**
     * 打印日志到外存储卡
     */
    private static synchronized void printMsgToFile(String tag, String msg) {
        try {
            File file = new File(mLogPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(mLogPath, true);
            StringBuffer content = new StringBuffer();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            String time = format.format(calendar.getTime());
            content.append("time:").append(time).append(System.getProperty("line.separator"));
            content.append("tag:").append(tag).append(System.getProperty("line.separator"));
            content.append("content:").append(msg).append(System.getProperty("line.separator"));
            writer.write(content.toString());
            writer.close();
        } catch (IOException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

}