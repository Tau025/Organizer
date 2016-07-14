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
import com.devtau.organizer.model.Transaction;
import java.util.ArrayList;
import static com.devtau.organizer.database.tables.TransactionsTable.*;
/**
 * represents top level of abstraction from dataBase
 * all work with db layer must be done in this class
 * getReadableDatabase() and getWritableDatabase() must not be called outside this class
 */
public class TransactionsSource {
    private static final String ERROR_TOAST = "db access error";
    private MySQLHelper dbHelper;
    private Context context;

    public TransactionsSource(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = MySQLHelper.getInstance(this.context);
    }

    public void dropAllTablesInDB(){
        dbHelper.dropAllTablesInDB();
    }

    public long create(Transaction transaction){
        long id = -1;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = getContentValues(transaction);
            id = db.insert(TABLE_NAME, null, cv);
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return id;
    }

    public boolean update(Transaction transaction){
        int result = -1;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = getContentValues(transaction);
            result = db.update(TABLE_NAME, cv, BaseColumns._ID + " = ?",
                    new String[]{String.valueOf(transaction.getTransactionID())});
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return result != -1;
    }

    public boolean remove(Transaction transaction){
        int result = -1;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(TABLE_NAME, BaseColumns._ID + " = ?",
                    new String[]{String.valueOf(transaction.getTransactionID())});
        } catch (SQLiteException e) {
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return result != -1;
    }

    public Transaction getItemByID(long itemID) {
        Transaction transaction = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + BaseColumns._ID + "='" + String.valueOf(itemID) + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            transaction = new Transaction(cursor);
        }
        cursor.close();
        return transaction;
    }

    public ArrayList<Transaction> getTransactionsListForAPhotoSession(PhotoSession currentPhotoSession) {
        ArrayList<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + TASK_ID + "='" + String.valueOf(currentPhotoSession.getPhotoSessionID()) + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(new Transaction(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Transaction> getItemsList() {
        ArrayList<Transaction> list = new ArrayList<>();
        String sortMethod = "ASC";
        String selectQuery;
        selectQuery = "SELECT * FROM " + TABLE_NAME
                + " ORDER BY " + DATE + " " + sortMethod;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(new Transaction(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        Logger.d("TransactionsSource.getItemsList() list.size(): " + String.valueOf(list.size()));
        return list;
    }
}
