/****************************************************************************************
 * 文件名称：OldTrackerActivity.java
 * 内容摘要：旧协议手环
 * 版本编号：1.0.1
 * 创建日期：2019年09月27日
 * **************************************************************************************/

package Activitys.OldTracker;

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
import com.onecoder.fitblekit.API.OldTracker.FBKApiOldTracker;
import com.onecoder.fitblekit.API.OldTracker.FBKApiOldTrackerCallBack;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaSleep;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaUserInfo;
import com.onecoder.fitblekitdemo.Activitys.HeartRate.HeartRateActivity;
import com.onecoder.fitblekitdemo.Activitys.NewTracker.RecordActivity;
import com.onecoder.fitblekitdemo.Activitys.ScanDevices.DevicesScanActivity;
import com.onecoder.fitblekitdemo.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OldTrackerActivity extends Activity {

    // TAG值
    private static final String TAG = HeartRateActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int OLDTRACKER_TO_SCAN = 13001;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 设备API
    private FBKApiOldTracker m_apiOldTracker;

    // ListView
    private ListView m_OldTrackerListView;

    // ListView BaseAdapter
    private BaseAdapter m_OldTrackerAdapter;

    // List
    private static List<String> m_OldTrackerArray = new ArrayList<>();

    // 连接状态
    private TextView m_statusText;

    // 心率
    private TextView m_stepsText;
    private TextView m_caloriesText;
    private TextView m_distanceText;
    private TextView m_heartRateText;

    // 数据回调
    private FBKApiOldTrackerCallBack m_apiOldTrackerCallBack = new FBKApiOldTrackerCallBack() {
        @Override
        public void realTimeHeartRate(Object data, FBKApiOldTracker apiOldTracker) {
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
        public void realTimeSteps(Object data, FBKApiOldTracker apiOldTracker) {
            final Map<String, String> resultMap = (Map<String, String>) data;
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
        public void findPhone(boolean status, FBKApiOldTracker apiOldTracker) {

        }

        @Override
        public void oldTrackerRecord(Object data, FBKApiOldTracker apiOldTracker) {
            final Map<String, Object> resultMap = (Map<String, Object>) data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (resultMap.keySet().size() > 0) {
                        Intent intent = new Intent(OldTrackerActivity.this, RecordActivity.class);
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
                    Toast.makeText(OldTrackerActivity.this,errorString,Toast.LENGTH_SHORT).show();
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
                            m_apiOldTracker.connecBluetooth( m_bluetoothDevice);
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
                    m_OldTrackerArray.set(1,"Read Battery Power"+"   ("+String.valueOf(batteryPower)+"%"+")");
                    m_OldTrackerAdapter.notifyDataSetChanged();
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
                    m_OldTrackerArray.set(2,"Read Firmware Version"+"   ("+nowVersion+")");
                    m_OldTrackerAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_OldTrackerArray.set(3,"Read Hardware Version"+"   ("+nowVersion+")");
                    m_OldTrackerAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_OldTrackerArray.set(4,"Read Software Version"+"   ("+nowVersion+")");
                    m_OldTrackerAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(OldTrackerActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(OldTrackerActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oldtracker);
        m_apiOldTracker = new FBKApiOldTracker(OldTrackerActivity.this, m_apiOldTrackerCallBack);
        m_apiOldTracker.registerBleListenerReceiver();
        FBKParaUserInfo myUserInfo = new FBKParaUserInfo();
        myUserInfo.setGender(1);
        myUserInfo.setAge(25);
        myUserInfo.setHeight(170);
        myUserInfo.setWeight(72.1);
        myUserInfo.setStepSize(70);
        myUserInfo.setStepGoal(10000);
        m_apiOldTracker.setUserInfo(myUserInfo);
        FBKParaSleep mySleepInfo = new FBKParaSleep();
        mySleepInfo.setNormalStartHour(22);
        mySleepInfo.setNormalStartMinute(0);
        mySleepInfo.setNormalEndHour(8);
        mySleepInfo.setNormalEndMinute(0);
        mySleepInfo.setWeekendStartHour(23);
        mySleepInfo.setWeekendStartMinute(0);
        mySleepInfo.setWeekendEndHour(9);
        mySleepInfo.setWeekendEndMinute(0);
        m_apiOldTracker.setSleepInfo(mySleepInfo);
        m_apiOldTracker.setBikeInfo(2.096);
        m_OldTrackerArray.clear();
        m_OldTrackerArray.add("Get Record");
        m_OldTrackerArray.add("Read Battery Power");
        m_OldTrackerArray.add("Read Firmware Version");
        m_OldTrackerArray.add("Read Hardware Version");
        m_OldTrackerArray.add("Read Software Version");
        m_OldTrackerArray.add("Private get version");
        m_OldTrackerArray.add("Private get mac");
        m_OldTrackerArray.add("Private Enter OTA Mode");
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_apiOldTracker.disconnectBle();
        m_apiOldTracker.unregisterBleListenerReceiver();
    }
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.oldtracker_text_status);
        m_stepsText = (TextView) this.findViewById(R.id.oldtracker_text_steps);
        m_caloriesText = (TextView) this.findViewById(R.id.oldtracker_text_calories);
        m_distanceText = (TextView) this.findViewById(R.id.oldtracker_text_distance);
        m_heartRateText = (TextView) this.findViewById(R.id.oldtracker_text_heartrate);
        m_OldTrackerListView = (ListView) this.findViewById(R.id.oldtracker_list);
        m_OldTrackerAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_OldTrackerArray.size();
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
                LayoutInflater inflater = OldTrackerActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_OldTrackerArray.get(position));

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };
        m_OldTrackerListView.setAdapter(m_OldTrackerAdapter);
        m_OldTrackerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    m_apiOldTracker.getOldTrackerRecord();
                }
                else if (position == 1) {
                    m_apiOldTracker.readDeviceBatteryPower();
                }
                else if (position == 2) {
                    m_apiOldTracker.readFirmwareVersion();
                }
                else if (position == 3) {
                    m_apiOldTracker.readHardwareVersion();
                }
                else if (position == 4) {
                    m_apiOldTracker.readSoftwareVersion();
                }
                else if (position == 5) {
                    m_apiOldTracker.getPrivateVersion();
                }
                else if (position == 6) {
                    m_apiOldTracker.getPrivateMacAddress();
                }
                else if (position == 7) {
                    m_apiOldTracker.enterOTAMode();
                }
            }
        });
    }
    public void backAction(View view) {
        finish();
    }
    public void deviceAction(View view) {
        Intent intent = new Intent(OldTrackerActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,OLDTRACKER_TO_SCAN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OLDTRACKER_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
            m_apiOldTracker.connecBluetooth(m_bluetoothDevice);
        }
    }
}
