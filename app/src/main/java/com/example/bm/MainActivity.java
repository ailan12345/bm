package com.example.bm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 2;
    private LinearLayout settingarea;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void bluetoothClick(View view) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "開啟中"
                    , Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "已經開啟",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void meditationClick(View v) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, meditation.class);
        startActivity(intent);

    }

//    public void descriptionClick(View v) {
//        Intent intent = new Intent();
//        intent.setClass(MainActivity.this, description.class);
//        startActivity(intent);
//
//    }


    public void breatheClick(View v) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, breathe.class);
        startActivity(intent);

    }

    public void recordClick(View v) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, record.class);
        startActivity(intent);

    }

    public void settingClick(View v) {  //設定按鈕
        settingarea  = (LinearLayout) findViewById(R.id.settingArea);
        settingarea.setVisibility(View.VISIBLE);    //設定介面開
    }

    public void settingCloseClick(View v) {  //設定關閉按鈕
        settingarea  = (LinearLayout) findViewById(R.id.settingArea);
        settingarea.setVisibility(View.GONE);    //設定介面關
    }

}