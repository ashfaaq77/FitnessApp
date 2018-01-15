package com.ashfaaq.android.fitnessappv2;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class fitnessDatabaseHelper extends SQLiteOpenHelper{

    //Database name and version
    private static final String DATABASE_NAME = "FitnessDB";
    private static final int DATABASE_VERSION = 27;

    fitnessDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        FitnessDB.onCreate(db); //create the database
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        FitnessDB.onUpgrade(db, oldVersion, newVersion);
    }
}
