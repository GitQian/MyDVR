package com.xinzhihui.mydvr.utils;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */

public class FileOrderUtils {
    //按照文件大小排序
    public static List<File> orderByLength(String fliePath) {
        List<File> files = Arrays.asList(new File(fliePath).listFiles());
        Collections.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.length() - f2.length();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }

            public boolean equals(Object obj) {
                return true;
            }
        });
        return files;
    }

    //按照文件名称排序
    public static List<File> orderByName(String fliePath) {
        List<File> files = Arrays.asList(new File(fliePath).listFiles());
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
        return files;
    }

    //按日期排序
    public static List<File> orderByDate(String[] filePath) {
        Log.d("qiansheng", filePath[0] + "------------->");
        Log.d("qiansheng", filePath[1] + "------------->");
        List<File> files1 = Arrays.asList(new File(filePath[0]).listFiles());
        List<File> fileList1 = new ArrayList(files1);

        List<File> files2 = Arrays.asList(new File(filePath[1]).listFiles());
        List<File> fileList2 = new ArrayList(files2);

        if (fileList2 != null && fileList2.size() != 0) {
            if (fileList1 != null) {
                fileList1.addAll(fileList2);
            } else {
                fileList1 = fileList2;
            }

        }

        if (fileList1 != null) {
            Collections.sort(fileList1, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
//                long diff = o1.lastModified() - o2.lastModified();
//                if (diff > 0)
//                    return 1;
//                else if (diff == 0)
//                    return 0;
//                else
//                    return -1;
                    String time1 = o1.getName().split("_", 2)[1];
                    String time2 = o2.getName().split("_", 2)[1];
                    return time1.compareTo(time2);
                }
            });
        }
        return fileList1;
    }
}
