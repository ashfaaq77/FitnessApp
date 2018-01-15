package com.ashfaaq.android.fitnessappv2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class LogActivity extends AppCompatActivity {

    //variable and setting adapter,views
    SimpleCursorAdapter mAdapter;
    ListView listview;
    ArrayList<Integer> pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        listview = findViewById(R.id.lv);
        pos = new ArrayList<Integer>();

        queryContentProvider();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), LatLngActivity.class);
                Bundle bundle = new Bundle();

                bundle.putInt("position", pos.get(i));

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }


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

        Cursor cursor = getContentResolver().query(FitnessContentProvider.LOGS_CONTENT_URI, projection, null, null, null);

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

        listview.setAdapter(mAdapter);
    }

}
