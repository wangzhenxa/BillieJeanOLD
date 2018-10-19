package com.ontim.billiejean.testcase;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;

import com.ontim.billiejean.BillieJeanConfig;
import com.ontim.billiejean.Utils;

import java.util.Calendar;

public class PowerOnOffCase implements BillieJeanCasable {
    private static final String TAG = BillieJeanConfig.PRO_TAG
            + "PowerOnOffCase";
    public static final String ALARM_RAW_DATA = "intent.extra.alarm_raw";

    @SuppressLint("WrongConstant")
    @Override
    public void execute(Context context) {
        Log.i(TAG, "execute!");
        Intent intent = new Intent(BillieJeanConfig.ACTION_BASE);
        Parcel out = Parcel.obtain();
        //alarm.writeToParcel(out, 0);
        out.setDataPosition(0);
        intent.putExtra(ALARM_RAW_DATA, out.marshall());
        
        PendingIntent sender = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Log.d(TAG,"context= "+context);
        try {
            AlarmManager am = (AlarmManager)
                    context.getSystemService(Context.ALARM_SERVICE);
            SystemClock.elapsedRealtime();
            Log.d(TAG,"开机");
            long onTime = System.currentTimeMillis()
                    + BillieJeanConfig.POWER_OFF_WAIT_TIME;
            CharSequence onTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", onTime);
            CharSequence onTimeStr2 = DateFormat.format("yyyy-MM-dd HH:mm:ss", onTime/1000);
            Log.d(TAG,"onTimeStr= "+onTimeStr);
            //Log.d(TAG,"onTimeStr2= "+onTimeStr2);
            Log.d(TAG,"sender= "+sender);
            am.setExact(7, onTime, sender);
            
            Calendar c = Calendar.getInstance();
            c.setTime(new java.util.Date(onTime));
            
            if (Utils.getRestartCount(context) > 0) {
                int i = Utils.getRestartCount(context)*1000;
                Log.d(TAG, "Restart_sleep= " + i);
                Thread.sleep(i);
            } else {
                Thread.sleep(BillieJeanConfig.POWER_ON_WAIT_TIME);
                Log.d(TAG, "Restart_sleep1= " + BillieJeanConfig.POWER_ON_WAIT_TIME);
            }
            

            if (Utils.getIsRunning(context)) {
                Intent offIt = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
                offIt.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
                //offIt.putExtra(Intent.EXTRA_TIMEOUT, 0L);
                offIt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(offIt);
                Thread.sleep(BillieJeanConfig.POWER_ON_WAIT_TIME);
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "error", e);
        }
    }
    
    @Override
    public boolean isSingleCase() {
        // TODO Auto-generated method stub
        return false;
    }

}
