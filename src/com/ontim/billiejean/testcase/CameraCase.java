package com.ontim.billiejean.testcase;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;

import com.ontim.billiejean.BillieJeanConfig;
import com.ontim.billiejean.Utils;
import android.content.Context;
import android.util.Log;

public class CameraCase implements BillieJeanCasable {
    private static final String TAG = BillieJeanConfig.PRO_TAG + "CameraCase";

    @Override
    public void execute(Context context) {
        Log.i(TAG, "execute!");
        try {
            Utils.excuteCommand("uiautomator runtest Camera_HDR_test.jar -c com.uitest.Run");
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
