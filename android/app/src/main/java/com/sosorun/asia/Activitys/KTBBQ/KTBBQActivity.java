/****************************************************************************************
 * 文件名称：KTBBQActivity.java
 * 内容摘要：烧烤针
 * 版本编号：1.0.1
 * 创建日期：2020年04月14日
 ****************************************************************************************/

package Activitys.KTBBQ;

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
import com.onecoder.fitblekit.API.KTBBQ.FBKApiKTBBQ;
import com.onecoder.fitblekit.API.KTBBQ.FBKApiKTBBQCallBack;
import com.onecoder.fitblekit.API.KTBBQ.FBKKTBBQUnits;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.onecoder.fitblekit.Protocol.KTBBQ.Parameter.FBKKTBBQResultValue;
import com.onecoder.fitblekitdemo.Activitys.ScanDevices.DevicesScanActivity;
import com.onecoder.fitblekitdemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KTBBQActivity extends Activity {

    // TAG值
    private static final String TAG = KTBBQActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int KTBBQ_TO_SCAN = 14001;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 烧烤针API
    private FBKApiKTBBQ m_ktbbq;

    // ListView
    private ListView m_ktbbqListView;

    // ListView BaseAdapter
    private BaseAdapter m_ktbbqAdapter;

    // List
    private static List<String> m_ktbbqArray = new ArrayList<>();

    // 连接状态
    private TextView m_statusText;


    // Ble CallBack
    private FBKApiKTBBQCallBack m_apiKTBBQCallBack = new FBKApiKTBBQCallBack() {
        @Override
        public void deviceMacAddress(Object data, FBKApiKTBBQ apiKTBBQ) {
            Log.e(TAG, "deviceMacAddress---"+data.toString());
        }

        @Override
        public void deviceVersion(Object data, FBKApiKTBBQ apiKTBBQ) {
            Log.e(TAG, "deviceVersion---"+data.toString());
        }

        @Override
        public void realTimeData(Object data, FBKApiKTBBQ apiKTBBQ) {
            Map<String,Object> dataMap = (Map<String,Object>) data;
            final List<FBKKTBBQResultValue> dataArray = (List<FBKKTBBQResultValue>) dataMap.get("value");

            if (dataArray == null) {
                Log.e(TAG, "realTimeData---null");
                return;
            }

            for (int i = 0; i < dataArray.size(); i++) {
                FBKKTBBQResultValue resultValue = dataArray.get(i);
                String logString = "realTimeData---"+resultValue.getChannelId()+"---"+resultValue.getValue();
                Log.e(TAG, logString);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dataArray.size() == 6) {
//                        Toast.makeText(KTBBQActivity.this,"realTimeData data",Toast.LENGTH_SHORT).show();
                    }
                    else if (dataArray.size() == 3) {
                        Toast.makeText(KTBBQActivity.this,"广播状态的实时数据",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void setTemperatureResult(boolean status, int channelNo, boolean isMaxTemperature, FBKApiKTBBQ apiKTBBQ) {
            Log.e(TAG, "setTemperatureResult---"+status+"---"+channelNo+"---"+isMaxTemperature);
        }

        @Override
        public void setTimeResult(boolean status, int channelNo, FBKApiKTBBQ apiKTBBQ) {
            Log.e(TAG, "setTimeResult---"+status+"---"+channelNo);
        }

        @Override
        public void setUnitResult(boolean status, int channelNo, FBKApiKTBBQ apiKTBBQ) {
            Log.e(TAG, "setUnitResult---"+status+"---"+channelNo);
        }

        @Override
        public void getUnitResult(FBKKTBBQUnits unit, int channelNo, FBKApiKTBBQ apiKTBBQ) {
            Log.e(TAG, "getUnitResult---"+unit.toString()+"---"+channelNo);
        }

        @Override
        public void getTemperatureResult(int tem, int channelNo, boolean isMaxTemperature, FBKApiKTBBQ apiKTBBQ) {
            Log.e(TAG, "getTemperatureResult---  "+tem+" ---"+channelNo+"---"+isMaxTemperature);
        }

        @Override
        public void deleteTemperatureResult(boolean status, int channelNo, boolean isMaxTemperature, FBKApiKTBBQ apiKTBBQ) {
            Log.e(TAG, "deleteTemperatureResult---"+status+"---"+channelNo+"---"+isMaxTemperature);
        }

        @Override
        public void readyAlarmResult(boolean status, FBKApiKTBBQ apiKTBBQ) {
            Log.e(TAG, "readyAlarmResult---"+status);
        }

        @Override
        public void setAlarmResult(boolean status, FBKApiKTBBQ apiKTBBQ) {
            Log.e(TAG, "setAlarmResult---"+status);
        }

        @Override
        public void deleteAlarmResult(boolean status, FBKApiKTBBQ apiKTBBQ) {
            Log.e(TAG, "deleteAlarmResult---"+status);
        }

        @Override
        public void getAlarmResult(boolean status, boolean isDelete, int minutes, FBKApiKTBBQ apiKTBBQ) {
            Log.e(TAG, "getAlarmResult---"+status+"---"+isDelete+"---"+minutes);
        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {

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
                            m_ktbbq.connecBluetooth( m_bluetoothDevice);
                        }
                    }
                    else if (status == FBKBleDeviceStatus.BleTurnOff) {
                        m_statusText.setText("BleTurnOff");
                    }
                    else if (status == FBKBleDeviceStatus.BleDisconnected) {
                        m_statusText.setText("BleDisconnected");
                    }
                    else if (status == FBKBleDeviceStatus.BleBroadcast) {
                        m_statusText.setText("广播BleBroadcast");
                    }
                }
            });
        }

        @Override
        public void batteryPower(int power, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void protocolVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void firmwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(KTBBQActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(KTBBQActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_ktbbq);

        m_ktbbq = new FBKApiKTBBQ(KTBBQActivity.this, m_apiKTBBQCallBack);
//        m_ktbbq.editDefaultPara(15,-90,5);
        m_ktbbq.registerBleListenerReceiver();
        m_ktbbqArray.clear();
        m_ktbbqArray.add("Set Min Temperature");
        m_ktbbqArray.add("Set Max Temperature");
        m_ktbbqArray.add("Get Min Temperature");
        m_ktbbqArray.add("Get Max Temperature");
        m_ktbbqArray.add("Delete Min Temperature");
        m_ktbbqArray.add("Delete Max Temperature");
        m_ktbbqArray.add("Set Time");
        m_ktbbqArray.add("Set Unit C");
        m_ktbbqArray.add("Set Unit F");
        m_ktbbqArray.add("Get Unit");
        m_ktbbqArray.add("Ready Alarm");
        m_ktbbqArray.add("Get Mac address");
        m_ktbbqArray.add("Get Device Version");
        m_ktbbqArray.add("Enter OTA Mode");
        m_ktbbqArray.add("Set Alarm Time");
        m_ktbbqArray.add("Delete Alarm Time");
        m_ktbbqArray.add("Get Alarm Time");
        m_ktbbqArray.add("Disconnect Device");
        m_ktbbqArray.add("Private get version");
        m_ktbbqArray.add("Private get mac");
        m_ktbbqArray.add("Private Enter OTA Mode");
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
        m_ktbbq.disconnectBle();
        m_ktbbq.unregisterBleListenerReceiver();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.ktbbq_text_status);
        m_ktbbqListView = (ListView) this.findViewById(R.id.ktbbq_list);
        m_ktbbqAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_ktbbqArray.size();
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
                LayoutInflater inflater = KTBBQActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_ktbbqArray.get(position));

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_ktbbqListView.setAdapter(m_ktbbqAdapter);
        m_ktbbqListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    m_ktbbq.setBBQTemperature(-10, 1, false);
                }
                else if (position == 1) {
                    m_ktbbq.setBBQTemperature(80, 1, true);
                }
                else if (position == 2) {
                    m_ktbbq.getBBQTemperature(1, false);
                }
                else if (position == 3) {
                    m_ktbbq.getBBQTemperature(1, true);
                }
                else if (position == 4) {
                    m_ktbbq.deleteBBQTemperature(1, false);
                }
                else if (position == 5) {
                    m_ktbbq.deleteBBQTemperature(1, true);
                }
                else if (position == 6) {
                    m_ktbbq.setBBQTime(90, 1);
                }
                else if (position == 7) {
                    m_ktbbq.setBBQUnit(FBKKTBBQUnits.C, 1);
                }
                else if (position == 8) {
                    m_ktbbq.setBBQUnit(FBKKTBBQUnits.F, 1);
                }
                else if (position == 9) {
                    m_ktbbq.getBBQUnit(1);
                }
                else if (position == 10) {
                    m_ktbbq.readyAlarm();
                }
                else if (position == 11) {
                    m_ktbbq.getMacAddress();
                }
                else if (position == 12) {
                    m_ktbbq.getDeviceVersion();
                }
                else if (position == 13) {
                    m_ktbbq.enterOTAMode();
                }
                else if (position == 14) {
                    m_ktbbq.setAlarmTime(5);
                }
                else if (position == 15) {
                    m_ktbbq.deleteAlarmTime();
                }
                else if (position == 16) {
                    m_ktbbq.getAlarmTime();
                }
                else if (position == 17) {
                    m_ktbbq.disconnectBle();
                }
                else if (position == 18) {
                    m_ktbbq.getPrivateVersion();
                }
                else if (position == 19) {
                    m_ktbbq.getPrivateMacAddress();
                }
                else if (position == 20) {
                    m_ktbbq.enterOTAMode();
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
        Intent intent = new Intent(KTBBQActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,KTBBQ_TO_SCAN);
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
        if (requestCode == KTBBQ_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
            m_ktbbq.connecBluetooth(m_bluetoothDevice);
        }
    }

}
