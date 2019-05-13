package com.example.bm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
        record.loadUrl("https://ailan.herokuapp.com/");
    }

//    public void onBackPressed() {
//        if (webRecord.canGoBack()) {
//            webRecord.goBack();
//            return;
//        }
//        super.onBackPressed();
//    }
}
