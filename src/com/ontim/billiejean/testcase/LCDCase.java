package com.ontim.billiejean.testcase;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import com.ontim.billiejean.BillieJeanConfig;

public class LCDCase implements BillieJeanCasable {
    private static final String TAG = BillieJeanConfig.PRO_TAG + "LCDCase";

    @Override
    public void execute(Context context) {
        ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                .wakeUp(SystemClock.uptimeMillis());
        startActivity(context);
    }

    private void startActivity(Context context) {
        Intent intent = new Intent(context, LCDTestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        // overridePendingTransition(R.anim.activity_open,0);
    }

    @Override
    public boolean isSingleCase() {
        return true;
    }

}
