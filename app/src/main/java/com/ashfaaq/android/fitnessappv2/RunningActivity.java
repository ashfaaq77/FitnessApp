package com.ashfaaq.android.fitnessappv2;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class RunningActivity extends AppCompatActivity {

    //Button
    Button playButton;
    Button stopButton;
    public static boolean active = false;

    //Binder connection
    private MyBoundService.MyBinder myService = null;

    //variable
    private int state = 0; //play==1, pause==2, stop==0
    private Boolean permissionGranted = false; //checking permission

    private static final int PERMISSIONS_REQUEST_LOCATION = 7676;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);


        //initialisation
        init();

        //checking permission
        checkPermission();

        if(active) {
            state = 1;
            playButton.setBackground(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
            stopButton.setEnabled(false);
            stopButton.setBackground(getResources().getDrawable(R.drawable.ic_stop_screen_share_black_24dp));
        }

        this.startService(new Intent(this, MyBoundService.class));
        this.bindService(new Intent(this, MyBoundService.class), serviceConnection, Context.BIND_AUTO_CREATE);



    }

    private void init() {
        //init of button
        playButton = (Button) findViewById(R.id.play);
        stopButton = (Button) findViewById(R.id.stop);

    }


    //stop button
    public void stopB(View v) {

        state = 0; //stop

        this.unbindService(serviceConnection);
        this.stopService(new Intent(this, MyBoundService.class));

        active = false;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    //play button
    public void playB(View v) {

        //check if it is playing or not
        if (state == 2 || state == 0) {
            state = 1;
            playButton.setBackground(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
            stopButton.setEnabled(false);
            stopButton.setBackground(getResources().getDrawable(R.drawable.ic_stop_screen_share_black_24dp));
            myService.setState();

        } else { //it is playing
            state = 2;
            playButton.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
            stopButton.setBackground(getResources().getDrawable(R.drawable.ic_stop_black_24dp));
            stopButton.setVisibility(View.VISIBLE);
            stopButton.setEnabled(true);

            myService.setState();

        }

    }

    private ServiceConnection serviceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {;
            myService = (MyBoundService.MyBinder) service;
            myService.registerCallback(callback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService.unregisterCallback(callback);
            myService = null;
        }
    };

    CallbackInterface callback = new CallbackInterface() {
        @Override
        public void setTimeD(final float disT, final String time) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView distance = (TextView) findViewById(R.id.distance);
                    TextView timer = (TextView) findViewById(R.id.timer);

                    if(disT < 0) {
                        DecimalFormat twoSF = new DecimalFormat("#.00");
                        String dist = (twoSF.format(disT));
                        distance.setText("0" + dist);
                    }else {
                        DecimalFormat twoSF = new DecimalFormat("#.00");
                        distance.setText(twoSF.format(disT));
                    }

                    timer.setText(time);
                }
            });
        }

        public void changeState(final int check) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(check == 0) {
                        state = 2;
                        playButton.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
                        stopButton.setBackground(getResources().getDrawable(R.drawable.ic_stop_black_24dp));
                        stopButton.setVisibility(View.VISIBLE);
                        stopButton.setEnabled(true);

                    }else {
                       // active = false;
                        state = 2;
                        playButton.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
                        stopButton.setBackground(getResources().getDrawable(R.drawable.ic_stop_black_24dp));
                        stopButton.setVisibility(View.VISIBLE);
                        stopButton.setEnabled(true);
                    }
                }
            });

        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

       /* if(serviceConnection!=null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }*/
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        active = true;

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    private void checkPermission() {
        //checking if the user has allowed access to FINE_LOCATION and COARSE_LOCATION
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        } else {
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        permissionGranted = false;

        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION:

                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permissionGranted = false;
                            return;
                        }
                    }
                    permissionGranted = true;
                    checkPermission();
                }
            default:
        }
    }





}
