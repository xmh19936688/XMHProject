package com.xmh.skyeyedemo.activity;

import android.os.Bundle;

import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.base.BaseActivity;

public class MainActivity extends BaseActivity {

    //TODO 监听添加好友请求，如果是username_开头则同意

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO 退出登录并使用username_head登录
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }
}
