/****************************************************************************************
 * 文件名称：NewTrackerActivity.java
 * 内容摘要：新协议手环
 * 版本编号：1.0.1
 * 创建日期：2019年08月06日
 ****************************************************************************************/

package Activitys.NewTracker;

import static com.onecoder.fitblekitdemo.Activitys.ScanDevices.DevicesScanActivity.SCAN_ACTIVITY_BACK;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod;
import com.onecoder.fitblekit.API.NewTracker.FBKApiNewTracker;
import com.onecoder.fitblekit.API.NewTracker.FBKApiNewTrackerCallBack;
import com.onecoder.fitblekit.API.NewTracker.FBKMusicStatus;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaAlarm;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaNotice;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaSitWater;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaSleep;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaUserInfo;
import com.onecoder.fitblekitdemo.Activitys.ScanDevices.DevicesScanActivity;
import com.onecoder.fitblekitdemo.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NewTrackerActivity extends Activity {

    // TAG值
    private static final String TAG = NewTrackerActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int NEWTRACKER_TO_SCAN = 2001;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 新协议手环
    private FBKApiNewTracker m_apiNewTracker;

    // ListView
    private ListView m_newTrackerListView;

    // ListView BaseAdapter
    private BaseAdapter m_newTrackerAdapter;

    // List
    private static List<String> m_newTrackerArray = new ArrayList<>();

    private TextView m_statusText;
    private TextView m_stepsText;
    private TextView m_caloriesText;
    private TextView m_distanceText;
    private TextView m_heartRateText;


    // 新协议手环回调
    private FBKApiNewTrackerCallBack m_apiNewTrackerCallBack = new FBKApiNewTrackerCallBack() {
        @Override
        public void realTimeSteps(Object stepsData, FBKApiNewTracker apiNewTracker) {
            final Map<String, String> resultMap = (Map<String, String>) stepsData;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_stepsText.setText("   Steps: "+resultMap.get("steps"));
                    m_caloriesText.setText("   Calories: "+resultMap.get("calories"));
                    m_distanceText.setText("   Diatance: "+resultMap.get("distance"));
                }
            });
        }

        @Override
        public void realTimeHeartRate(Object data, FBKApiNewTracker apiNewTracker) {
            Map<String, Object> resultMap = (Map<String, Object>) data;
            String heartRateStr = (String) resultMap.get("heartRate");
            final int heartRate = Integer.parseInt(heartRateStr);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_heartRateText.setText("   HeartRate: "+heartRate);
                }
            });
        }

        @Override
        public void findPhone(boolean isFinding, FBKApiNewTracker apiNewTracker) {
            final boolean finding = isFinding;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (finding) {
                        Toast.makeText(NewTrackerActivity.this,"Find my phone",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void takePhoto(FBKApiNewTracker apiNewTracker) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NewTrackerActivity.this,"Take Photo",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void musicStatus(FBKMusicStatus musicStatus, FBKApiNewTracker apiNewTracker) {

        }

        @Override
        public void lastSyncTime(String timeString, FBKApiNewTracker apiNewTracker) {
            final String myTimeString = timeString;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NewTrackerActivity.this,"Last Syn Time:"+myTimeString,Toast.LENGTH_SHORT).show();
                }
            });
        }


        @Override
        public void recordDetailData(Object data, FBKApiNewTracker apiNewTracker) {
            final Map<String, Object> resultMap = (Map<String, Object>) data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (resultMap.keySet().size() > 0) {
                        Intent intent = new Intent(NewTrackerActivity.this, RecordActivity.class);
                        intent.putExtra("data",(Serializable)resultMap);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {
            final String errorString = error;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NewTrackerActivity.this,errorString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void bleConnectStatus(FBKBleDeviceStatus connectStatus, FBKApiBsaeMethod apiBsaeMethod) {
            final FBKBleDeviceStatus status = connectStatus;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (status == FBKBleDeviceStatus.BleConnecting) {
                        m_statusText.setText("Connecting");
                    }
                    else if (status == FBKBleDeviceStatus.BleConnected) {
                        m_statusText.setText("Connected");
                    }
                    else if (status == FBKBleDeviceStatus.Blesynchronizing) {
                        m_statusText.setText("Synchronizing");
                    }
                    else if (status == FBKBleDeviceStatus.BleSyncOver) {
                        m_statusText.setText("Syn Over");
                    }
                    else if (status == FBKBleDeviceStatus.BleReconnect) {
                        m_statusText.setText("Reconnecting");
                    }
                    else if (status == FBKBleDeviceStatus.BleTurnOn) {
                        m_statusText.setText("BleTurnOn");
                        if (m_bluetoothDevice != null) {
                            m_apiNewTracker.connecBluetooth( m_bluetoothDevice);
                        }
                    }
                    else if (status == FBKBleDeviceStatus.BleTurnOff) {
                        m_statusText.setText("BleTurnOff");
                    }
                }
            });
        }


        @Override
        public void batteryPower(final int power, FBKApiBsaeMethod apiBsaeMethod) {
            final int batteryPower = power;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_newTrackerArray.set(20,"Read Battery Power"+"   ("+String.valueOf(batteryPower)+"%"+")");
                    m_newTrackerAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void protocolVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
        }

        @Override
        public void firmwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_newTrackerArray.set(21,"Read Firmware Version"+"   ("+nowVersion+")");
                    m_newTrackerAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_newTrackerArray.set(22,"Read Hardware Version"+"   ("+nowVersion+")");
                    m_newTrackerAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_newTrackerArray.set(23,"Read Software Version"+"   ("+nowVersion+")");
                    m_newTrackerAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NewTrackerActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NewTrackerActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    };


    /************************************************************************************
     * 方法名称：onCreate
     * 功能描述：初始化
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtracker);

        Log.e(TAG,"NewTrackerActivity---"+Thread.currentThread().getId());

        m_apiNewTracker = new FBKApiNewTracker(NewTrackerActivity.this,m_apiNewTrackerCallBack);
        m_apiNewTracker.registerBleListenerReceiver();
        m_newTrackerArray.clear();
        m_newTrackerArray.add("Set Time");
        m_newTrackerArray.add("Set User Info");
        m_newTrackerArray.add("Set Sleep Info");
        m_newTrackerArray.add("Set Water Info");
        m_newTrackerArray.add("Set Sit Info");
        m_newTrackerArray.add("Set Notice Info");
        m_newTrackerArray.add("Set Alarm Info");
        m_newTrackerArray.add("Set Bike Info");
        m_newTrackerArray.add("Set Max Heart Rate");
        m_newTrackerArray.add("Open Real Time Steps");
        m_newTrackerArray.add("Close Real Time Steps");
        m_newTrackerArray.add("Take Photo");
        m_newTrackerArray.add("Open Heart Rate Mode");
        m_newTrackerArray.add("Get Total Record");
        m_newTrackerArray.add("Get Steps Record");
        m_newTrackerArray.add("Get Sleep Record");
        m_newTrackerArray.add("Get Heart Rate Record");
        m_newTrackerArray.add("Get Bike Record");
        m_newTrackerArray.add("Get Train Record");
        m_newTrackerArray.add("Get Every Day Record");
        m_newTrackerArray.add("Read Battery Power");
        m_newTrackerArray.add("Read Firmware Version");
        m_newTrackerArray.add("Read Hardware Version");
        m_newTrackerArray.add("Read Software Version");
        m_newTrackerArray.add("Private get version");
        m_newTrackerArray.add("Private get mac");
        m_newTrackerArray.add("Private Enter OTA Mode");
        initView();
    }


    /************************************************************************************
     * 方法名称：onDestroy
     * 功能描述：销毁页面
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_apiNewTracker.disconnectBle();
        m_apiNewTracker.unregisterBleListenerReceiver();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.newtracker_text_status);
        m_stepsText = (TextView) this.findViewById(R.id.newtracker_text_steps);
        m_caloriesText = (TextView) this.findViewById(R.id.newtracker_text_calories);
        m_distanceText = (TextView) this.findViewById(R.id.newtracker_text_distance);
        m_heartRateText = (TextView) this.findViewById(R.id.newtracker_text_heartrate);

        m_newTrackerListView = (ListView) this.findViewById(R.id.newtracker_list);
        m_newTrackerAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_newTrackerArray.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = NewTrackerActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_newTrackerArray.get(position));

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_newTrackerListView.setAdapter(m_newTrackerAdapter);
        m_newTrackerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    m_apiNewTracker.setUTC(new Date());
                }
                else if (position == 1) {
                    FBKParaUserInfo myUserInfo = new FBKParaUserInfo();
                    myUserInfo.setGender(1);
                    myUserInfo.setAge(25);
                    myUserInfo.setHeight(170);
                    myUserInfo.setWeight(72.1);
                    myUserInfo.setStepSize(70);
                    myUserInfo.setStepGoal(10000);
                    m_apiNewTracker.setUserInfo(myUserInfo);
                }
                else if (position == 2) {
                    FBKParaSleep mySleepInfo = new FBKParaSleep();
                    mySleepInfo.setNormalStartHour(22);
                    mySleepInfo.setNormalStartMinute(0);
                    mySleepInfo.setNormalEndHour(8);
                    mySleepInfo.setNormalEndMinute(0);
                    mySleepInfo.setWeekendStartHour(23);
                    mySleepInfo.setWeekendStartMinute(0);
                    mySleepInfo.setWeekendEndHour(9);
                    mySleepInfo.setWeekendEndMinute(0);
                    m_apiNewTracker.setSleepInfo(mySleepInfo);
                }
                else if (position == 3) {
                    FBKParaSitWater myWaterInfo = new FBKParaSitWater();
                    myWaterInfo.setAmStartHour(8);
                    myWaterInfo.setAmStartMinute(03);
                    myWaterInfo.setAmEndHour(12);
                    myWaterInfo.setAmEndMinute(03);
                    myWaterInfo.setPmStartHour(16);
                    myWaterInfo.setPmStartMinute(57);
                    myWaterInfo.setPmEndHour(18);
                    myWaterInfo.setPmEndMinute(57);
                    myWaterInfo.setIntervalMinute(30);
                    myWaterInfo.setSwitchStatus(true);
                    m_apiNewTracker.setWaterInfo(myWaterInfo);
                }
                else if (position == 4) {
                    FBKParaSitWater mySitInfo = new FBKParaSitWater();
                    mySitInfo.setAmStartHour(8);
                    mySitInfo.setAmStartMinute(23);
                    mySitInfo.setAmEndHour(12);
                    mySitInfo.setAmEndMinute(23);
                    mySitInfo.setPmStartHour(16);
                    mySitInfo.setPmStartMinute(59);
                    mySitInfo.setPmEndHour(17);
                    mySitInfo.setPmEndMinute(59);
                    mySitInfo.setIntervalMinute(30);
                    mySitInfo.setSwitchStatus(true);
                    m_apiNewTracker.setSitInfo(mySitInfo);
                }
                else if (position == 5) {
                    FBKParaNotice myNoticeInfo = new FBKParaNotice();
                    myNoticeInfo.setMail(true);
                    myNoticeInfo.setMessage(true);
                    myNoticeInfo.setMissedCall(true);
                    myNoticeInfo.setQQ(true);
                    myNoticeInfo.setWeChat(true);
                    myNoticeInfo.setFaceBook(true);
                    myNoticeInfo.setWhatsAPP(true);
                    myNoticeInfo.setSkype(true);
                    myNoticeInfo.setOthers(true);
                    m_apiNewTracker.setNoticeInfo(myNoticeInfo);
                }
                else if (position == 6) {
                    List<FBKParaAlarm> alarmInfoList = new ArrayList<>();

                    List<String> repeatTimeList1 = new ArrayList<>();
                    repeatTimeList1.add("1");
                    repeatTimeList1.add("2");
                    repeatTimeList1.add("3");
                    repeatTimeList1.add("4");
                    repeatTimeList1.add("5");
                    repeatTimeList1.add("6");
                    repeatTimeList1.add("7");

                    FBKParaAlarm myAlarmInfo1 = new FBKParaAlarm();
                    myAlarmInfo1.setAlarmId(0);
                    myAlarmInfo1.setAlarmName("起床");
                    myAlarmInfo1.setAlarmTime("14:08");
                    myAlarmInfo1.setRepeatTime(repeatTimeList1);
                    myAlarmInfo1.setSwitchStatus(true);
                    alarmInfoList.add(myAlarmInfo1);

                    List<String> repeatTimeList2 = new ArrayList<>();
                    repeatTimeList2.add("0");

                    FBKParaAlarm myAlarmInfo2 = new FBKParaAlarm();
                    myAlarmInfo2.setAlarmId(1);
                    myAlarmInfo2.setAlarmName("生日");
                    myAlarmInfo2.setAlarmTime("2019-08-31 14:00:00");
                    myAlarmInfo2.setRepeatTime(repeatTimeList2);
                    myAlarmInfo2.setSwitchStatus(true);
                    alarmInfoList.add(myAlarmInfo2);

                    m_apiNewTracker.setAlarmInfo(alarmInfoList);
                }
                else if (position == 7) {
                    m_apiNewTracker.setBikeInfo(2.096);
                }
                else if (position == 8) {
                    m_apiNewTracker.setHeartRateMax(220);
                }
                else if (position == 9) {
                    m_apiNewTracker.openRealTimeSteps(true);
                }
                else if (position == 10) {
                    m_apiNewTracker.openRealTimeSteps(false);
                }
                else if (position == 11) {
                    m_apiNewTracker.openTakePhoto(true);
                }
                else if (position == 12) {
                    m_apiNewTracker.openHeartRateMode(true);
                }
                else if (position == 13) {
                    m_apiNewTracker.getTotalRecord();
                }
                else if (position == 14) {
                    m_apiNewTracker.getStepRecord();
                }
                else if (position == 15) {
                    m_apiNewTracker.getSleepRecord();
                }
                else if (position == 16) {
                    m_apiNewTracker.getHeartRateRecord();
                }
                else if (position == 17) {
                    m_apiNewTracker.getBikeRecord();
                }
                else if (position == 18) {
                    m_apiNewTracker.getTrainRecord();
                }
                else if (position == 19) {
                    m_apiNewTracker.getEverydayRecord();
                }
                else if (position == 20) {
                    m_apiNewTracker.readDeviceBatteryPower();
                }
                else if (position == 21) {
                    m_apiNewTracker.readFirmwareVersion();
                }
                else if (position == 22) {
                    m_apiNewTracker.readHardwareVersion();
                }
                else if (position == 23) {
                    m_apiNewTracker.readSoftwareVersion();
                }
                else if (position == 24) {
                    m_apiNewTracker.getPrivateVersion();
                }
                else if (position == 25) {
                    m_apiNewTracker.getPrivateMacAddress();
                }
                else if (position == 26) {
                    m_apiNewTracker.enterOTAMode();
                }
            }
        });
    }


    /************************************************************************************
     * 方法名称：backAction
     * 功能描述：返回
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    public void backAction(View view) {
        finish();
    }


    /************************************************************************************
     * 方法名称：deviceAction
     * 功能描述：选择设备
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    public void deviceAction(View view) {
        Intent intent = new Intent(NewTrackerActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,NEWTRACKER_TO_SCAN);
    }


    /************************************************************************************
     * 方法名称：onActivityResult
     * 功能描述：
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEWTRACKER_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
            m_apiNewTracker.connecBluetooth( m_bluetoothDevice);
        }
    }

}
