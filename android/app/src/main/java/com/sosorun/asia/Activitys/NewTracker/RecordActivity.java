/****************************************************************************************
 * 文件名称：RecordActivity.java
 * 内容摘要：历史记录显示
 * 版本编号：1.0.1
 * 创建日期：2019年08月20日
 ****************************************************************************************/

package com.sosorun.asia.Activitys.NewTracker;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class RecordActivity extends Activity {

    // TAG值
    private static final String TAG = RecordActivity.class.getSimpleName();

    // 数据
    private Map<String, Object> m_recordMap = new HashMap<>();

    private Button m_recordOne;
    private Button m_recordTwo;
    private Button m_recordThree;
    private Button m_recordFour;
    private TextView m_contentText;

    /************************************************************************************
     * 方法名称：onCreate
     * 功能描述：初始化
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_recordMap = ModelStorage.getInstance().getModel();
    }


    /************************************************************************************
     * 方法名称：onCreate
     * 功能描述：初始化
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ModelStorage.getInstance().clearModel();
    }

    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/



    /************************************************************************************
     * 方法名称：backAction
     * 功能描述：返回
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    public void backAction(View view) {
        finish();
    }


    /*************************************************************************************
     * 方法名称：onClick
     * 功能描述：
     * 输入参数：
     * 返回数据：
     *************************************************************************************/


}
