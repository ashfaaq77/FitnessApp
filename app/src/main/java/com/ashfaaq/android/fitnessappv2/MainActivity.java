package com.ashfaaq.android.fitnessappv2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    //Permission
    private static final int PERMISSIONS_REQUEST_LOCATION = 7676;

    //Check if permission is granted or not
    private Boolean permissionGranted = false;
    public static Boolean loc = false;
    public static int check = 1;

    //Googlemap
    private GoogleMap mMap;
    private LocationManager manager;
    private LocationListener locationListener;

    //latitude and longitude
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // checking permission
        checkPermission();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //get the latitude and longitude
                lat = location.getLatitude();
                lng = location.getLongitude();


                LatLng latLng = new LatLng(lat, lng);

                //clear old circle
                mMap.clear();

                //create new circle
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                mMap.getUiSettings().setScrollGesturesEnabled(true);
                mMap.getUiSettings().setRotateGesturesEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.addCircle(new CircleOptions().center(latLng).radius(10).visible(true)
                        .fillColor(Color.CYAN).strokeColor(Color.BLACK).zIndex(10));
                mMap.setMinZoomPreference(16);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(loc) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        check = 1;
    }


    //checking permission of ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION AND READ_PHONE_STATE
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            check = 0;
            ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_LOCATION);
        }else {
            if(!loc && check == 0) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }

            loc = true;
            check = 1;
        }
    }

    //start button
    public void startB(View v) {


        Intent intent = new Intent(this, RunningActivity.class);
        startActivity(intent);

    }

    //setting button
    public void settingB(View v) {

        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }


    //logs button
    public void logB(View v) {

        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);

    }

    //achievement Button
    public void achieveB(View v) {
        Intent intent = new Intent(this, AdvanceLogActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        permissionGranted = false;

        switch(requestCode){
            case PERMISSIONS_REQUEST_LOCATION:

                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            permissionGranted = false;
                            return;
                        }
                    }
                    permissionGranted = true;
                    checkPermission();
                }
            default:
                Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_LONG);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        manager.removeUpdates(locationListener);
    }

}
