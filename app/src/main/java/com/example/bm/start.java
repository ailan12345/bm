package com.example.bm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Service;
import android.widget.VideoView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class start extends AppCompatActivity {

    /*private BluetoothAdapter bluetoothAdapter;*/
    private static final int REQUEST_ENABLE_BT = 2;
    private ConnectThread connectThread;
    BluetoothAdapter bluetoothAdapter;
    UUID MY_UUID;
    private OutputStream tmpOut ;//輸出流
    private InputStream tmpIn ;//輸入流
    TextView osValue;
    int attention = 0;
    int meditation = 0;


    MediaPlayer mediaPlayer;
    VideoView videoView;
    CountDownTimer yourCountDownTimer;
    //    CountDownTimer yourCountDownTimer2;
    int reciprocal = 10000;
    int frequency = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
//        frequency = intent.getIntExtra("frequency", 2); //頻率
        yourCountDownTimer = new CountDownTimer(reciprocal, 1000) {
            TextView mTextView = (TextView) findViewById(R.id.textView);
            @Override
            public void onTick(long millisUntilFinished) {
                //倒數秒數中要做的事
                int alltime = (int) millisUntilFinished/1000;
                int minute = alltime / 60;
                int second = alltime % 60;
                mTextView.setText(minute +":" + second);
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
//        yourCountDownTimer2 = new CountDownTimer(reciprocal, (frequency-1)*1000) {
//            int i = 0;
//            int frequencyTime = 0;
//            TextView myAwesomeTextView = (TextView)findViewById(R.id.textView7);
//            @Override
//            public void onTick(long millisUntilFinished) {
//                //倒數秒數中要做的事
//                setVibrate(200); // 震動 0.2 秒
//                i += 1;
//                new CountDownTimer(frequency*1000, 1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        Log.v("Log_tag", "Tick of Progress" +(int)(frequency-(millisUntilFinished/1000))*(100/frequency) + "|||||" + millisUntilFinished);
//                        if (i % 2 == 1) {
//                            myAwesomeTextView.setText("吸氣" + ((millisUntilFinished / 1000)));
//                        } else {
//                            myAwesomeTextView.setText("吐吐氣" + ((millisUntilFinished / 1000)));
//                        }
//                    } @Override
//                    public void onFinish() {
//                        //倒數完成後要做的事
//                    }
//                }.start();
//            }
//            @Override
//            public void onFinish() {
//                //倒數完成後要做的事
//                setVibrate(200); // 震動 0.2 秒
//                myAwesomeTextView.setText("Done!");
//            }
//        }.start();







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


        connectThread = new ConnectThread(device);
        connectThread.run();
        ReadData rd = new ReadData();
        rd.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Toast.makeText(start.this,
                "返回" , Toast.LENGTH_SHORT).show();
        yourCountDownTimer.cancel();
//        yourCountDownTimer2.cancel();
        mediaPlayer.stop();
        mediaPlayer.release();
        videoView.stopPlayback();
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
                                osValue = (TextView) findViewById(R.id.textView7);
                                osValue.setText(attention+"||||"+meditation);
                            }
                        });
                        Log.e("TGAC", "attention and meditation: "+attention+"||||"+meditation);
                    }//Integer.toHexString(i)
                    if (needdata.size()>=31){
                        needdata = new ArrayList<Integer>();
                        data = new ArrayList<Integer>();
                        need = false;
                    }
                }} catch (IOException e) {
                e.printStackTrace();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), String.valueOf(msg.obj),
                    Toast.LENGTH_LONG).show();
            super.handleMessage(msg);
        }
    };



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
