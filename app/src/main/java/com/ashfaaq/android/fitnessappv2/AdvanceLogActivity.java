package com.ashfaaq.android.fitnessappv2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class AdvanceLogActivity extends AppCompatActivity {

    //Variables
    private static final int id = 756;

    private Calendar mCalendar;
    private int year;
    private int month;
    private int day;
    private String date;

    ArrayList<Integer> pos;

    //Adapter and listviews
    ListView lv;
    SimpleCursorAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_log);

        //Get the date
        mCalendar = Calendar.getInstance();

        year = mCalendar.get(Calendar.YEAR);
        month = mCalendar.get(Calendar.MONTH)+1;
        day = mCalendar.get(Calendar.DAY_OF_MONTH);

        getDate(year, month, day);

        lv = (ListView) findViewById(R.id.lvS);
        pos = new ArrayList<Integer>();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), LatLngActivity.class);
                Bundle bundle = new Bundle();

                bundle.putInt("position", pos.get(i));

                intent.putExtras(bundle);

                startActivity(intent);

            }
        });

        queryContentProvider();

    }

    //achievement button
    public void achievementB(View v) {
        Intent intent = new Intent(this, AchievementActivity.class);
        startActivity(intent);
    }

    //date picker
    public void calendarB(View v) {
        showDialog(id);
        queryContentProvider();

    }

    public void searchB(View v) {
        queryContentProvider();
    }


    //Date Picker
    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == id) {
            return new DatePickerDialog(this, DateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener DateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

                    getDate(arg1, arg2+1, arg3);

                }
            };

    //setting the date
    public void getDate(int year, int month, int day) {
        date = day + "/" + month + "/" + year;

    }

    //query to get the date selected
    public void queryContentProvider() {

        String[] projection = new String[] {
                FitnessDB.KEY_ROWID,
                FitnessDB.KEY_DATE,
                FitnessDB.KEY_DISTANCE,
                FitnessDB.KEY_TIMERAN
        };

        String colsToDisplay [] = new String[] {
                FitnessDB.KEY_DATE,
                FitnessDB.KEY_DISTANCE,
                FitnessDB.KEY_TIMERAN
        };

        int[] colResIds = new int[] {
                R.id.date,
                R.id.distance,
                R.id.timerun
        };

        Uri uri = Uri.parse(FitnessContentProvider.LOGS_CONTENT_URI + "/0");

        Cursor cursor = getContentResolver().query(uri, projection,  date, null, null);

        pos.clear();

        if(cursor != null) {
            while(cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(FitnessDB.KEY_ROWID));
                pos.add(id);

            }
        }

        mAdapter = new SimpleCursorAdapter(
                this,
                R.layout.listview_layout,
                cursor,
                colsToDisplay,
                colResIds,
                0);

        mAdapter.notifyDataSetChanged();
        lv.setAdapter(mAdapter);
    }

}
