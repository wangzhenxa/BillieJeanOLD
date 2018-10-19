package com.ontim.billiejean;

import android.app.Application;
import android.content.Context;
/**
 * App
 */
public class App extends Application {
    public static Context ctx;
    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
    }
}