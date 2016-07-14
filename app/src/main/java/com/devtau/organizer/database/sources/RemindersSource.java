package com.devtau.organizer.database.sources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;
import android.widget.Toast;
import com.devtau.organizer.database.MySQLHelper;
import com.devtau.organizer.model.Reminder;
import com.devtau.organizer.util.Logger;
import com.devtau.organizer.util.Util;
import java.util.ArrayList;
import java.util.Calendar;
import static com.devtau.organizer.database.tables.RemindersTable.*;
/**
 * represents top level of abstraction from dataBase
 * all work with db layer must be done in this class
 * getReadableDatabase() and getWritableDatabase() must not be called outside this class
 */
public class RemindersSource {
    private static final String ERROR_TOAST = "db access error";
    private MySQLHelper dbHelper;
    private Context context;

    public RemindersSource(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = MySQLHelper.getInstance(this.context);
    }

    public void dropAllTablesInDB(){
        dbHelper.dropAllTablesInDB();
    }

    public long create(Reminder reminder){
        long id = -1;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = getContentValues(reminder);
            id = db.insert(TABLE_NAME, null, cv);
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return id;
    }

    public boolean update(Reminder reminder){
        int result = -1;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = getContentValues(reminder);
            result = db.update(TABLE_NAME, cv, BaseColumns._ID + " = ?",
                    new String[]{String.valueOf(reminder.getReminderID())});
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return result != -1;
    }

    public boolean remove(Reminder reminder){
        int result = -1;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(TABLE_NAME, BaseColumns._ID + " = ?",
                    new String[]{String.valueOf(reminder.getReminderID())});
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return result != -1;
    }

    public Reminder getReminderByID(long reminderID) {
        Reminder reminder = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + BaseColumns._ID + "='" + String.valueOf(reminderID) + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            reminder  = new Reminder(cursor);
        }
        cursor.close();
        return reminder;
    }

    public int getRemindersCount() {
        int count = 0;
        String selectQuery = "SELECT COUNT (*) FROM " + TABLE_NAME;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
//        Logger.d("RemindersSource.getRemindersCount() tasksCount: " + String.valueOf(tasksCount));
        return count;
    }

    public ArrayList<Reminder> getItemsList() {
        ArrayList<Reminder> list = new ArrayList<>();
        String sortMethod = "ASC";
        String selectQuery;
        selectQuery = "SELECT * FROM " + TABLE_NAME
                + " ORDER BY " + DATE + " " + sortMethod;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(new Reminder(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        Logger.d("PhotoSessionsSource.getTasksList() list.size(): " + String.valueOf(list.size()));
        return list;
    }

    public Cursor getRemindersCursor() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.query(TABLE_NAME, new String[]{BaseColumns._ID, DESCRIPTION},
                    null, null, null, null, null);
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return cursor;
    }
}
