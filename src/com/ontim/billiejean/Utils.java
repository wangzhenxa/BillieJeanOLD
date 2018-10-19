package com.ontim.billiejean;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.os.PowerManager.WakeLock;
import android.os.storage.StorageManager;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import android.util.Log;

public class Utils {
    private static final String TAG = BillieJeanConfig.PRO_TAG + "Utils";
    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(
                BillieJeanConfig.PREFS.NODE_PREP_NAME, 0);
    }

    public static int getTestMode(Context context) {
        return getInt(context, BillieJeanConfig.PREFS.NODE_TEST_CASE,
                BillieJeanConfig.TEST_CASE_NONE);
    }

    public static void putTestMode(Context context, int mode) {
        putInt(context, BillieJeanConfig.PREFS.NODE_TEST_CASE, mode);
    }
    public static void putIsLimited(Context context, Boolean status) {
        putBoolean(context, BillieJeanConfig.PREFS.NODE_TEST_LIMITED, status);
    }
    public static void putTestCountPre(Context context,int num) {
        putInt(context,BillieJeanConfig.PREFS.NODE_TEST_COUNT_PRE,num);
    }
    public static int getTestCountPre(Context context){
        return getInt(context,BillieJeanConfig.PREFS.NODE_TEST_COUNT_PRE);
    }
    public static void putTotalCountEdit(Context context, String num) {
        putString(context, BillieJeanConfig.PREFS.NODE_TEST_COUNT_EDIT, num);
    }
    public static String getTotalCountEdit(Context context) {
        return getString(context, BillieJeanConfig.PREFS.NODE_TEST_COUNT_EDIT);
    }
    public static Boolean getIsLimited(Context context) {
        return getBoolean(context, BillieJeanConfig.PREFS.NODE_TEST_LIMITED);
    }

    public static void putTestCount(Context context, int count) {
        putInt(context, BillieJeanConfig.PREFS.NODE_TEST_COUNT, count);
    }

    public static void putTotalCount(Context context, int count) {
        putInt(context, BillieJeanConfig.PREFS.NODE_TOTAL_COUNT, count);
    }
    
    public static void putRestartCount(Context context, int count) {
        putInt(context, BillieJeanConfig.PREFS.NODE_RESTART_COUNT, count);
    }

    public static void putSelectedCase(Context context, int mode) {
        putInt(context, BillieJeanConfig.PREFS.NODE_TEST_CASE_SELECTED, mode);
    }

    public static void putBatteryLevel(Context context, int level) {
        putInt(context, BillieJeanConfig.PREFS.NODE_BATTERY_LEVEL, level);
    }

    public static void putBatteryState(Context context, int state) {
        putInt(context, BillieJeanConfig.PREFS.NODE_BATTERY_STATE, state);
    }

    public static void putInfoText(Context context, String alert) {
        putString(context, BillieJeanConfig.PREFS.NODE_INFO_TEXT, alert);
    }

    public static void putIsRunning(Context context, boolean isRunning) {
        putBoolean(context, BillieJeanConfig.PREFS.NODE_IS_RUNNING, isRunning);
    }

    public static void putInt(Context context, String prefKey, int value) {
        Log.d(TAG, "put " + prefKey + " value " + value);
        Editor edit = getPrefs(context).edit();
        edit.putInt(prefKey, value);
        edit.apply();
        edit.commit();
    }

    public static void putString(Context context, String prefKey, String value) {
        Log.d(TAG, "put " + prefKey + " value " + value);
        Editor edit = getPrefs(context).edit();
        edit.putString(prefKey, value);
        edit.apply();
        edit.commit();
    }

    public static void putBoolean(Context context, String prefKey, boolean value) {
        Log.d(TAG, "put " + prefKey + " value " + value);
        Editor edit = getPrefs(context).edit();
        edit.putBoolean(prefKey, value);
        edit.apply();
        edit.commit();
    }

    public static boolean getIsRunning(Context context) {
        return getBoolean(context, BillieJeanConfig.PREFS.NODE_IS_RUNNING, false);
    }

    public static int getTotalCount(Context context) {
        return getInt(context, BillieJeanConfig.PREFS.NODE_TOTAL_COUNT, -2);
    }
    
    public static int getRestartCount(Context context) {
        Log.d(TAG,"Utils_context"+context);
        return getInt(context, BillieJeanConfig.PREFS.NODE_RESTART_COUNT);
    }

    public static int getTestCount(Context context) {
        return getInt(context, BillieJeanConfig.PREFS.NODE_TEST_COUNT);
    }

    public static int getBatteryLevel(Context context) {
        return getInt(context, BillieJeanConfig.PREFS.NODE_BATTERY_LEVEL);
    }

    public static int getBatteryState(Context context) {
        return getInt(context, BillieJeanConfig.PREFS.NODE_BATTERY_STATE);
    }

    public static int getCaseSelected(Context context) {
        if (Utils.hasResetFactoryCase()) {
            putTestMode(context, BillieJeanConfig.TEST_RESET_FACTORY_TEST);
        }
        return getInt(context, BillieJeanConfig.PREFS.NODE_TEST_CASE_SELECTED);
    }

    private static int getInt(Context context, String prefKey) {
        return getInt(context, prefKey, 0);
    }

    private static int getInt(Context context, String prefKey, int defValue) {
        return getPrefs(context).getInt(prefKey, defValue);
    }

    private static boolean getBoolean(Context context, String prefKey) {
        return getBoolean(context, prefKey, false);
    }

    private static boolean getBoolean(Context context, String prefKey,
            boolean defValue) {
        return getPrefs(context).getBoolean(prefKey, defValue);
    }

    public static String getInfoText(Context context) {
        return getString(context, BillieJeanConfig.PREFS.NODE_INFO_TEXT);
    }

    private static String getString(Context context, String prefKey) {
        return getString(context, prefKey, "");
    }

    private static String getString(Context context, String prefKey,
            String defValue) {
        return getPrefs(context).getString(prefKey, defValue);
    }

    public static void installTop1000(String apkPath) throws IOException {
        excuteCommand(BillieJeanConfig.Top1000_INSTALL + apkPath);
    }
    
    public static void uninstallTop1000(Context context, String apkPath) throws IOException {
        excuteCommand(BillieJeanConfig.Top1000_UNINSTALL + getPackageName(apkPath, context));
    }
    
    public static void startApp(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        Intent i = pm.getLaunchIntentForPackage(getPackageName(apkPath, context));//获取启动的包名
        context.startActivity(i);
    }
    
    public static String getPackageName(String absPath,Context context) {
        
        String packageName = "";
        
        PackageManager pm = context.getPackageManager(); 
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath,PackageManager.GET_ACTIVITIES); 
        if (pkgInfo != null) { 
            ApplicationInfo appInfo = pkgInfo.applicationInfo; 
            packageName = appInfo.packageName; // 得到包名 
            Log.d(TAG,"packageName==" + packageName);
        }
        return packageName;
    }
    
    public static String getVersion(String absPath,Context context) {
        
        String version = "";
        
        PackageManager pm = context.getPackageManager(); 
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath,PackageManager.GET_ACTIVITIES); 
        if (pkgInfo != null) { 
            ApplicationInfo appInfo = pkgInfo.applicationInfo; 
            appInfo.sourceDir = absPath; 
            appInfo.publicSourceDir = absPath;
            version = pkgInfo.versionName; // 得到版本信息  
            Log.d(TAG,"version==" + version);
        }
        return version;
    }
    
    public static String getAppName(String absPath,Context context) {
        
        String appName = "";
        
        PackageManager pm = context.getPackageManager(); 
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath,PackageManager.GET_ACTIVITIES); 
        if (pkgInfo != null) { 
            ApplicationInfo appInfo = pkgInfo.applicationInfo; 
            appInfo.sourceDir = absPath; 
            appInfo.publicSourceDir = absPath;
            appName = pm.getApplicationLabel(appInfo).toString();// 得到应用名  
            Log.d(TAG,"appName==" + appName);
        }
        return appName;
    }
    
    public static boolean isSdCardExist() {  
    return Environment.getExternalStorageState().equals(  
            Environment.MEDIA_MOUNTED);  
    }
    
    public static void writeFile(String s, String fileName) throws IOException {
        try {  
            File file = new File(Environment.getExternalStorageDirectory(),  
                    fileName);  
            //第二个参数意义是说是否以append方式添加内容  
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));  
            //String info = s;  
            bw.write(s);
            bw.write("\r\n");
            bw.flush();  
            System.out.println("写入成功");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
    }
    
    public static void writeReaderFile(String str,String s) throws IOException {
        
        try {  
            File file = new File(str);
            //deleteFile();
            Log.d(TAG,"file== " + file);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));  
            Log.d(TAG,"bw== " + bw);
            //String info = s;  
            Log.d(TAG,"s== " + s);
            bw.write(s);
            bw.write("\r\n");
            bw.flush();  
            System.out.println("写入成功");
            Log.d(TAG,"写入完成");
        } catch (Exception e) {  
            e.printStackTrace();  
        }
    }
    
    public static int readerFile(String str) throws IOException {
        FileReader fr =  new FileReader (str);
        BufferedReader br = new BufferedReader (fr); 
        String s = br.readLine();
        Log.d(TAG,"ssss=== " + s);
        fr.close();
        int i=0;
        if (s!=null) {
            i =Integer.parseInt(s);
        }
        return i;
    }
    
    /**
     * 删除TOP APP测试结果
     */
    
    public static void deleteFile() {
        File file = new File(Environment.getExternalStorageDirectory(), BillieJeanConfig.FILE_NAME);
        Log.d(TAG, "file = " + file.getPath().toString());
        if (file.exists()) { // 判断文件是否存在
            file.delete();
        } else {
            Log.e(TAG, "文件不存在");
        }
    }
    
    /** 
     * 判断应用是否已安装 
     *  
     * @param context 
     * @param packageName 
     * @return 
     */  
    public static boolean isInstalled(Context context, String packageName) {  
        boolean hasInstalled = false;  
        PackageManager pm = context.getPackageManager();  
        List<PackageInfo> list = pm  
                .getInstalledPackages(PackageManager.PERMISSION_GRANTED);  
        for (PackageInfo p : list) {  
            if (packageName != null && packageName.equals(p.packageName)) {  
                hasInstalled = true;  
                break;  
            }  
        }  
        return hasInstalled;  
    }
    
    public static void renameAPP(String oldPath, String newPath) {
        File file = new File(oldPath);
        boolean newAppPath = file.renameTo(new File(newPath));
        Log.d(TAG,"newAppPath==" + newAppPath);
    }


    public static void excuteCpuStress() throws IOException {
        excuteCommand(String.format(BillieJeanConfig.CPU_STRESS_SHELL_COMMAND,
                BillieJeanConfig.CPU_STRESS_LOG_FILE));
        Log.d(TAG,"CPU== " + String.format(BillieJeanConfig.CPU_STRESS_SHELL_COMMAND,
                BillieJeanConfig.CPU_STRESS_LOG_FILE));
    }

    public static String excuteCommand(String command) throws IOException {
        Process rt;
        DataOutputStream os = null;
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        try {
            Log.d(TAG, "excute ======= " + command);
            rt = Runtime.getRuntime().exec(command);
            br = new BufferedReader(new InputStreamReader(rt.getInputStream()));
            String temp = "";
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
            Log.d(TAG, "sb == " + sb.toString());
            os = new DataOutputStream(rt.getOutputStream());
            os.flush();
            os.writeBytes("exit\n");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

    public static boolean isRunning(String packageName) {
        List<RunningAppProcessInfo> appProcesses = null;
        try {
            appProcesses = ActivityManagerNative.getDefault().getRunningAppProcesses();
        } catch (RemoteException e) {
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return false;
        }
        if (appProcesses == null) {
            return false;
        }
        for (RunningAppProcessInfo appProcess : appProcesses) {
            Log.w(TAG, "isRunning  appProcess.processName = "
                    + appProcess.processName);
            if (appProcess.processName.contains(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStressAppRunning(String process) {
        List<RunningAppProcessInfo> appProcesses = null;
        try {
            appProcesses = ActivityManagerNative.getDefault()
                    .getRunningAppProcesses();
        } catch (RemoteException e) {
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return false;
        }
        if (appProcesses == null) {
            return false;
        }
        for (RunningAppProcessInfo appProcess : appProcesses) {
            Log.w(TAG, "isRunning  appProcess.processName = "
                    + appProcess.processName);
            if (appProcess.processName.contains(process)) {
                return true;
            }
        }
        return false;
    }

    // 开启轮询服务
    public static void startPollingService(Context context, int seconds,
            Class<?> cls, String action) {
        // 获取AlarmManager系统服务
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        // 包装需要执行Service的Intent
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 触发服务的起始时间
        long triggerAtTime = SystemClock.elapsedRealtime();

        // 使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtTime, seconds * 1000, pendingIntent);
    }

    // 停止轮询服务
    public static void stopPollingService(Context context, Class<?> cls,
            String action) {
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 取消正在执行的服务
        manager.cancel(pendingIntent);
    }

    public static WakeLock acquireWakeLock(Context context) {
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, "AutoTestService");
        if (null != wakeLock) {
            wakeLock.acquire();
        }
        return wakeLock;
    }

    // 释放设备电源锁
    public static void releaseWakeLock(WakeLock wakeLock) {
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "CameraStress");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile = null;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static String getTestCaseStr(Context context, int caseId) {
        String testCaseStr = null;
        switch (caseId) {
            case BillieJeanConfig.TEST_CASE_REBOOT:
                testCaseStr = context.getString(R.string.title_reboot_test);
                break;
//            case BillieJeanConfig.TEST_CASE_CPU_STRESS:
//                testCaseStr = context.getString(R.string.title_cpu_stress_test);
//                break;
            case BillieJeanConfig.TEST_CASE_SCREEN_POWER:
                testCaseStr = context
                        .getString(R.string.title_screen_power_test);
                break;
            case BillieJeanConfig.TEST_CASE_LCD_TEST:
                testCaseStr = context.getString(R.string.title_lcd_test);
                break;
            case BillieJeanConfig.TEST_POWER_OFF_ON:
                testCaseStr = context
                        .getString(R.string.title_power_off_on_test);
                break;
            case BillieJeanConfig.TEST_RESET_FACTORY_TEST:
                testCaseStr = context
                        .getString(R.string.title_reset_factory_test);
                break;
            case BillieJeanConfig.TEST_CAMERA_TEST:
                testCaseStr = context.getString(R.string.title_camera_test);
                break;
//            case BillieJeanConfig.TEST_Top1000_TEST:
//                testCaseStr = context
//                        .getString(R.string.title_Top1000_test);
//                break;
            case BillieJeanConfig.TEST_CASE_NONE:
            default:
                testCaseStr = context.getString(R.string.title_none);
                break;
        }
        return testCaseStr;
    }

    public static boolean hasResetFactoryCase() {
        return BillieJeanConfig.RESET_FACTORY_FLAG_FILE.exists();
    }

    public static boolean removeResetFlag() {
        boolean flag = false;
        if (BillieJeanConfig.RESET_FACTORY_FLAG_FILE.exists()) {
            flag = BillieJeanConfig.RESET_FACTORY_FLAG_FILE.delete();
        }
        File fileTestCount = new File(BillieJeanConfig.RESET_FLAG);
        File fileTotalCount = new File(BillieJeanConfig.RESET_FACTORY_FLAG);
        if(fileTestCount.exists()){
            flag=fileTestCount.delete();
        }
        if(fileTotalCount.exists()){
            flag=fileTotalCount.delete();
        }
        return flag;
    }

    public static void createResetFlag() throws IOException {
        new FileOutputStream(BillieJeanConfig.RESET_FACTORY_FLAG_FILE);
        //return BillieJeanConfig.RESET_FACTORY_FLAG_FILE.createNewFile();
    }

    /*
      * Three possible return value for this method: emmc ; sdcard ; none When
      * property "persist.sys.switch_storage" does not exist, the return value
      * would be "none"
      */

    public static String getPrimary() {
        String property = SystemProperties.get("persist.sys.switch_storage",
                "none,0");
        String[] split = property.split(",");
        if (split.length != 2)
            return "none";
        return split[0];
    }
    
    
    
    public static List<String> getApkPath(final String strPath) {
        List<String> list = new ArrayList<String>();
        
        File file = new File(strPath);
        Log.d(TAG,"getApkPath_file:"+file);
        File[] files = file.listFiles();
        Log.d(TAG,"getApkPath_files:"+files);
        
        if (files == null) {
            return null;
        }
        Log.d(TAG,"getApkPath_files.length:" + files.length);
        for(int i = 0; i < files.length; i++) {
            final File f = files[i];
            Log.d(TAG,"getApkPath_f:" + f);
            if(f.isFile()) {
                try{
                    int idx = f.getPath().lastIndexOf(".");
                    Log.d(TAG,"getApkPath_idx:" + idx);
                    if (idx <= 0) {
                        continue;
                    }
                    String suffix = f.getPath().substring(idx);
                    Log.d(TAG,"getApkPath_suffix:" + suffix);
                    if (suffix.toLowerCase().equals(".apk"))
                    {
                        list.add(f.getPath());
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        return list;
    }
    

    
    public static void alarm_flag_setup(long onTime) {
        Log.d(TAG,"ALARM_FLAG_FILE" + BillieJeanConfig.ALARM_FLAG_FILE);
        File alarmFlagFile = BillieJeanConfig.ALARM_FLAG_FILE;
        Calendar c = Calendar.getInstance();
        c.set(2012, 0, 1, 0, 0, 0);
        Calendar to = Calendar.getInstance();
        to.setTimeInMillis(onTime);
        TimeZone zone = c.getTimeZone();
        long dstOffset = zone.getOffset(onTime);
        long startTimeInMillis = c.getTimeInMillis();
        long dstonTime = onTime - dstOffset;
        long timeDiffInMillis = dstonTime - startTimeInMillis;
        long timeDiffInSecs = timeDiffInMillis/1000;

        //Log.v("write " + String.valueOf(timeDiffInSecs) + " to" + alarmFlagFile);

        if (alarmFlagFile.exists()) {
            //Log.v(alarmFlagFile + " already exist, delete it");
            try {
                alarmFlagFile.delete();
                //Log.v(ALARM_FLAG_FILE + " delete before write success");
            } catch (Exception e) {
                //Log.v(ALARM_FLAG_FILE + " delete before write failed");
            }
        }

        FileOutputStream command = null;
        try {
            command = new FileOutputStream(alarmFlagFile);
            command.write(String.valueOf(timeDiffInSecs).getBytes());
            command.write("\n".getBytes());
            command.write(String.valueOf(onTime / 1000).getBytes());
            command.write("\n".getBytes());

            command.flush();
            command.getFD().sync();
            command.close();
            //Log.v(ALARM_FLAG_FILE + " write done");
            command = null;
        } catch (Exception e) {
            //Log.v(ALARM_FLAG_FILE + " write error : " + e.getMessage());
        } finally {
            if (null != command) {
                try {
                    command.close();
                } catch (IOException e) {
                    //Log.v("FileOutputStream close error : " + e.getMessage());
                }
            }
        }
    }
     /**
     * @param is_removale true 外置
     *                    false 内置
     *                    获取外置/内置内存卡的地址
     */
public static String getStoragePath(Context mContext,boolean is_removale) {
    StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
    Class<?> storageVolumeClazz = null;
    
    try {
        storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
        Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
        Method getPath = storageVolumeClazz.getMethod("getPath");
        Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
        Object result = getVolumeList.invoke(mStorageManager);
        final int length = Array.getLength(result);
        for (int i = 0; i < length; i++) {
            Object storageVolumeElement = Array.get(result, i);
            String path = (String) getPath.invoke(storageVolumeElement);
            boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
            if (is_removale == removable) {
                Log.d(TAG,"ExternalDtorage = "+path);
                return path;
            }
        }
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    } catch (InvocationTargetException e) {
        e.printStackTrace();
    } catch (NoSuchMethodException e) {
        e.printStackTrace();
    } catch (IllegalAccessException e) {
        e.printStackTrace();
    }
    return null;
}
}
