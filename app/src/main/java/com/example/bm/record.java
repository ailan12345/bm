package com.example.bm;

import android.os.Bundle;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class record extends AppCompatActivity {

    WebView record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);
        record = (WebView) findViewById(R.id.webRecord);
        WebSettings settings = record.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);//是否允許在WebView中訪問內容URL
        record.loadUrl("https://ailan.herokuapp.com/bm/findbm/" + getMacAddr() + "/");
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

//    public void onBackPressed() {
//        if (webRecord.canGoBack()) {
//            webRecord.goBack();
//            return;
//        }
//        super.onBackPressed();
//    }
}
