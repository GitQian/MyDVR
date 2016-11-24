// ICameraManager.aidl
package com.xinzhihui.mydvr;

// Declare any non-default types here with import statements

interface ICameraManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void open(int cameraId);
    void startPreView(int cameraId);
    void stopPreView(int cameraId);
    boolean startRecord(int cameraId);
    void stopRecord(int cameraId);
}
