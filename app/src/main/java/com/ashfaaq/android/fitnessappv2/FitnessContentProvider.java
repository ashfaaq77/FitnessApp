package com.ashfaaq.android.fitnessappv2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;


public class FitnessContentProvider extends ContentProvider{


    private fitnessDatabaseHelper dbHelper;

    //LOGS
    private static final int ALL_LOGS = 1;
    private static final int SINGLE_LOGS = 2;

    //USER
    private static final int ALL_USER = 3;
    private static final int SINGLE_USER = 4;

    //AUTHORITY
    private static final String AUTHORITY = "com.ashfaaq.android.fitnessappv2";

    //CONTENT URI FOR USER AND LOGS
    public static final Uri LOGS_CONTENT_URI= Uri.parse("content://" + AUTHORITY + "/logs");
    public static final Uri USER_CONTENT_URI= Uri.parse("content://" + AUTHORITY + "/user");

    //uriMatcher to match uri
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "logs", ALL_LOGS);
        uriMatcher.addURI(AUTHORITY, "logs/#", SINGLE_LOGS);
        uriMatcher.addURI(AUTHORITY, "user", ALL_USER);
        uriMatcher.addURI(AUTHORITY, "user/#", SINGLE_USER);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new fitnessDatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder, @Nullable CancellationSignal cancellationSignal) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int desc = 0;

        switch (uriMatcher.match(uri)) {
            case ALL_LOGS:
                queryBuilder.setTables(FitnessDB.LOG_TABLE);
                desc = 1;
                queryBuilder.appendWhere(FitnessDB.KEY_DISTANCE +" != 0" );
                break;
            case SINGLE_LOGS:
                //qeury for date
                if(uri.getPathSegments().get(1).equals("0")) {
                    String sql1 = "SELECT * FROM " + FitnessDB.LOG_TABLE + " WHERE " + FitnessDB.KEY_DATE + "= '"+ selection +"' AND " + FitnessDB.KEY_DISTANCE + " != 0";
                    Cursor cursor1 = db.rawQuery(sql1, selectionArgs);
                    return cursor1;
                }else if(uri.getPathSegments().get(1).equals("00")){
                    String sql1 = "SELECT SUM("+ FitnessDB.KEY_DISTANCE +") AS sum, MAX("+ FitnessDB.KEY_TIMERAN +") AS time FROM " + FitnessDB.LOG_TABLE + " WHERE " + FitnessDB.KEY_DATE + " LIKE '%/" + selection + "/%'";
                    Cursor cursor1 = db.rawQuery(sql1, selectionArgs);
                    return cursor1;
                } else if(uri.getPathSegments().get(1).equals("000")) {
                    String sql1 = "SELECT SUM("+ FitnessDB.KEY_DISTANCE +") AS sum, MAX("+ FitnessDB.KEY_TIMERAN +") AS time FROM " + FitnessDB.LOG_TABLE + " WHERE " + FitnessDB.KEY_DATE + " LIKE '" + selection + "'";
                    Cursor cursor1 = db.rawQuery(sql1, selectionArgs);
                    return cursor1;
                } else {
                     String row_id = uri.getPathSegments().get(1);
                    String sql1 = "SELECT * FROM " + FitnessDB.LOG_TABLE + " WHERE " + FitnessDB.KEY_ROWID + "= '"+ row_id +"'";
                    Cursor cursor1 = db.rawQuery(sql1, selectionArgs);
                    return cursor1;
                }

            case ALL_USER:
                queryBuilder.setTables(FitnessDB.USER_TABLE);
                //do nothing
                break;
            case SINGLE_USER:
                queryBuilder.setTables(FitnessDB.USER_TABLE);
                String user_id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(FitnessDB.KEY_ROWID + "=" + user_id);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if(desc == 1) {
            Cursor cursor = queryBuilder.query(db, projection, selection,
                    selectionArgs, null, null, FitnessDB.KEY_ROWID + " DESC");
            return cursor;
        }else {
            Cursor cursor = queryBuilder.query(db, projection, selection,
                    selectionArgs, null, null, sortOrder);
            return cursor;
        }


    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (uriMatcher.match(uri)) {
            case ALL_LOGS:
                return "vnd.android.cursor.dir/vnd.com.ashfaaq.android.fitnessappv2.logs";
            case SINGLE_LOGS:
                return "vnd.android.cursor.dir/vnd.com.ashfaaq.android.fitnessappv2.logs";
            case ALL_USER:
                return "vnd.android.cursor.dir/vnd.com.ashfaaq.android.fitnessappv2.user";
            case SINGLE_USER:
                return "vnd.android.cursor.dir/vnd.com.ashfaaq.android.fitnessappv2.user";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case ALL_LOGS:
                long log_id = db.insert(FitnessDB.LOG_TABLE, null, contentValues);
                getContext().getContentResolver().notifyChange(uri ,null);
                return Uri.parse(LOGS_CONTENT_URI + "/" + log_id);
            case ALL_USER:
                long user_id = db.insert(FitnessDB.USER_TABLE, null, contentValues);
                getContext().getContentResolver().notifyChange(uri ,null);
                return Uri.parse(USER_CONTENT_URI + "/" + user_id);
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String id;
        switch (uriMatcher.match(uri)) {
            case ALL_LOGS:
                //do nothing
                break;
            case SINGLE_LOGS:
                //Do nothing as we are not modifying logs
                break;
            case ALL_USER:
                //do nothing
                break;
            case SINGLE_USER:
                id = uri.getPathSegments().get(1);
                s = FitnessDB.KEY_ROWID + "=" + id
                        + (!TextUtils.isEmpty(s) ?
                        " AND (" + s + ')' : "");

                int user_updateCount = db.update(FitnessDB.USER_TABLE, contentValues, s, strings);
                getContext().getContentResolver().notifyChange(uri, null);
                return user_updateCount;
                //break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        return  0;
    }


}