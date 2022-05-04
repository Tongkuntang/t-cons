/****************************************************************************************
 * 文件名称：RecordActivity.java
 * 内容摘要：历史记录显示
 * 版本编号：1.0.1
 * 创建日期：2019年08月20日
 ****************************************************************************************/

package Activitys.NewTracker;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onecoder.fitblekitdemo.R;

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
        setContentView(R.layout.activity_record);

        m_recordMap = ModelStorage.getInstance().getModel();
        initView();
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
    private void initView() {
        m_recordOne = (Button) this.findViewById(R.id.record_button_one);
        m_recordTwo= (Button) this.findViewById(R.id.record_button_two);
        m_recordThree = (Button) this.findViewById(R.id.record_button_three);
        m_recordFour = (Button) this.findViewById(R.id.record_button_four);
        m_contentText = (TextView) this.findViewById(R.id.record_text_content);
        m_contentText.setMovementMethod(ScrollingMovementMethod.getInstance());

        int keyNumber = 0;
        for (String key : m_recordMap.keySet()) {
            if (keyNumber == 0) {
                m_recordOne.setVisibility(View.VISIBLE);
                m_recordOne.setText(key);
            }
            else if (keyNumber == 1) {
                m_recordTwo.setVisibility(View.VISIBLE);
                m_recordTwo.setText(key);
            }
            else if (keyNumber == 2) {
                m_recordThree.setVisibility(View.VISIBLE);
                m_recordThree.setText(key);
            }
            else if (keyNumber == 3) {
                m_recordFour.setVisibility(View.VISIBLE);
                m_recordFour.setText(key);
            }
            keyNumber = keyNumber + 1;
        }

        onClick(m_recordOne);
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


    /*************************************************************************************
     * 方法名称：onClick
     * 功能描述：
     * 输入参数：
     * 返回数据：
     *************************************************************************************/
    public void onClick(View view) {
        String valueString = "";
        if (view.getId() == R.id.record_button_one) {
            valueString = m_recordMap.get(m_recordOne.getText()).toString();
        }
        else if (view.getId() == R.id.record_button_two){
            valueString = m_recordMap.get(m_recordTwo.getText()).toString();
        }
        else if (view.getId() == R.id.record_button_three){
            valueString = m_recordMap.get(m_recordThree.getText()).toString();
        }
        else if (view.getId() == R.id.record_button_four){
            valueString = m_recordMap.get(m_recordFour.getText()).toString();
        }

        String result1 = valueString.replace("},","\n\n");
        String result2 = result1.replace(",","\n");
        String result3 = result2.replace("{","");
        String result4 = result3.replace("[","\n");
        String result5= result4.replace("}]","\n");

        m_contentText.setText(result5);
    }

}
