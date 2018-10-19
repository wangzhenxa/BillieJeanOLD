package com.ontim.billiejean;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import com.ontim.billiejean.testcase.*;

import java.io.IOException;

import static com.ontim.billiejean.Utils.writeReaderFile;

public class BillieJeanService extends Service {
    private static final String TAG = BillieJeanConfig.PRO_TAG + "BillieJeanService";

    private UIRefreshListener refreshListener = null;
    private BillieJeanCasable action = null;

    private BatteryMonitorReceiver receiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "oncreate");
        receiver = new BatteryMonitorReceiver();
        receiver.regist(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.i(TAG, "start command = " + intent.getAction());
            Log.d(TAG, "Utils.hasResetFactoryCase() = " + Utils.hasResetFactoryCase());
            if (Utils.hasResetFactoryCase()) {
                Utils.putTestMode(this, BillieJeanConfig.TEST_RESET_FACTORY_TEST);
                if (intent.getAction() == BillieJeanConfig.ACTION_BEGIN_CASE_BOOT_START) {
                    try {
                        int i = Utils.readerFile(BillieJeanConfig.RESET_FLAG);
                        int totalCount = Utils.readerFile(BillieJeanConfig.RESET_FACTORY_FLAG);
                        Log.i(TAG, "testCount" + i);
                        if (totalCount - i > 0) {
                            writeReaderFile(BillieJeanConfig.RESET_FLAG, String.valueOf(i));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Log.d(TAG,"===========================in the Start Command =========================");
        executeCase();
        // }
        return START_REDELIVER_INTENT;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        receiver.unRgist(this);
    }

    private void refreshUi() {
        if (refreshListener != null) {
            refreshListener.refresh();
        }
    }

    /**
     * 执行测试用例
     */
    protected void executeCase() {
        Log.d(TAG, "getTestMode================" + Utils.getTestMode(this));
        Log.d(TAG, "TEST_RESET_FACTORY_TEST================" + BillieJeanConfig.TEST_RESET_FACTORY_TEST);
        if (Utils.getTestMode(this) == BillieJeanConfig.TEST_RESET_FACTORY_TEST) {
            try {
                Utils.createResetFlag();
                int i = Utils.readerFile(BillieJeanConfig.RESET_FLAG);
                int totalCount = Utils.readerFile(BillieJeanConfig.RESET_FACTORY_FLAG);
                Utils.putTestCount(BillieJeanService.this, i - 1);
                Utils.putTotalCount(BillieJeanService.this, totalCount);
                Utils.putSelectedCase(BillieJeanService.this, BillieJeanConfig.TEST_RESET_FACTORY_TEST);
            } catch (IOException e) {
                Log.e(TAG, "", e);
            }
        }

        /**
         * 执行一次完成后，检测再次执行的条件，然后再次执行
         */
        if (checkBattery()) {
            if (checkIsRunning()) {
                if (checkTargetCount()) {
                    refreshUi();
                    Log.d(TAG, "Make action...");
                    makeAction();
                    /**
                     * 执行测试
                     */
                    ExcuteCaseTask Task = new ExcuteCaseTask();
                    Task.execute(0);
                } else {
                    Log.d(TAG, "TargetCount == testcount");
                }
            } else {
                Log.d(TAG, "Task is not Running");
            }
        } else {
//            电池问题导致不能进行测试
            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, BillieJeanService.class);
            intent.setAction(BillieJeanConfig.ACTION_BASE);
            int requestCode = 0;
            PendingIntent pendIntent = PendingIntent.getService(this, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            long triggerAtTime = System.currentTimeMillis() + 30 * 1000;
            Log.d(TAG, "System.currentTimeMillis() = " + System.currentTimeMillis());
            Log.d(TAG, "triggerAtTime = " + triggerAtTime);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pendIntent);
        }
        refreshUi();
    }

    class ExcuteCaseTask extends AsyncTask<Integer, Integer, String> {
        private WakeLock wakeLock = null;

        @Override
        protected String doInBackground(Integer... params) {
            Log.d(TAG, "ExcuteCaseTask");
            acquireWakeLocak();
            /**
             * 一次任务执行完后执行次数+1
             */
            if (Utils.getTestMode(BillieJeanService.this) == BillieJeanConfig.TEST_RESET_FACTORY_TEST) {
                try {
                    int i = Utils.readerFile(BillieJeanConfig.RESET_FLAG);
                    int totalCount = Utils.readerFile(BillieJeanConfig.RESET_FACTORY_FLAG);
                    Utils.putTestCount(BillieJeanService.this, i + 1);
                    Utils.putTotalCount(BillieJeanService.this, totalCount);
                    refreshUi();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            } else {
                Log.d(TAG, "current count ==========" + String.valueOf(Utils.getTestCount(BillieJeanService.this)));
                Utils.putTestCount(BillieJeanService.this, Utils.getTestCount(BillieJeanService.this) + 1);
                refreshUi();
            }

            /**
             * 执行测试
             */
            Utils.putInfoText(BillieJeanService.this, BillieJeanService.this.getString(R.string.test_state_running));
            if (action == null) {
                Log.e(TAG, "action is null!");
                return null;
            }
            action.execute(BillieJeanService.this);
            if (action.isSingleCase()) {
                return null;
            }
            refreshUi();
            Log.d(TAG,"=========================== in the Doing Background =========================");
            executeCase();
            releaseWakeLock();
            return null;
        }

        private void acquireWakeLocak() {
            PowerManager pm = (PowerManager) BillieJeanService.this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                    BillieJeanService.TAG);
            if (wakeLock != null) {
                wakeLock.acquire();
            }
        }

        private void releaseWakeLock() {

            if (wakeLock != null) {
                wakeLock.release();
                wakeLock = null;
            }
        }
    }

    // 获取上次执行的测试项
    private void makeAction() {
        if (Utils.getTestMode(this) != BillieJeanConfig.TEST_RESET_FACTORY_TEST) {
            Utils.removeResetFlag();
        }
        switch (Utils.getTestMode(this)) {
            case BillieJeanConfig.TEST_CASE_REBOOT:
                action = new RebootCase();
                break;
//            case BillieJeanConfig.TEST_CASE_CPU_STRESS:
//                action = new CpuStressCase();
//                break;
            case BillieJeanConfig.TEST_CASE_SCREEN_POWER:
                action = new ScreenOnOffCase();
                break;
            case BillieJeanConfig.TEST_CASE_LCD_TEST:
                action = new LCDCase();
                break;
            case BillieJeanConfig.TEST_POWER_OFF_ON:
                action = new PowerOnOffCase();
                break;
            case BillieJeanConfig.TEST_RESET_FACTORY_TEST:
                action = new ResetFactoryCase();
                break;
            case BillieJeanConfig.TEST_CAMERA_TEST:
                action = new CameraCase();
                break;
//            case BillieJeanConfig.TEST_Top1000_TEST:
//                action = new Top1000Case();
//                break;
//            case BillieJeanConfig.TEST_OtaUp_TEST:
//                action = new OtaUpCase();
//                break;
            default:
                break;
        }
    }

    private boolean checkBattery() {
        boolean flag = false;
        if (Utils.getBatteryLevel(this) > 30) {
            flag = true;
        } else if (Utils.getBatteryLevel(this) <= 0) {
            flag = false;
        } else {
            flag = false;
            Utils.putInfoText(this, getString(R.string.battery_info_low) + '\n' + getString(R.string.test_state_pause));
            Utils.putIsRunning(this, false);
        }
        return flag;
    }

    private boolean checkTargetCount() {
        boolean flag = false;
        if (Utils.getTestMode(BillieJeanService.this) == BillieJeanConfig.TEST_RESET_FACTORY_TEST) {
            try {
                int totalCount = Utils.readerFile(BillieJeanConfig.RESET_FACTORY_FLAG);
                int i = Utils.readerFile(BillieJeanConfig.RESET_FLAG);
                Log.d("res_factory_test", totalCount + " " + i);
                if (totalCount == -1 || totalCount - i > 0) {
                    flag = true;
                } else {
                    Utils.putInfoText(this, getString(R.string.test_complete));
                    flag = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Total_Count=" + String.valueOf(Utils.getTotalCount(this)));
            Log.d(TAG, "Current_Count=" + String.valueOf(Utils.getTestCount(this)));
            if (Utils.getTotalCount(this) == -1 || Utils.getTotalCount(this) - Utils.getTestCount(this) > 0) {
                Log.d(TAG, "in running");
                flag = true;
            } else {
                Log.d(TAG, "in finished");
                flag = false;
                Utils.putIsRunning(this, false);
                Utils.putInfoText(this, getString(R.string.test_complete));
                refreshUi();
            }
        }
        Log.d(TAG, "CheckCount-Flag = " + String.valueOf(flag));
        return flag;

    }

    public boolean checkRestartCount() {
        boolean flag = false;
        if (Utils.getRestartCount(this) > 0) {
            Log.d(TAG, "BillieJeanService_this" + Utils.getRestartCount(this));
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    public int ret() {
        int re = Utils.getRestartCount(this) * 1000;
        Log.d(TAG, "re" + re);
        Log.d(TAG, "this" + this);
        return re;
    }

    private boolean checkIsRunning() {
        boolean flag = Utils.getIsRunning(this);
        Log.d(TAG, "check_isRunning");
        Log.d(TAG, "IsRunning_flag = " + String.valueOf(flag));
        if (flag) {
            // Utils.putInfoText(this, getString(R.string.battery_info_low));
        } else {

            if (checkTargetCount()) {
                Utils.putInfoText(this, getString(R.string.test_state_stop));
            } else {

            }
            // Utils.removeResetFlag();
        }
        Log.d(TAG, "ret_isRunning=" + String.valueOf(flag));
        return flag;
    }

    public void setUiRefreshListener(UIRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new BillieJeanBinder();
    }

    class BillieJeanBinder extends Binder {
        public BillieJeanService getService() {
            return BillieJeanService.this;
        }
    }

    interface UIRefreshListener {
        public void refresh();
    }

    class BatteryMonitorReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "action == " + intent.getAction());
            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int level = 100; // percentage, or -1 for unknown
            if (rawlevel >= 0 && scale > 0) {
                level = (rawlevel * 100) / scale;
            }
            Log.d(TAG, "rawlevel=" + rawlevel);
            Log.d(TAG, "scale=" + scale);
            Log.d(TAG, "status=" + status);
            Log.d(TAG, "health=" + health);
            Log.d(TAG, "level=" + level);
            Utils.putBatteryLevel(context, level);
            Utils.putBatteryState(context, status);
        }

        public Intent regist(Context context) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            filter.addAction(Intent.ACTION_BATTERY_LOW);
            filter.addAction(Intent.ACTION_BATTERY_OKAY);
            return context.registerReceiver(this, filter);
        }

        public void unRgist(Context context) {
            context.unregisterReceiver(this);
        }
    }

    /**
     * Monitor operations happening in the system.
     */

}
