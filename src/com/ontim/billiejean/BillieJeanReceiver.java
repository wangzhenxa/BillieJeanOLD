package com.ontim.billiejean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BillieJeanReceiver extends BroadcastReceiver {
    private static final String TAG = BillieJeanConfig.PRO_TAG + "BillieJeanReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "receive action = " + intent.getAction());
        Intent serviceIntent = new Intent();
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i(TAG, "ACTION_BOOT_COMPLETED");
            serviceIntent.setAction(BillieJeanConfig.ACTION_BEGIN_CASE_BOOT_START);
            serviceIntent.setClass(context, BillieJeanService.class);
            context.startService(serviceIntent);
            startActivity(context);
        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
            Log.i(TAG, "ACTION_MEDIA_MOUNTED");

            serviceIntent.setAction(BillieJeanConfig.ACTION_BEGIN_CASE_MOUNT_START);
            serviceIntent.setClass(context, BillieJeanService.class);
            context.startService(serviceIntent);
            startActivity(context);
        } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
            Log.i(TAG, "ACTION_MEDIA_UNMOUNTED");
            serviceIntent.setAction(BillieJeanConfig.ACTION_BEGIN_CASE_UNMOUNT_START);
            serviceIntent.setClass(context, BillieJeanService.class);
            context.startService(serviceIntent);
            startActivity(context);
        }
    }

    private void startActivity(Context context) {
        Intent intent = new Intent(context, BillieJean.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
