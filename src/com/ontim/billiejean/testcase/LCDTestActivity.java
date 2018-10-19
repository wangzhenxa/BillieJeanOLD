package com.ontim.billiejean.testcase;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.ontim.billiejean.BillieJeanConfig;
import com.ontim.billiejean.Utils;

public class LCDTestActivity extends Activity {
    private static final String TAG = BillieJeanConfig.PRO_TAG
            + "LCDTestActivity";

    private View view;

    private static final int[] sColorSequence = new int[] { Color.RED,
            Color.GREEN, Color.BLUE, Color.WHITE, Color.BLACK };
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "  onCreate");
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set to full-screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        view = new View(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case BillieJeanConfig.HANDLE_LCD_COLOR:
                        view.setBackgroundColor(message.arg1);
                        view.invalidate();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    class LCDTestTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            Log.i(TAG, "LCDTestTask");
            while (Utils.getIsRunning(LCDTestActivity.this)) {
                for (int i = 0; i < sColorSequence.length; i++) {
                    Log.i(TAG, "set color" + sColorSequence[i]);
                    try {
                        Message message = mHandler.obtainMessage();
                        message.what = BillieJeanConfig.HANDLE_LCD_COLOR;
                        message.arg1 = sColorSequence[i];
                        mHandler.sendMessage(message);
                        Thread.sleep(BillieJeanConfig.LCD_DELAY_TIME);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "", e);
                    }
                }
            }
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "on resume");
        new LCDTestTask().execute(1);
        setContentView(view);
        view.setBackgroundColor(Color.GREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "on pause");
        finish();
    }

}
