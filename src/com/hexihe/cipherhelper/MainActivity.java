package com.hexihe.cipherhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

    private static final String[] FUNCTIONS = {
        "凯撒密码", "栅栏密码", "倒序", "QWE密码", "莫尔斯电码",
        "键盘V密码", "埃特巴什码", "键盘坐标", "手机键盘", "字母表",
        "一个词暴力", "维吉尼亚密码", "元素周期表", "增删空格", "替换字符",
        "进制转换", "MD5", "大小写转换", "字母频率", "Base64",
        "Unicode", "中文电码", "区位码", "二维码", "扫码"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.list_functions);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_function, R.id.tv_name, FUNCTIONS);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 24) {
                    Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                    startActivityForResult(intent, 100);
                } else {
                    Intent intent = new Intent(MainActivity.this, FunctionActivity.class);
                    intent.putExtra("index", position);
                    intent.putExtra("name", FUNCTIONS[position]);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String result = data.getStringExtra("result");
            if (result != null) {
                Intent intent = new Intent(this, FunctionActivity.class);
                intent.putExtra("index", -1);
                intent.putExtra("name", "扫描结果");
                intent.putExtra("preset", result);
                startActivity(intent);
            }
        }
    }
}
