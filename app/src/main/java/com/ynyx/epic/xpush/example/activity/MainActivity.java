package com.ynyx.epic.xpush.example.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.ynyx.epic.xpush.example.R;
import com.ynyx.epic.xpush.smack.XPushInterface;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XPushInterface.setDebugMode(false);// debug 
        XPushInterface.init(this);
        
    }
}
/*
    private TextView tvMessage;
    * tvMessage = (TextView) findViewById(R.id.tvMessage);

        String message = getIntent().getStringExtra("message");
        if (!TextUtils.isEmpty(message)) {
            tvMessage.setText(message);
        }
    * 
    * 

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String message = getIntent().getStringExtra("message");
        if (!TextUtils.isEmpty(message)) {
            tvMessage.setText(message);
        }
    }
    */