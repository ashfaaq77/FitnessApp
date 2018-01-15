package com.ashfaaq.android.fitnessappv2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class AddActivity extends AppCompatActivity {

    //Defining views
    ImageView pic;
    EditText name;
    EditText age;
    EditText weight;
    EditText height;
    EditText sex;

    //check if empty
    private boolean empty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //initialization
        init();

        userContent();
    }

    //Initialization
    private void init() {

        //defining txtviews
        pic = (ImageView) findViewById(R.id.ivadd);
        name = (EditText) findViewById(R.id.nameText);
        age = (EditText) findViewById(R.id.ageNo);
        weight = (EditText) findViewById(R.id.weightNo);
        height = (EditText) findViewById(R.id.heightNo);
        sex = (EditText) findViewById(R.id.sexA);
    }

    //save button
    public void saveB(View v) {

        //content values to add data to user database
        ContentValues contentValues = new ContentValues();

        contentValues.put(FitnessDB.KEY_USERNAME, name.getText().toString());
        contentValues.put(FitnessDB.KEY_AGE, Integer.parseInt(age.getText().toString()));
        contentValues.put(FitnessDB.KEY_WEIGHT, Integer.parseInt(weight.getText().toString()));
        contentValues.put(FitnessDB.KEY_HEIGHT, Integer.parseInt(height.getText().toString()));
        contentValues.put(FitnessDB.KEY_SEX, sex.getText().toString());

        //If(empty) then insert else update queries
        if(empty) {

            getContentResolver().insert(FitnessContentProvider.USER_CONTENT_URI, contentValues);

        }else if(!empty) {
            Uri uri = Uri.parse(FitnessContentProvider.USER_CONTENT_URI + "/1");
            getContentResolver().update(uri, contentValues, null, null);
        }

        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);

    }

    //Get the user content
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

        Uri uriC = Uri.parse(FitnessContentProvider.USER_CONTENT_URI + "/1");
        Cursor cursor = getContentResolver().query(
                uriC, projection, null, null, null);

        if (cursor != null && cursor.moveToLast()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow(FitnessDB.KEY_USERNAME));
            age = cursor.getInt(cursor.getColumnIndexOrThrow(FitnessDB.KEY_AGE));
            weight = cursor.getInt(cursor.getColumnIndexOrThrow(FitnessDB.KEY_WEIGHT));
            height = cursor.getInt(cursor.getColumnIndexOrThrow(FitnessDB.KEY_WEIGHT));
            sex = cursor.getString(cursor.getColumnIndexOrThrow(FitnessDB.KEY_SEX));

            setViews(username, age, weight, height, sex);
            empty = false;

        } else {
            username = "Default Name";
            age = 21;
            weight = 70;
            height = 180;
            sex = "Male";
            empty = true;

            setViews(username, age, weight, height, sex);
        }
    }

    //Setting the views
    private void setViews(String name, int age, int weight, int height, String sex) {

        this.name.setText(name);
        this.height.setText(Integer.toString(height));
        this.weight.setText(Integer.toString(weight));
        this.age.setText(Integer.toString(age));
        this.sex.setText(sex);

    }

}
