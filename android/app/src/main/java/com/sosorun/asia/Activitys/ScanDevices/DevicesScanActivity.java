/****************************************************************************************
 * 文件名称：DevicesScanActivity.java
 * 内容摘要：设备扫描页面
 * 版本编号：1.0.1
 * 创建日期：2019年08月06日
 ****************************************************************************************/

package Activitys.ScanDevices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.onecoder.fitblekit.API.ScanDevices.FBKApiScan;
import com.onecoder.fitblekit.API.ScanDevices.FBKApiScanCallBack;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDevice;
import com.onecoder.fitblekitdemo.R;

import java.util.ArrayList;
import java.util.List;

public class DevicesScanActivity extends Activity {

    // TAG值
    private static final String TAG = DevicesScanActivity.class.getSimpleName();
    public static int SCAN_ACTIVITY_BACK = 1001;
    private FBKApiScan m_scanner;
    private ListView m_scanListView;
    private BaseAdapter m_scanListAdapter;
    private static List<FBKBleDevice> m_deviceArray = new ArrayList<>();
    private int m_cellNumber;


    /************************************************************************************
     * 方法名称：FBKApiScanCallBack
     * 功能描述：扫描回调
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private FBKApiScanCallBack m_apiScanCallBack = new FBKApiScanCallBack() {
        @Override
        public void bleScanResult(List<FBKBleDevice> deviceArray, FBKApiScan apiScan) {
            m_deviceArray = deviceArray;
            m_scanListAdapter.notifyDataSetChanged();
        }

        @Override
        public void bleScanAvailable(Boolean isAvailable, FBKApiScan apiScan) {

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
        setContentView(R.layout.activity_devicescan);

        m_cellNumber = -1;
        initView();

        m_scanner = new FBKApiScan();
        m_scanner.setScanRssi(-120);
        m_scanner.startScan(m_apiScanCallBack);
    }


    /************************************************************************************
     * 方法名称：onKeyDown
     * 功能描述：重写返回方法
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            m_scanner.stopScan();
            m_deviceArray.clear();
            m_scanListAdapter.notifyDataSetChanged();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_scanListView = (ListView) this.findViewById(R.id.devicescan_list);
        m_scanListAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_deviceArray.size();
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
                LayoutInflater inflater = DevicesScanActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                FBKBleDevice myBleDevice = m_deviceArray.get(position);
                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText(myBleDevice.getDeviceName()+" "+myBleDevice.getDeviceRssi());

                if (myBleDevice.isBBODevice()) {
                    Log.e(TAG,"BBODevice *****"+myBleDevice.getBBQInformation().toString());
                }

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_scanListView.setAdapter(m_scanListAdapter);
        m_scanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                m_scanner.stopScan();

                FBKBleDevice myBleDevice = m_deviceArray.get(position);
                Intent backIntent = new Intent();
                backIntent.putExtra("bluetooth",myBleDevice.getBleDevice());
                setResult(SCAN_ACTIVITY_BACK,backIntent);
                Log.e(TAG,"setOnItemClickListener *****"+myBleDevice.getMacAddress());

                m_deviceArray.clear();
                m_scanListAdapter.notifyDataSetChanged();
                finish();
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
        m_scanner.stopScan();
        m_deviceArray.clear();
        m_scanListAdapter.notifyDataSetChanged();
        finish();
    }


}
