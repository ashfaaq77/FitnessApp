package com.ashfaaq.android.fitnessappv2;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import java.text.DecimalFormat;
import java.util.Calendar;

public class MyBoundService extends Service {


    //variable
    private Location initial_loc;
    private Location final_loc;
    private Location partial_loc;
    float distance = 0;
    int check_init = 0;
    private int time = 0;
    String timeString = "";

    int state = 0; //0 is stop; 1 is running; 2 is pause

    RemoteCallbackList<MyBinder> remoteCallbackList = new RemoteCallbackList<MyBinder>();

    boolean isGPSEnable = false;
    private LocationListener locationListener;
    private LocationManager manager;

    //threading
    Thread timeKm; //for timer and km
    progress runnable;

   String latlngString = "";
    @Override
    public void onCreate() {

        initializeLocationManager();
        pendingIntent();

        //broadcast receiver
        registerReceiver(bcr, new IntentFilter(Intent.ACTION_BATTERY_LOW));
        registerReceiver(bcr, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
        registerReceiver(bcr, new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));
        registerReceiver(bcr, new IntentFilter(Intent.ACTION_CALL));
        registerReceiver(bcr, new IntentFilter(Intent.ACTION_ANSWER));


        if (isGPSEnable) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;

            }

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    latlngString += Double.toString(location.getLatitude()) + " " + Double.toString(location.getLongitude()) + " ";

                    if(check_init == 0) {

                        initial_loc.setLatitude(location.getLatitude());
                        initial_loc.setLongitude(location.getLongitude());

                        final_loc.setLatitude(location.getLatitude());
                        final_loc.setLongitude(location.getLongitude());

                        distance = 0;
                        check_init = 1;
                    } else {

                        partial_loc.setLatitude(location.getLatitude());
                        partial_loc.setLongitude(location.getLongitude());
                        if(state == 1) {
                            distance += (final_loc.distanceTo(partial_loc)) / 1000;
                        }

                        final_loc.setLatitude(location.getLatitude());
                        final_loc.setLongitude(location.getLongitude());

                    }

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }

                @Override
                public void onProviderEnabled(String s) {
                }

                @Override
                public void onProviderDisabled(String s) {
                }
            };

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }
    }

    private void initializeLocationManager() {
        if (manager == null) {

            manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            isGPSEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            check_init = 0;

            initial_loc = new Location("Starting Point");
            final_loc = new Location("Ending Point");
            partial_loc = new Location("Partial Point");

            runnable = new progress();
            timeKm = new Thread(runnable);

        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder implements IInterface
    {
        @Override
        public IBinder asBinder() {
            return this;
        }

        void setState()
        {
             MyBoundService.this.setState();
        }


        public void registerCallback(CallbackInterface callback) {
            this.callback = callback;
            remoteCallbackList.register(MyBinder.this);
        }

        public void unregisterCallback(CallbackInterface callback) {
            remoteCallbackList.unregister(MyBinder.this);
        }

        CallbackInterface callback;
    }

    //play and pause
    public void setState() {
        if(state == 0 || state == 2) {
            state=1;
            if(timeKm.isAlive()) {
                runnable.onResume();
            }else {
                timeKm.start();
            }
        }else if(state == 1){
            state = 2;
            runnable.onPause();
        }
    }


    public void doCallbacks(float count, String tT)
    {
        final int n = remoteCallbackList.beginBroadcast();
        for (int i=0; i<n; i++)
        {
            remoteCallbackList.getBroadcastItem(i).callback.setTimeD(count, tT);
        }
        remoteCallbackList.finishBroadcast();
    }

    public void doCallbacks2(int check)
    {

        final int n = remoteCallbackList.beginBroadcast();
        for (int i=0; i<n; i++)
        {
            remoteCallbackList.getBroadcastItem(i).callback.changeState(check);
        }
        remoteCallbackList.finishBroadcast();
    }

    //get Distance
    public float getDistance() {
        return distance;
    }

    //time progress
    class progress implements Runnable {

        private Object play;
        private boolean stop;
        private boolean pause;

        public progress() {
            play = new Object();
            stop = false;
            pause = false;
        }

        @Override
        public void run() {
            while (!stop) {

                try {
                    tTime();
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                synchronized (play) {
                    while (pause) {
                        try {
                            play.wait();
                            // Thread.currentThread().wait();
                        } catch (InterruptedException e) {

                        }
                    }
                }

            }
        }

        //pause
        public void onPause() {
            synchronized (play) {
                pause = true;
            }
        }

        //resume
        public void onResume() {
            synchronized (play) {
                pause = false;
                stop = false;
                play.notifyAll();
            }
        }
    }


    public void tTime() {
        time++;

        int h = time / 3600;
        int min = (time % 3600) / 60;
        int sec = time % 60;

        if(h > 0) {
            /*String*/ timeString = String.format("%02d:%02d:%02d", h, min, sec);
            doCallbacks(getDistance(), timeString);
        }else {
            /*String*/ timeString = String.format("%02d:%02d", min, sec);
            doCallbacks(getDistance(), timeString);
        }

    }

    public void addDatabase() {

        //get the time
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = day + "/" + month + "/" + year;

        ContentValues contentValues = new ContentValues();

        contentValues.put(FitnessDB.KEY_DATE, date);

        DecimalFormat twoSF = new DecimalFormat("#.00");
        contentValues.put(FitnessDB.KEY_DISTANCE, twoSF.format(distance));

        contentValues.put(FitnessDB.KEY_LATLNG, latlngString);
        contentValues.put(FitnessDB.KEY_TIMERAN, timeString);

        getContentResolver().insert(FitnessContentProvider.LOGS_CONTENT_URI, contentValues);

    }

    //Broadcast receiver
    private BroadcastReceiver bcr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case "android.intent.action.BATTERY_LOW":
                    doCallbacks2(1);
                    runnable.onPause();
                    break;
                case "android.intent.action.AIRPLANE_MODE":
                    doCallbacks2(1);
                    runnable.onPause();
                    break;
                case "android.intent.action.NEW_OUTGOING_CALL":
                    doCallbacks2(0);
                    runnable.onPause();
                    state=2;
                    break;
            }

        }
    };


    //pending intent
    public void pendingIntent() {
       PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, RunningActivity.class), 0);
       Resources r = getResources();
       Notification notification = new NotificationCompat.Builder(this)
               .setTicker(("message"))
               .setSmallIcon(R.drawable.ic_runer_silhouette_running_fast)
               .setContentTitle("Fitness App")
               .setContentText("Click to open app")
               .setContentIntent(pi)
               .setAutoCancel(false)
               .setOngoing(true)
               .build();

       NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
       notificationManager.notify(0, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        state = 0;
        timeKm.interrupt();
        unregisterReceiver(bcr);
        addDatabase();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        super.onDestroy();
    }



}