package com.ashfaaq.android.fitnessappv2;


import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class LatLngActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    //Variable
    int position;
    String date;
    GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lat_lng);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //get the position of the item in database
        Bundle bundle = getIntent().getExtras();
        position = bundle.getInt("position");

        String[] projection = {
                FitnessDB.KEY_ROWID,
                FitnessDB.KEY_LATLNG
        };

        Uri uriC = Uri.parse(FitnessContentProvider.LOGS_CONTENT_URI + "/" + position);
        Cursor cursor = getContentResolver().query(
                uriC, projection, null, null, null);


        if(cursor != null && cursor.moveToFirst()) {
             date = cursor.getString(cursor.getColumnIndexOrThrow(FitnessDB.KEY_LATLNG));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        populate();
    }

    //Populating the database
    public void populate() {
        String[] parts = date.split(" ");

        if(mMap != null) {
            for(int i = 0; i < parts.length; i+=2) {

                LatLng latLng = new LatLng(Double.parseDouble(parts[i]), Double.parseDouble(parts[i+1]));

                mMap.addCircle(new CircleOptions().center(latLng).radius(10).visible(true)
                        .fillColor(Color.RED).strokeColor(Color.BLACK).zIndex(10));


                mMap.setMinZoomPreference(15);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            }
        }else {
;
        }

    }
}
