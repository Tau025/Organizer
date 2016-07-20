package com.devtau.organizer.database.sources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;
import android.widget.Toast;

import com.devtau.organizer.database.MySQLHelper;
import com.devtau.organizer.model.Client;
import com.devtau.organizer.util.Logger;

import java.util.ArrayList;
import static com.devtau.organizer.database.tables.ClientsTable.*;
/**
 * represents top level of abstraction from dataBase
 * all work with db layer must be done in this class
 * getReadableDatabase() and getWritableDatabase() must not be called outside this class
 */
public class ClientsSource {
    private static final String ERROR_TOAST = "db access error";
    private static final String LOG_TAG = ClientsSource.class.getSimpleName();
    private MySQLHelper dbHelper;
    private Context context;

    public ClientsSource(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = MySQLHelper.getInstance(this.context);
    }

    public void dropAllTablesInDB(){
        dbHelper.dropAllTablesInDB();
    }

    public long create(Client client){
        long id = -1;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = getContentValues(client);
            id = db.insert(TABLE_NAME, null, cv);
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return id;
    }

    public boolean update(Client client){
        int result = -1;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = getContentValues(client);
            result = db.update(TABLE_NAME, cv, BaseColumns._ID + " = ?",
                    new String[]{String.valueOf(client.getClientID())});
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return result != -1;
    }

    public boolean remove(Client client){
        int result = -1;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(TABLE_NAME, BaseColumns._ID + " = ?",
                    new String[]{String.valueOf(client.getClientID())});
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return result != -1;
    }

    public Client getItemByID(long itemID) {
        Client client = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + BaseColumns._ID + "='" + String.valueOf(itemID) + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            client = new Client(cursor);
        }
        cursor.close();
        return client;
    }

    public ArrayList<Client> getClientsList() {
        ArrayList<Client> list = new ArrayList<>();
        String selectQuery;
        selectQuery = "SELECT * FROM " + TABLE_NAME
                + " ORDER BY " + NAME + " " + "ASC";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(new Client(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Logger.d(LOG_TAG, "ClientsSource.getClientsList() list.size(): " + String.valueOf(list.size()));
        return list;
    }
}
