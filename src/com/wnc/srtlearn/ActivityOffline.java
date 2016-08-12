package com.wnc.srtlearn;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ActivityOffline extends Activity
{

    private TextView txtLog;
    private final String DESC_TEXT = "" + "离在线语法识�?首次使用�?��联网授权)\n"
            + "如果无法正常使用请检�?\n" + " 1. 是否在AndroidManifest.xml配置了APP_ID\n"
            + " 2. 是否在开放平台对应应用绑定了包名\n" + "\n"
            + "点击�?��后你可以�?可以根据语法自行定义离线说法):\n"
            + " 1. 打电话给张三(离线)\n" + " 2. 打电话给李四(离线)\n"
            + " 3. 打开计算�?离线)\n" + " 4. 明天天气怎么�?�?��联网)\n" + " ..." + "\n";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk2_api);
        findViewById(R.id.setting).setVisibility(View.GONE);
        findViewById(R.id.txtResult).setVisibility(View.GONE);

        txtLog = (TextView) findViewById(R.id.txtLog);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent("com.baidu.action.RECOGNIZE_SPEECH");
                intent.putExtra("grammar", "asset:///baidu_speech_grammar.bsg"); // 设置离线的授权文�?离线模块�?��授权),
                                                                                 // 该语法可以用自定义语义工具生�?
                                                                                 // 链接http://yuyin.baidu.com/asr#m5
                // intent.putExtra("slot-data", your slots); //
                // 设置grammar中需要覆盖的词条,如联系人�?
                // startActivityForResult(intent, 1);

                txtLog.setText(DESC_TEXT);
            }
        });

        txtLog.setText(DESC_TEXT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            Bundle results = data.getExtras();
            ArrayList<String> results_recognition = results
                    .getStringArrayList("results_recognition");
            txtLog.append("识别结果(数组形式): " + results_recognition + "\n");
        }
    }
}
