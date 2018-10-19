package com.ontim.billiejean;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.ontim.billiejean.BillieJeanService.BillieJeanBinder;
import com.ontim.billiejean.BillieJeanService.UIRefreshListener;

import java.io.IOException;

import static com.ontim.billiejean.Utils.putIsLimited;
import static com.ontim.billiejean.Utils.writeReaderFile;

public class BillieJean extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static final String TAG = BillieJeanConfig.PRO_TAG + "BillieJean";

    /**
     * Fragment managing the behaviors, interactions and presentation of the
     * navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private PlaceholderFragment placeholderFragment = null;
    private static BillieJeanService billieJeanService = null;
    private Handler mHandler = null;
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            billieJeanService.setUiRefreshListener(null);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            billieJeanService = ((BillieJeanBinder) service).getService();
            billieJeanService.setUiRefreshListener(new UIRefreshListener() {
                @Override
                public void refresh() {
                    Log.d(TAG, "refresh");
                    Message message = mHandler.obtainMessage();
                    message.what = BillieJeanConfig.HANDLE_UI_REFRESH;
                    mHandler.sendMessage(message);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mHandler = new BillieJeanHandler();
        Intent intent = new Intent(BillieJeanConfig.BillieJean_Service);
        intent.setPackage(BillieJeanConfig.PACKAGE_NAME);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        billieJeanService.setUiRefreshListener(null);
        unbindService(conn);
        super.onDestroy();
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        placeholderFragment = PlaceholderFragment.newInstance(position);
        fragmentManager.beginTransaction().replace(R.id.container, placeholderFragment).commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case BillieJeanConfig.TEST_CASE_REBOOT:
                mTitle = getString(R.string.title_reboot_test);
                break;
            case BillieJeanConfig.TEST_CASE_SCREEN_POWER:
                mTitle = getString(R.string.title_screen_power_test);
                break;
            case BillieJeanConfig.TEST_CASE_LCD_TEST:
                mTitle = getString(R.string.title_lcd_test);
                break;
//            case BillieJeanConfig.TEST_CASE_CPU_STRESS:
//                mTitle = getString(R.string.title_cpu_stress_test);
//                break;
            case BillieJeanConfig.TEST_POWER_OFF_ON:
                mTitle = getString(R.string.title_power_off_on_test);
                break;
            case BillieJeanConfig.TEST_RESET_FACTORY_TEST:
                mTitle = getString(R.string.title_reset_factory_test);
                break;
            case BillieJeanConfig.TEST_CAMERA_TEST:
                mTitle = getString(R.string.title_camera_test);
                break;
//            case BillieJeanConfig.TEST_Top1000_TEST:
//                mTitle = getString(R.string.title_Top1000_test);
//                break;
//            case BillieJeanConfig.TEST_OtaUp_TEST:
//                mTitle = getString(R.string.title_OtaUp_test);
//                break;
            default:
                mTitle = getString(R.string.title_none);
                break;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private TextView testCaseView;
        private TextView testCountView;
        private TextView totalCountView;

        private TextView testInfoTextView;

        private TextView selectCaseView;

        private Button beginTestButton;
        private Button resetInfoButton;
        private Button stopTestButton;

        private CheckBox runLimitCheck;
        private EditText totalCountEdit;
        private EditText intervalTimesEdit;
        private LinearLayout setTimesLayout;

        private int ret;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i(TAG, "PlaceholderFragment onCreate");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Utils.putSelectedCase(getActivity(), getArguments().getInt(ARG_SECTION_NUMBER));
            initView(rootView);
            fillData();
            return rootView;
        }

        private void initView(View view) {
            testCaseView = (TextView) view.findViewById(R.id.test_case);
            testCountView = (TextView) view.findViewById(R.id.test_count);
            totalCountView = (TextView) view.findViewById(R.id.total_count);

            testInfoTextView = (TextView) view.findViewById(R.id.test_info_text);
            selectCaseView = (TextView) view.findViewById(R.id.case_selected_text);

            beginTestButton = (Button) view.findViewById(R.id.begin_test);
            resetInfoButton = (Button) view.findViewById(R.id.reset_info);
            stopTestButton = (Button) view.findViewById(R.id.stop_test);
            runLimitCheck = (CheckBox) view.findViewById(R.id.run_limit_check);
            /**
             * Begin Button Click events
             */
            beginTestButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.i(TAG, "beginTestButton onClick");
                    Utils.removeResetFlag();
                    Utils.putTestMode(getActivity(), Utils.getCaseSelected(PlaceholderFragment.this.getActivity()));
                    Utils.putIsRunning(getActivity(), true);
                    setTargetCount();
                    setIntervalTimesCount();
                    resetTestData();
                    fillData();
                    // 删除Top App 测试产生的数据
//                    if (Utils.getTestMode(getActivity()) == BillieJeanConfig.TEST_Top1000_TEST) {
//                        Utils.deleteFile();
//                    }
                    Utils.putInfoText(getActivity(), getString(R.string.test_state_running));
                    // start services
                    final Intent intent = new Intent();
                    intent.setClass(getActivity(), BillieJeanService.class);
                    intent.setPackage(BillieJeanConfig.PACKAGE_NAME);
                    getActivity().startService(intent);
                    beginTestButton.setClickable(false);
                }
            });
            /**
             * ResetInfo Button Click events
             */
            resetInfoButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.i(TAG, "resetInfoButton onClick");
                    resetTestData();
                    // stop service
                    // final Intent intent = new Intent();
                    // intent.setClass(getActivity(), BillieJeanService.class);
                    // intent.setPackage(BillieJeanConfig.PACKAGE_NAME);
                    // getActivity().stopService(intent);
                    fillData();
                    // 删除Top App 测试产生的数据
//                    if (Utils.getTestMode(getActivity()) == BillieJeanConfig.TEST_Top1000_TEST) {
//                        Utils.deleteFile();
//                    }
                    beginTestButton.setClickable(true);
                }
            });
            /**
             * Stop Button Click events
             */
            stopTestButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.i(TAG, "stopTestButton onClick");
                    Utils.putIsRunning(getActivity(), false);
                    Utils.removeResetFlag();
                    // stop service
                    // final Intent intent = new Intent(getActivity(),BillieJeanService.class);
                    // // intent.setClass(getActivity(), BillieJeanService.class);
                    // // intent.setPackage(BillieJeanConfig.PACKAGE_NAME);
                    // getActivity().stopService(intent);
                    Log.i(TAG, "after fillData");
                    beginTestButton.setClickable(true);
                }
            });

            /**
             * run times check
             */
            runLimitCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.i(TAG, "runLimitCheck onCheckedChanged");
                    if (isChecked) {
                        setTimesLayout.setVisibility(View.GONE);
                        putIsLimited(getContext(),true);
                    } else {
                        setTimesLayout.setVisibility(View.VISIBLE);
                        putIsLimited(getContext(),false);

                    }
                }
            });
            totalCountEdit = (EditText) view.findViewById(R.id.total_count_edit);
            intervalTimesEdit = (EditText) view.findViewById(R.id.interval_time);

            setTimesLayout = (LinearLayout) view.findViewById(R.id.set_times_layout);
            if (runLimitCheck.isChecked()) {
                setTimesLayout.setVisibility(View.GONE);
            } else {
                setTimesLayout.setVisibility(View.VISIBLE);
            }
        }

        private void setTargetCount() {
            if (runLimitCheck.isChecked()) {
                if (Utils.getTestMode(getActivity()) == BillieJeanConfig.TEST_RESET_FACTORY_TEST) {
                    try {
                        writeReaderFile(BillieJeanConfig.RESET_FACTORY_FLAG, String.valueOf(-1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Utils.putTotalCount(getActivity(), -1);
                }
                Utils.putIsLimited(getActivity(),true);
            } else {
                if (Utils.getTestMode(getActivity()) == BillieJeanConfig.TEST_RESET_FACTORY_TEST) {
                    try {
                        writeReaderFile(BillieJeanConfig.RESET_FACTORY_FLAG, totalCountEdit.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Utils.putTotalCount(getActivity(), Integer.parseInt(totalCountEdit.getText().toString()));
                }
                Utils.putTotalCountEdit(getActivity(),totalCountEdit.getText().toString());
                Utils.putIsLimited(getActivity(),false);
            }
        }

        private void setIntervalTimesCount() {
            Utils.putRestartCount(getActivity(), Integer.parseInt(intervalTimesEdit.getText().toString()));
            Log.d(TAG, "intervalTimes== " + intervalTimesEdit.getText().toString());
        }

        private void resetTestData() {
            Utils.putTestCount(getActivity(), 0);
            if (Utils.getTestMode(getActivity()) == BillieJeanConfig.TEST_RESET_FACTORY_TEST) {
                try {
                    Utils.writeReaderFile(BillieJeanConfig.RESET_FLAG, String.valueOf(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void fillData() {
            Log.d(TAG,"in fill data....");
            testCaseView.setText(String.format(getString(R.string.already_executing),
                    Utils.getTestCaseStr(getActivity(), Utils.getTestMode(getActivity()))));
            testCountView.setText(String.format(getString(R.string.already_execute_count),
                    String.valueOf(Utils.getTestCount(getActivity()))));
            if (Utils.getTotalCount(getActivity()) == -2) {
                totalCountView.setText(String.format(getString(R.string.total_execute_count), ""));
            } else {
                totalCountView.setText(String.format(getString(R.string.total_execute_count),
                        String.valueOf(Utils.getTotalCount(getActivity()))));
            }
            testInfoTextView.setText(String.format(getString(R.string.test_info), Utils.getInfoText(getActivity())));
            selectCaseView.setText(String.format(getString(R.string.case_selected_text),
                    Utils.getTestCaseStr(getActivity(), Utils.getCaseSelected(getActivity()))));
            runLimitCheck.setChecked(Utils.getIsLimited(getActivity()));
            if (!Utils.getIsLimited(getActivity())){
                totalCountEdit.setText(Utils.getTotalCountEdit(getActivity()));
            }else {

            }

        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((BillieJean) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    class BillieJeanHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            Log.d(TAG, "handleMessage " + message.what);
            switch (message.what) {
                case BillieJeanConfig.HANDLE_UI_REFRESH:
                default:
                    placeholderFragment.fillData();
                    break;
            }
        }
    }
}