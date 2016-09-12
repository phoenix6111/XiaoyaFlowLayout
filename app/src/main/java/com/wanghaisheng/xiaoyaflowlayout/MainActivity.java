package com.wanghaisheng.xiaoyaflowlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.wanghaisheng.view.flowlayout.FlowLayout;

public class MainActivity extends AppCompatActivity {

    private String[] texts = {"wanghaisheng","吴亚玲","hello world","thinking in java","李亚 hello",
            "wanghaisheng","吴亚玲","hello world","thinking in java","李亚 hello",
            "wanghaisheng","吴亚玲","hello world","thinking in java","李亚 hello"};

    private FlowLayout mFlowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFlowLayout = (FlowLayout) findViewById(R.id.flow_layout);

        initDatas();
    }

    private void initDatas() {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (String text : texts) {
            TextView textView = (TextView) inflater.inflate(R.layout.item_textview,mFlowLayout,false);
            textView.setText(text);

            mFlowLayout.addView(textView);
        }
    }
}
