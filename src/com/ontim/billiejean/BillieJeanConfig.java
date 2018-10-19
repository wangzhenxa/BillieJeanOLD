package com.ontim.billiejean;

import java.io.File;



public class BillieJeanConfig {
    public static final String PRO_TAG = "BJ-";
    private static final String path = Utils.getStoragePath(App.ctx, true);

    public static final String ACTION_BASE = "Billiejean_";
    public static final String ACTION_BEGIN_CASE_BOOT_START = ACTION_BASE + "boot.start";
    public static final String ACTION_BEGIN_CASE_MOUNT_START = ACTION_BASE + "mount.start";
    public static final String ACTION_BEGIN_CASE_UNMOUNT_START = ACTION_BASE + "unmount.start";
    public static final String ACTION_SERVICE_BIND = ACTION_BASE + ".bind";

    public static final String CPU_STRESS_SHELL_COMMAND = "/data/local/tmp/stressapptest -s 99999 -M 512 -m 4 -C 4 -W";
    public static final String CPU_STRESS_LOG_FILE = "/data/local/tmp/cpustress_log/";
    public static final File RESET_FACTORY_FLAG_FILE = new File(path+"/reset_test");
    public static final String Top1000_INSTALL = "pm install -r ";
    public static final String Top1000_UNINSTALL = "pm uninstall ";
    public static final String FILE_NAME = "TopLog.txt";
    
    public static final String RESET_FLAG = path+"/Reset_Count.txt";
    public static final String RESET_FACTORY_FLAG = path+"/reset_test.txt";
    
    public static final File ALARM_FLAG_FILE = new File(path+"/poweron_timeinmillis");

    public static final int TEST_CASE_NONE = -1;
    public static final int TEST_CASE_REBOOT = 0;
    public static final int TEST_CASE_SCREEN_POWER = 1;
//    public static final int TEST_CASE_CPU_STRESS = 2;
    public static final int TEST_CASE_LCD_TEST = 2;
    public static final int TEST_POWER_OFF_ON = 3;
    public static final int TEST_RESET_FACTORY_TEST = 4;
    public static final int TEST_CAMERA_TEST = 5;
//    public static final int TEST_Top1000_TEST = 7;
//    public static final int TEST_OtaUp_TEST = 8;

    public static final int HANDLE_UI_REFRESH = 1;
    public static final int HANDLE_LCD_COLOR = 1;
    public static final String EXTRA_LCD_COLOR = "lcd_color";

    public static final int STARTAPP_WAIT_TIME = 5 * 1000;
    public static final int RUNAPP_WAIT_TIME = 20 * 1000;
    public static final int REBOOT_WAIT_TIME = 20 * 1000;
    public static final int SCREEN_ON_OFF_WAIT_TIME = 5 * 1000;
    public static final int CPU_STRESS_ROLL_TIME = 10 * 1000;
    public static final int LCD_DELAY_TIME = 5 * 1000;
    public static final int POWER_ON_WAIT_TIME = 25 * 1000;
    public static final int POWER_OFF_WAIT_TIME = 4 * 60 * 1000;
    public static final String BillieJean_Service = "com.ontim.billiejean.BillieJeanService";
    public static final String PACKAGE_NAME = "com.ontim.billiejean";

    public final static class PREFS {
        public static final String NODE_TEST_LIMITED = "limited_status";
        public static final String NODE_TEST_COUNT_EDIT = "count_exit";
        public static final String NODE_PREP_NAME = "BillieJean";
        // public static final String NODE_TEST_CASE_EXECUTING = "test_case_executing";
        public static final String NODE_TEST_CASE = "test_case";
        public static final String NODE_TEST_COUNT = "test_count";
        public static final String NODE_TOTAL_COUNT = "total_times";
        public static final String NODE_RESTART_COUNT = "restart_times";
        public static final String NODE_TEST_COUNT_PRE = "test_count_pre";
        public static final String NODE_INFO_TEXT = "test_info_text";
        public static final String NODE_INFO_BATTERY_STATE = "battery_info_text";
        public static final String NODE_IS_RUNNING = "is_running";
        public static final String NODE_TEST_CASE_SELECTED = "test_case_selected";
        public static final String NODE_BATTERY_LEVEL = "battery_level";
        public static final String NODE_BATTERY_STATE = "battery_state";
    }
}
