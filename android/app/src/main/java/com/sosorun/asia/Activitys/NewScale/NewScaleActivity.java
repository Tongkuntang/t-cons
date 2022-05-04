/****************************************************************************************
 * 文件名称：NewScaleActivity.java
 * 内容摘要：新协议秤
 * 版本编号：1.0.1
 * 创建日期：2019年08月22日
 ****************************************************************************************/

package Activitys.NewScale;

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
import com.onecoder.fitblekit.API.NewScale.FBKApiNewScale;
import com.onecoder.fitblekit.API.NewScale.FBKApiNewScaleCallBack;
import com.onecoder.fitblekit.API.NewScale.FBKWeightUnits;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.onecoder.fitblekitdemo.Activitys.NewTracker.NewTrackerActivity;
import com.onecoder.fitblekitdemo.Activitys.ScanDevices.DevicesScanActivity;
import com.onecoder.fitblekitdemo.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NewScaleActivity extends Activity {

    // TAG值
    private static final String TAG = NewTrackerActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int NEWSCALE_TO_SCAN = 3001;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 新协议手环
    private FBKApiNewScale m_apiNewScale;

    // ListView
    private ListView m_newScaleListView;

    // ListView BaseAdapter
    private BaseAdapter m_newScaleAdapter;

    // List
    private static List<String> m_newScaleArray = new ArrayList<>();

    private TextView m_statusText;
    private TextView m_weightText;
    private TextView m_timeText;

    // 新协议秤回调
    private FBKApiNewScaleCallBack m_newScaleCallBack = new FBKApiNewScaleCallBack() {
        @Override
        public void realTimeWeight(Object data, FBKApiNewScale apiNewScale) {
            final Map<String, String> resultMap = (Map<String, String>) data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_weightText.setText("   Weight: "+resultMap.get("weight"));
                    m_timeText.setText("   Time: "+resultMap.get("weightTime"));
                }
            });
        }

        @Override
        public void weightRecord(Object data, FBKApiNewScale apiNewScale) {
            Log.e(TAG,"weightRecord" + data.toString());
        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {
            final String errorString = error;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NewScaleActivity.this,errorString,Toast.LENGTH_SHORT).show();
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
                            m_apiNewScale.connecBluetooth( m_bluetoothDevice);
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
                    m_newScaleArray.set(3,"Read Battery Power"+"   ("+String.valueOf(batteryPower)+"%"+")");
                    m_newScaleAdapter.notifyDataSetChanged();
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
                    m_newScaleArray.set(4,"Read Firmware Version"+"   ("+nowVersion+")");
                    m_newScaleAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_newScaleArray.set(5,"Read Hardware Version"+"   ("+nowVersion+")");
                    m_newScaleAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_newScaleArray.set(6,"Read Software Version"+"   ("+nowVersion+")");
                    m_newScaleAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NewScaleActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NewScaleActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_newscale);

        Log.e(TAG,"NewScaleActivity---"+Thread.currentThread().getId());

        m_apiNewScale = new FBKApiNewScale(NewScaleActivity.this, m_newScaleCallBack);
        m_apiNewScale.registerBleListenerReceiver();
        m_newScaleArray.clear();
        m_newScaleArray.add("Set Time");
        m_newScaleArray.add("Set Units");
        m_newScaleArray.add("Get Record");
        m_newScaleArray.add("Read Battery Power");
        m_newScaleArray.add("Read Firmware Version");
        m_newScaleArray.add("Read Hardware Version");
        m_newScaleArray.add("Read Software Version");
        m_newScaleArray.add("Private get version");
        m_newScaleArray.add("Private get mac");
        m_newScaleArray.add("Private Enter OTA Mode");
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
        m_apiNewScale.disconnectBle();
        m_apiNewScale.unregisterBleListenerReceiver();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.newscale_text_status);
        m_weightText = (TextView) this.findViewById(R.id.newscale_text_weight);
        m_timeText = (TextView) this.findViewById(R.id.newscale_text_time);

        m_newScaleListView = (ListView) this.findViewById(R.id.newscale_list);
        m_newScaleAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_newScaleArray.size();
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
                LayoutInflater inflater = NewScaleActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_newScaleArray.get(position));

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_newScaleListView.setAdapter(m_newScaleAdapter);
        m_newScaleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    m_apiNewScale.setUTC(new Date());
                }
                else if (position == 1) {
                    m_apiNewScale.setScaleUnit(FBKWeightUnits.WeightUnitsKG);
                }
                else if (position == 2) {
                    m_apiNewScale.getWeightRecord();
                }
                else if (position == 3) {
                    m_apiNewScale.readDeviceBatteryPower();
                }
                else if (position == 4) {
                    m_apiNewScale.readFirmwareVersion();
                }
                else if (position == 5) {
                    m_apiNewScale.readHardwareVersion();
                }
                else if (position == 6) {
                    m_apiNewScale.readSoftwareVersion();
                }
                else if (position == 7) {
                    m_apiNewScale.getPrivateVersion();
                }
                else if (position == 8) {
                    m_apiNewScale.getPrivateMacAddress();
                }
                else if (position == 9) {
                    m_apiNewScale.enterOTAMode();
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
        Intent intent = new Intent(NewScaleActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,NEWSCALE_TO_SCAN);
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
        if (requestCode == NEWSCALE_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
            m_apiNewScale.connecBluetooth(m_bluetoothDevice);
        }
    }


}
