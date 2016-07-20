package com.devtau.organizer.database.sources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;
import android.widget.Toast;
import com.devtau.organizer.database.MySQLHelper;
import com.devtau.organizer.model.PhotoSession;
import com.devtau.organizer.util.Logger;
import com.devtau.organizer.util.Util;
import java.util.ArrayList;
import java.util.Calendar;
import static com.devtau.organizer.database.tables.PhotoSessionsTable.*;
/**
 * represents top level of abstraction from dataBase
 * all work with db layer must be done in this class
 * getReadableDatabase() and getWritableDatabase() must not be called outside this class
 */
public class PhotoSessionsSource {
    private static final String ERROR_TOAST = "db access error";
    private static final String LOG_TAG = PhotoSessionsSource.class.getSimpleName();
    private MySQLHelper dbHelper;
    private Context context;

    public PhotoSessionsSource(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = MySQLHelper.getInstance(this.context);
    }

    public void dropAllTablesInDB(){
        dbHelper.dropAllTablesInDB();
    }

    public long create(PhotoSession photoSession){
        long id = -1;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = getContentValues(photoSession);
            id = db.insert(TABLE_NAME, null, cv);
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return id;
    }

    public boolean update(PhotoSession photoSession){
        int result = -1;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = getContentValues(photoSession);
            result = db.update(TABLE_NAME, cv, BaseColumns._ID + " = ?", new String[]{String.valueOf(photoSession.getPhotoSessionID())});
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return result != -1;
    }

    public boolean remove(PhotoSession photoSession){
        int result = -1;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(photoSession.getPhotoSessionID())});
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return result != -1;
    }

    public PhotoSession getTaskByID(long taskID) {
        PhotoSession photoSession = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + BaseColumns._ID + "='" + String.valueOf(taskID) + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            photoSession = new PhotoSession(cursor);
        }
        cursor.close();
        return photoSession;
    }

    public int getTasksCount() {
        int count = 0;
        String selectQuery = "SELECT COUNT (*) FROM " + TABLE_NAME;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
//        Logger.d("PhotoSessionsSource.getTasksCount() count: " + String.valueOf(count));
        return count;
    }

    public ArrayList<PhotoSession> getItemsList() {
        ArrayList<PhotoSession> list = new ArrayList<>();
        String sortMethod = "ASC";
        String selectQuery;
        selectQuery = "SELECT * FROM " + TABLE_NAME
                + " ORDER BY " + START_DATE + " " + sortMethod;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(new PhotoSession(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        Logger.d("PhotoSessionsSource.getTasksList() list.size(): " + String.valueOf(list.size()));
        return list;
    }

    public Cursor getTasksCursorForADay(Calendar selectedDate) {
        Cursor cursor = null;
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(selectedDate.getTime());
        endOfDay.add(Calendar.DATE, 1);

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + START_DATE + ">='" + Util.dateFormat.format(selectedDate.getTime()) + "' AND "
                + START_DATE + "<'" + Util.dateFormat.format(endOfDay.getTime()) + "'";

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }

        // looping through all rows and adding to list
        ArrayList<PhotoSession> tasksList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                tasksList.add(new PhotoSession(cursor));
            } while (cursor.moveToNext());
        }
        
//        Logger.d("tasksList: " + String.valueOf(tasksList));
        return cursor;
    }

    public ArrayList<PhotoSession> getTasksListForADay(Calendar selectedDate) {
        Cursor cursor = null;
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(selectedDate.getTime());
        endOfDay.add(Calendar.DATE, 1);
        String sortMethod = "ASC";

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + START_DATE + ">='" + Util.dateFormat.format(selectedDate.getTime()) + "' AND "
                + START_DATE + "<'" + Util.dateFormat.format(endOfDay.getTime()) + "'"
                + " ORDER BY " + START_DATE + " " + sortMethod;

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }

        // looping through all rows and adding to list
        ArrayList<PhotoSession> tasksList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                tasksList.add(new PhotoSession(cursor));
            } while (cursor.moveToNext());
        }

//        Logger.d("tasksList: " + String.valueOf(tasksList));
        return tasksList;
    }

    public ArrayList<PhotoSession> getTasksListToBeNotifiedNow(Calendar startOfMinute) {
        Cursor cursor = null;
        Calendar endOfMinute = Calendar.getInstance();
        endOfMinute.setTime(startOfMinute.getTime());
        endOfMinute.add(Calendar.MINUTE, 1);

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + DEADLINE + ">='" + Util.dateFormat.format(startOfMinute.getTime()) + "' AND "
                + DEADLINE + "<'" + Util.dateFormat.format(endOfMinute.getTime()) + "'";
        Logger.d(LOG_TAG, "selectQuery: " + String.valueOf(selectQuery));

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }

        // looping through all rows and adding to list
        ArrayList<PhotoSession> tasksList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                tasksList.add(new PhotoSession(cursor));
            } while (cursor.moveToNext());
        }

        Logger.d(LOG_TAG, "tasksList: " + String.valueOf(tasksList));
        return tasksList;
    }
}
