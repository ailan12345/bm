package com.bm.breathM;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Service;
import android.widget.VideoView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class start extends AppCompatActivity {

    /*private BluetoothAdapter bluetoothAdapter;*/
    private static final int REQUEST_ENABLE_BT = 2;
    private ConnectThread connectThread;
    ReadData rd;
    BluetoothAdapter bluetoothAdapter;
    UUID MY_UUID;
    private OutputStream tmpOut ;//輸出流
    private InputStream tmpIn ;//輸入流
    TextView osValue;
    int attention = 0;
    int meditation = 0;
    ProgressBar progressBarM;
    ProgressBar progressBarA;


    MediaPlayer mediaPlayer;
    VideoView videoView;
    CountDownTimer yourCountDownTimer;
    CountDownTimer yourCountDownTimer2;
    int reciprocal = 10000;
    int frequency = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //no title bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.start);



        mediaPlayer = MediaPlayer.create(this, R.raw.train);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        videoView = (VideoView) this.findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.breathe));
        videoView.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        // hide medie controller
        videoView.setMediaController(null);
        videoView.start();

        Intent intent = this.getIntent();
        reciprocal = intent.getIntExtra("reciprocal", 10000); //總進行時數
        frequency = intent.getIntExtra("frequency", 0); //頻率
        yourCountDownTimer = new CountDownTimer(reciprocal, 1000) {
            TextView mTextView = (TextView) findViewById(R.id.textView);
            @Override
            public void onTick(long millisUntilFinished) {
                //倒數秒數中要做的事
                int alltime = (int) millisUntilFinished/1000;
                int minute = alltime / 60;
                int second = alltime % 60;
                if (second<10){
                    mTextView.setText(minute +":0" + second);
                }else{
                    mTextView.setText(minute +":" + second);
                }

            }
            @Override
            public void onFinish() {
                //倒數完成後要做的事
                mediaPlayer.stop();
                mediaPlayer.release();
                videoView.stopPlayback();
                mTextView.setText("Done!");
            }
        }.start();
        yourCountDownTimer2 = new CountDownTimer(reciprocal, (frequency-1)*1000) {
            int i = 0;
            int frequencyTime = 0;
            TextView myAwesomeTextView = (TextView)findViewById(R.id.textView7);
            @Override
            public void onTick(long millisUntilFinished) {
                //倒數秒數中要做的事
                setVibrate(200); // 震動 0.2 秒
                i += 1;
                new CountDownTimer(frequency*1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.v("Log_tag", "Tick of Progress" +(int)(frequency-(millisUntilFinished/1000))*(100/frequency) + "|||||" + millisUntilFinished);
                        if (i % 2 == 1) {
                            myAwesomeTextView.setText("吸氣" + ((millisUntilFinished / 1000)));
                        } else {
                            myAwesomeTextView.setText("吐吐氣" + ((millisUntilFinished / 1000)));
                        }
                    } @Override
                    public void onFinish() {
                        //倒數完成後要做的事
                    }
                }.start();
            }
            @Override
            public void onFinish() {
                //倒數完成後要做的事
                setVibrate(200); // 震動 0.2 秒
                myAwesomeTextView.setText("Done!");
            }
        };
        if(frequency!=0){
            yourCountDownTimer2.start();
        }



        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Register for broadcasts when a device is discovered.
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(receiver, filter);
//        Intent discoverableIntent =
//                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); //5 min
//        startActivity(discoverableIntent);

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            setVibrate(1000); // 震動 1 秒
            Toast.makeText(getApplicationContext(), "請開啟藍芽",
                    Toast.LENGTH_LONG).show();
        }

        BluetoothDevice device = bluetoothAdapter.getDefaultAdapter().getRemoteDevice("8C:DE:52:44:A7:40");
        MY_UUID = device.getUuids()[0].getUuid();
        osValue = (TextView) findViewById(R.id.textView7);
        osValue.setText("連接成功");
        connectThread = new ConnectThread(device);
        connectThread.run();
        rd = new ReadData();
        try {
            rd.sleep(500);
        } catch (InterruptedException e) {
            osValue.setText("尚未連接腦波設備1");
            return;
        }
        if (tmpIn!=null){
            rd.start();
        }else{
            osValue.setText("尚未連接腦波設備2");
        }
//        osValue = (TextView) findViewById(R.id.textView7);
//        osValue.setText(attention+"||||"+meditation);

//        if (tmpIn!=null){
//            try{
//                }catch (Exception e){
//            }
//        }else {
//            osValue.setText("尚未連接腦波設備");
//        }


    }

    @Override
    public void onPause()
    {
        super.onPause();
        Toast.makeText(start.this,
                "返回" , Toast.LENGTH_SHORT).show();
        yourCountDownTimer.cancel();
        yourCountDownTimer2.cancel();
//        mediaPlayer.stop();
        mediaPlayer.release();
        videoView.stopPlayback();
        connectThread.cancel();;
        rd.interrupt();
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


    public void InsertData(final String macAddr, final String attention, final String meditation){
        Log.e("TGAC", "attenti1111111111: ");
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{
            @Override
            protected String doInBackground(String...params){
                String macHolder = macAddr;
                String attHolder = attention;
                String medHolder = meditation;
                List<NameValuePair> nameValuePAirs = new ArrayList<>();
                nameValuePAirs.add(new BasicNameValuePair("macAddr", macHolder));
                nameValuePAirs.add(new BasicNameValuePair("attention", attHolder));
                nameValuePAirs.add(new BasicNameValuePair("meditation", medHolder));

                try{
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("https://ailan.herokuapp.com/bm/"+ macAddr + "/" + attention + "/" + meditation + "/");
                    Log.e("TGAC", ""+post);
                    post.setEntity(new UrlEncodedFormEntity(nameValuePAirs));
                    HttpResponse response = client.execute(post);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "";
            }
//            @Override
//            protected void onPostExecute(String result){
//                super.onPostExecute(result);
//                Toast.makeText()
//            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(macAddr, attention, meditation);
    }

    OkHttpClient client = new OkHttpClient();
    private void okhttpAsyncGet(){
        Request request = new Request.Builder()
                .url("https://ailan.herokuapp.com/bm/"+ getMacAddr() + "/" + attention + "/" + meditation + "/")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }


    public class ReadData extends Thread{
        int t =0;
        char c;
        int i = 0;
        Boolean need = false;
        List<Integer> data = new ArrayList<Integer>();
        List<Integer> needdata = new ArrayList<Integer>();
        //覆寫Thread方法run()
        public void run(){
            try {
                while((i=tmpIn.read())!=-1){
                    data.add(i);
                    if(data.size()>8 && i==131 &&
                            data.get(data.size()-2)==0 &&
                            data.get(data.size()-4)==32 &&
                            data.get(data.size()-5)==170 && data.get(data.size()-6)==170 && !need) {
                        need = true; }
                    if(need){
                        needdata.add(i);}
                    //  int attention;4 26     int meditation;5 28
                    if (needdata.size()==31){
                        attention=needdata.get(27);
                        meditation=needdata.get(29);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBarA = (ProgressBar) findViewById(R.id.progressBarA);
                                progressBarM = (ProgressBar) findViewById(R.id.progressBarM);
                                progressBarA.setProgress(attention);//專注
                                progressBarM.setProgress(meditation);//放鬆
                                okhttpAsyncGet();

//                                InsertData(getMacAddr(), Integer.toString(attention), Integer.toString(meditation));

                            }
                        });
                    }//Integer.toHexString(i)
                    if (needdata.size()>=31){
                        needdata = new ArrayList<Integer>();
                        data = new ArrayList<Integer>();
                        need = false;
                    }
                }} catch (IOException e) {
//                e.printStackTrace();
                return;
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };


    public void setVibrate(int time){
        Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(time);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Don't forget to unregister the ACTION_FOUND receiver.
//        unregisterReceiver(receiver);
//    }
//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            Toast.makeText(getApplicationContext(), String.valueOf(msg.obj),
//                    Toast.LENGTH_LONG).show();
//            super.handleMessage(msg);
//        }
//    };



    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e("TGA", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();
            int t =0;
            char c;
            int i;
            Boolean need = false;
            List<Integer> data = new ArrayList<Integer>();
            List<Integer> needdata = new ArrayList<Integer>();
            try {
                mmSocket.connect();
                tmpOut  = mmSocket.getOutputStream();
                tmpIn = mmSocket.getInputStream();
                bluetoothAdapter.cancelDiscovery();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("TGA", "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
//            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("TGA", "Could not close the client socket", e);
            }
        }
    }


}
