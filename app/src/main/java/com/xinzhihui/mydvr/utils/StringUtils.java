package com.xinzhihui.mydvr.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class StringUtils {
    public static final String DELIMITER = ",";

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static String nullToEmpty(CharSequence cs) {
        return isEmpty(cs) ? "" : cs.toString();
    }

    public static int getWordCount(CharSequence s) {
        if (s == null) {
            return 0;
        }
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 3;

        }
        return length;
    }

    @SuppressWarnings("unused")
    public static boolean isValidMobile(String mobile) {
        /**
         * 手机号码
         * 13*********
         * 15*********
         * 18*********
         *
         * 移动：134[0-8],135,136,137,138,139,150,151,157,158,159,182,187,188
         * 联通：130,131,132,152,155,156,185,186
         * 电信：133,1349,153,180,189
         */
//        String MOBILE = "^1(3[0-9]|5[0-9]|8[0-9])\\d{8}$";
        //手机号码的校验：1、长度校验11位，2、校验首位为1即可
//    	 String MOBILE = "^1\\d{10}$";
        /**
         * 中国移动：China Mobile
         * 134[0-8],135,136,137,138,139,150,151,157,158,159,182,187,188
         */
        String CM = "^1(34[0-8]|(3[5-9]|5[017-9]|8[278])\\d)\\d{7}$";
        /**
         * 中国联通：China Unicom
         * 130,131,132,152,155,156,185,186
         */
        String CU = "^1(3[0-2]|5[256]|8[56])\\d{8}$";
        /**
         * 中国电信：China Telecom
         * 133,1349,153,180,189
         */
        String CT = "^1((33|53|8[09])[0-9]|349)\\d{7}$";
        /**
         * 大陆地区固话及小灵通
         * 区号：010,020,021,022,023,024,025,027,028,029
         * 号码：七位或八位
         */
        String PHS = "^0(10|2[0-5789]|\\d{3})\\d{7,8}$";
        String MOBILE = "^1\\d{10}$";
        Pattern pattern = Pattern.compile(MOBILE);
        Matcher matcher = pattern.matcher(mobile);

        return matcher.matches();
    }

    public static boolean isValidAccount(CharSequence cs) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]+$");
        Matcher matcher = pattern.matcher(cs);

        return matcher.matches();
    }

    public static boolean isValidPassd(CharSequence cs) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]{6,16}+$");
        Matcher matcher = pattern.matcher(cs);

        return matcher.matches();
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !StringUtils.isEmpty(cs);
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是浮点数
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains("."))
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidMathNumber(String val) {
        Pattern pattern = Pattern.compile("((\\d+)?(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(val);
        return matcher.matches();
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isDigit(String value) {
        return isInteger(value) || isDouble(value);
    }

    public static String saftyTrimPhoneNum(String phoneNumber) {
        String ret = phoneNumber;

        if (StringUtils.isNotEmpty(phoneNumber)) {
            phoneNumber = phoneNumber.replaceAll(" |-", "");
            if (phoneNumber.startsWith("+") && phoneNumber.length() == 14) {
                phoneNumber = phoneNumber.substring(3);
            }
            ret = phoneNumber.replaceAll("\\D", "");
        }

        return ret;
    }

    //取文件名
    public static String subFileName(String path) {
        String fileName = null;
        if (path != null) {
            String[] strTmp = path.split("/");
            if (strTmp != null && strTmp.length > 1) {
                fileName = strTmp[strTmp.length - 1];
            }
        }
        return fileName;
    }

    /**
     * 获取文件名 或者 Uri标示ID
     *
     * @param path 路径 or Uri
     * @return
     */
    public static String getPathSubName(String path) {
        if (!TextUtils.isEmpty(path)) {
            int postion = path.lastIndexOf(File.separator);
            if (postion != -1) {
                return path.substring(postion + 1);
            }
        }
        return null;
    }

    /**
     * 获取文件后缀
     *
     * @param path 路径
     * @return
     */
    public static String getPathSuffix(String path) {
        if (!TextUtils.isEmpty(path)) {
            int postion = path.lastIndexOf('.');
            if (postion != -1) {
                return path.substring(postion);
            }
        }
        return null;
    }

    public static int getFirstNumericalIndex(String sourceString) {
        int index = 0;
        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(sourceString);
        if (matcher.find()) {
            index = sourceString.indexOf(matcher.group());
        }
        return index;
    }

    /**
     * 数组拼接为字符串 以 DELIMITER 分隔
     *
     * @param objs
     * @return
     */
    public static String arrayToString(int[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int a : array) {
            sb.append(a).append(DELIMITER);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    /**
     * 数组拼接为字符串 以 DELIMITER 分隔
     *
     * @param objs
     * @return
     */
    public static String arrayToString(Object[] objs) {
        if (objs == null || objs.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Object o : objs) {
            sb.append(o).append(DELIMITER);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    /**
     * 数组拼接为字符串 以分隔
     *
     * @param <T>
     * @param params
     * @return
     */
    public static <T> String arrayToString(List<T> params) {
        if (params == null || params.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (T param : params) {
            sb.append(param).append(DELIMITER);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    /**
     * 判断某个字符串是否存在于数组中
     *
     * @param stringArray 原数组
     * @param source      查找的字符串
     * @return 是否找到
     */
    public static boolean contains(String[] stringArray, String source) {
        // 转换为list
        List<String> tempList = Arrays.asList(stringArray);
        // 利用list的包含方法,进行判断
        if (tempList.contains(source)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 非空判断
     *
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        if (str == null || str.length() == 0 || str.equals("null")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 数值转换为地址
     *
     * @param addr
     * @return
     */
    public static String intToIp(int addr) {

        return ((addr & 0xFF) + "." + ((addr >>>= 8) & 0xFF) + "." + ((addr >>>= 8) & 0xFF) + "." + ((addr >>>= 8) & 0xFF));
    }

    public static String getNumericString(String name) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(name);
        return m.replaceAll("").trim();
    }

}
