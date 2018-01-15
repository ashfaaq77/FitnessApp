package com.ashfaaq.android.fitnessappv2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    //Defining views
    ImageView profile_pic;
    TextView profile_name;
    TextView profile_age;
    TextView profile_weight;
    TextView profile_height;
    TextView profile_sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();

        userContent();
    }

    //initialization of views
    private void init() {

        //defining txtviews
        profile_pic = (ImageView) findViewById(R.id.iv);
        profile_name = (TextView) findViewById(R.id.name);
        profile_age = (TextView) findViewById(R.id.ageNo);
        profile_weight = (TextView) findViewById(R.id.weightNo);
        profile_height = (TextView) findViewById(R.id.heightNo);
        profile_sex = (TextView) findViewById(R.id.sexA);
    }

    //query database and populate the views
    private void userContent() {

        String username;
        int age;
        int height;
        int weight;
        String sex;

        String[] projection = {
                FitnessDB.KEY_USERNAME,
                FitnessDB.KEY_AGE,
                FitnessDB.KEY_WEIGHT,
                FitnessDB.KEY_HEIGHT,
                FitnessDB.KEY_SEX
        };

        Uri uri = Uri.parse(FitnessContentProvider.USER_CONTENT_URI + "/1");
        Cursor cursor = getContentResolver().query(
                uri, projection, null, null, null);

        if(cursor != null && cursor.moveToLast()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow(FitnessDB.KEY_USERNAME));
            age = cursor.getInt(cursor.getColumnIndexOrThrow(FitnessDB.KEY_AGE));
            weight = cursor.getInt(cursor.getColumnIndexOrThrow(FitnessDB.KEY_WEIGHT));
            height = cursor.getInt(cursor.getColumnIndexOrThrow(FitnessDB.KEY_HEIGHT));
            sex = cursor.getString(cursor.getColumnIndexOrThrow(FitnessDB.KEY_SEX));

            setViews(username, age, weight, height, sex);

        }else {
            username = "Default Name";
            age = 21;
            weight = 70;
            height = 180;
            sex = "Male";

            setViews(username, age, weight, height, sex);
        }

    }

    private void setViews(String name, int age, int weight, int height, String sex) {

        profile_name.setText(name);
        profile_height.setText(Integer.toString(height) + "cm");
        profile_weight.setText(Integer.toString(weight) + "kg");
        profile_age.setText(Integer.toString(age));
        profile_sex.setText(sex);


    }

    //close button
    public void closeB(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void editB(View v) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }




}