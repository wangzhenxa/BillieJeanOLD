package com.ontim.billiejean.testcase;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.ontim.billiejean.BillieJeanConfig;
import com.ontim.billiejean.Utils;

public class RebootCase implements BillieJeanCasable {
    private static final String TAG = BillieJeanConfig.PRO_TAG + "RebootCase";

    @Override
    public void execute(Context context) {
        Log.i(TAG, "execute");
        try {
            if (Utils.getRestartCount(context) > 0) {
                int i = Utils.getRestartCount(context)*1000;
                Thread.sleep(i);
                Log.i(TAG, "Restart_sleep= " + i);
                if (Utils.getIsRunning(context)) {
                    ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                            .reboot(null);
                }
            } else {
                Thread.sleep(BillieJeanConfig.REBOOT_WAIT_TIME);
                Log.i(TAG, "REBOOT_sleep= " + BillieJeanConfig.REBOOT_WAIT_TIME);
                if (Utils.getIsRunning(context)) {
                    ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                            .reboot(null);
               }
            }
            
        } catch (InterruptedException e) {
            Log.e(TAG, "", e);
        }
    }
    @Override
    public boolean isSingleCase() {
        // TODO Auto-generated method stub
        return false;
    }

}
