package com.shukhrat.wbpvp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "feedback_table";
    private static final String COL0 = "ID";
    private static final String COL1 = "feedback_title";
    private static final String COL2 = "feedback_description";
    private static final String COL3 = "image";
    private static final String COL4 = "timestamp";
    private static final String COL5 = "location";
    private static final String COL6 = "date";
    private static final String COL7 = "status";
    private static final String COL8 = "anonymous";
    private static final String COL9 = "status_anonymous";

    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "+ TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL1 + " TEXT, "
                + COL2 + " TEXT, "
                + COL3 + " TEXT, "
                + COL4 + " TEXT, "
                + COL5 + " TEXT, "
                + COL6 + " TEXT, "
                + COL7 + " BOOLEAN, "
                + COL8 + " BOOLEAN, "
                + COL9 + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String title, String description, String image, String timeStamp, String location, String date, Boolean status, Boolean anonymous, String status_anonymous){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, title);
        contentValues.put(COL2, description);
        contentValues.put(COL3, image);
        contentValues.put(COL4, timeStamp);
        contentValues.put(COL5, location);
        contentValues.put(COL6, date);
        contentValues.put(COL7, status);
        contentValues.put(COL8, anonymous);
        contentValues.put(COL9, status_anonymous);


        long result = db.insert(TABLE_NAME, null, contentValues);

        //if data inserted incorrectly it will return -1
        if (result ==-1){
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME;
        Cursor data = database.rawQuery(query,null);
        return data;
    }

}
