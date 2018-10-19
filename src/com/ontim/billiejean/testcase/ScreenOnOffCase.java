package com.ontim.billiejean.testcase;

import com.ontim.billiejean.BillieJeanConfig;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import java.util.Random;

public class ScreenOnOffCase implements BillieJeanCasable {
    private static final String TAG = BillieJeanConfig.PRO_TAG
            + "ScreenOnOffCase";
    @Override
    public void execute(Context context) {
        Random random=new Random();
        int n;
        Log.i(TAG, "execute!");
        try {
            Log.d(TAG, "screen on ");
            ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                    .wakeUp(SystemClock.uptimeMillis());
            //Thread.sleep(BillieJeanConfig.SCREEN_ON_OFF_WAIT_TIME);
            n = random.nextInt(60)+1;
            Log.d(TAG, "等待灭屏时间==== " + n);
            Thread.sleep(n * 1000);
            Log.d(TAG, "screen off ");
            ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                    .goToSleep(SystemClock.uptimeMillis());
            //Thread.sleep(BillieJeanConfig.SCREEN_ON_OFF_WAIT_TIME);
            n = random.nextInt(60)+1;
            Log.d(TAG, "等待亮屏时间==== " + n);
            Thread.sleep(n * 1000);
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
