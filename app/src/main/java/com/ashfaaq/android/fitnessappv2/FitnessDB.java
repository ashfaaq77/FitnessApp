package com.ashfaaq.android.fitnessappv2;

import android.database.sqlite.SQLiteDatabase;

public class FitnessDB {
    /**
    //tables : log and user

    //log table: id(auto-increment), distance, date, latlng
    //user table: id(autoincrement), username, weight, height, age, sex, image_uri
     */


    //id row will be used in both table
    public static final String KEY_ROWID = "_id";

    //defining user table (rows)
    public static final String KEY_USERNAME = "username";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_AGE = "age";
    public static final String KEY_SEX = "sex";

    //defining log table (rows)
    public static final String KEY_DATE = "date";
    public static final String KEY_LATLNG = "latlng";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_TIMERAN = "time";


    //defining user and log table
    public static final String USER_TABLE = "User";
    public static final String LOG_TABLE = "Log";

    //sql to create table
    private static final String DBCREATE_USER =
            "CREATE TABLE if not exists " + USER_TABLE +
                    " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            KEY_USERNAME + " TEXT," +
                            KEY_WEIGHT + " INTEGER," +
                            KEY_AGE + " INTEGER," +
                            KEY_HEIGHT + " INTEGER," +
                            KEY_SEX + " TEXT);";

    public static final String DBCREATE_LOG =
            "CREATE TABLE if not exists " + LOG_TABLE +
                    " ("  + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            KEY_DATE + " TEXT," +
                            KEY_LATLNG + " TEXT," +
                            KEY_TIMERAN + " TEXT," +
                            KEY_DISTANCE + " FLOAT);";



    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(DBCREATE_USER); //create table
        db.execSQL(DBCREATE_LOG);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE);
        onCreate(db);
    }


}
