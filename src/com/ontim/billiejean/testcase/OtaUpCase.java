package com.ontim.billiejean.testcase;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;

import com.ontim.billiejean.BillieJeanConfig;
import com.ontim.billiejean.Utils;

import android.content.Context;
import android.util.Log;

public class OtaUpCase implements BillieJeanCasable {
    private static final String TAG = BillieJeanConfig.PRO_TAG + "OtaUpCase";

    File OTA_FILE = new File("/sdcard/update/otaup");
    File CAMERA_FILE = new File("/sdcard/update/camera");
    @Override
    public void execute(Context context) {
        Log.i(TAG, "execute!");
        try {
            /*if (!Utils.excuteCommand("ps").contains(
                    BillieJeanConfig.CPU_STRESS_PROCESS_NAME)) {
                Utils.putTotalCount(context, -1);
                Utils.excuteCpuStress();
            }*/
            if (hasResetFactoryCase(CAMERA_FILE)) {
                Utils.excuteCommand("uiautomator runtest AutoCameraTest.jar -c com.uitest.Run");
            }
            
            if (hasResetFactoryCase(OTA_FILE)) {
                removeResetFlag(OTA_FILE);
                Utils.excuteCommand("rm /sdcard/*-ota.zip");
                Utils.excuteCommand("cp /sdcard/update/1-ota.zip /sdcard/");
            } else {
                createResetFlag(OTA_FILE);
                Utils.excuteCommand("rm /sdcard/*-ota.zip");
                Utils.excuteCommand("cp /sdcard/update/2-ota.zip /sdcard/");
            }
            createResetFlag(CAMERA_FILE);
            Utils.excuteCommand("uiautomator runtest OtaUpdata.jar -c com.uitest.Run");
            Thread.sleep(BillieJeanConfig.REBOOT_WAIT_TIME);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } catch (InterruptedException e) {
            Log.e(TAG, "", e);
        }
    }
    
    public void createResetFlag(File c_f) throws IOException {
        new FileOutputStream(c_f);
    }
    
    public boolean hasResetFactoryCase(File h_f) {
        return h_f.exists();
    }
    
    public boolean removeResetFlag(File r_f) {
        boolean flag = false;
        if (r_f.exists()) {
            flag = r_f.delete();
        }
        return flag;
    }

    @Override
    public boolean isSingleCase() {
        return false;
    }

}
