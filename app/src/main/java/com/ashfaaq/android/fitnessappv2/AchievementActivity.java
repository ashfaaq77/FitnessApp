package com.ashfaaq.android.fitnessappv2;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Calendar;

public class AchievementActivity extends AppCompatActivity {


    //Textviews
    TextView distancePerDaily;
    TextView distancePerMonth;
    TextView timePerDaily;
    TextView timePerMonth;

    //Variables
    int year;
    int month;
    int day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        //Daily
        distancePerDaily = (TextView) findViewById(R.id.dailydistance);
        timePerDaily = (TextView) findViewById(R.id.timeperdaily);

        //Month
        distancePerMonth = (TextView) findViewById(R.id.monthdistance);
        timePerMonth = (TextView) findViewById(R.id.timepermonth);

        //Calender: get data
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //get data
        getDistancePerDaily();
        getDistancePerMonth();


    }

    public void getDistancePerDaily() {

        String[] projection = {
                FitnessDB.KEY_ROWID,
                FitnessDB.KEY_DISTANCE,
                FitnessDB.KEY_TIMERAN
        };

        String date = day + "/" + month + "/" + year;

        Uri uri = Uri.parse(FitnessContentProvider.LOGS_CONTENT_URI + "/0");
        Cursor cursor = getContentResolver().query(uri, projection, date, null, null);

        float distance = 0;
        String time = "";

        while (cursor != null && cursor.moveToNext()) {

            distance += cursor.getFloat(cursor.getColumnIndexOrThrow(FitnessDB.KEY_DISTANCE));
            time = cursor.getString(cursor.getColumnIndexOrThrow(FitnessDB.KEY_TIMERAN));

        }

        if(distance < 0) {
            distance = 0;
        }

        if(time.equals("")) {
            time = "No time";
        }

        distancePerDaily.setText(Float.toString(distance));
        timePerDaily.setText(time);
    }

    public void getDistancePerMonth() {

        String[] projection = {
                FitnessDB.KEY_ROWID,
                FitnessDB.KEY_DISTANCE,
                FitnessDB.KEY_TIMERAN
        };

        Uri uri = Uri.parse(FitnessContentProvider.LOGS_CONTENT_URI + "/00");
        Cursor cursor = getContentResolver().query(uri, projection, Integer.toString(month), null, null);

        float dis = 0;
        String time = "";

        while (cursor != null && cursor.moveToNext()) {

             dis = cursor.getFloat(cursor.getColumnIndexOrThrow("sum"));
             time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
        }

        if(dis < 0) {
            dis = 0;
        }

        if(time.equals("")) {
            time = "No time";
        }

        distancePerMonth.setText(Float.toString(dis));
        timePerMonth.setText(time);

    }
}
