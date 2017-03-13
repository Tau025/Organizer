package com.devtau.organizer.database.sources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.devtau.organizer.database.MySQLHelper;
import com.devtau.organizer.model.PhotoSession;
import com.devtau.organizer.util.Logger;
import com.devtau.organizer.util.Util;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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
            Logger.e(LOG_TAG, ERROR_TOAST, e);
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
            Logger.e(LOG_TAG, ERROR_TOAST, e);
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return result != -1;
    }

    public PhotoSession getPhotoSessionByID(long taskID) {
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + BaseColumns._ID + "='" + String.valueOf(taskID) + "'";
        Cursor cursor = queryDb(selectQuery);
        
        PhotoSession photoSession = null;
        if (cursor != null && cursor.moveToFirst()) {
            photoSession = new PhotoSession(cursor);
            cursor.close();
        }
        
        Logger.d(LOG_TAG, "getPhotoSessionByID() photoSession: " + photoSession);
        return photoSession;
    }

    public int getPhotoSessionsCount() {
        int count = 0;
        String selectQuery = "SELECT COUNT (*) FROM " + TABLE_NAME;
        Cursor cursor = queryDb(selectQuery);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        Logger.d(LOG_TAG, "getPhotoSessionsCount() count: " + String.valueOf(count));
        return count;
    }

    public ArrayList<PhotoSession> getItemsList() {
        String sortMethod = "ASC";
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " ORDER BY " + START_DATE + " " + sortMethod;
        Cursor cursor = queryDb(selectQuery);

        // looping through all rows and adding to list
        ArrayList<PhotoSession> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new PhotoSession(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        Logger.d(LOG_TAG, "getItemsList() list.size: " + String.valueOf(list.size()));
        return list;
    }

    public Cursor getPhotoSessionsCursorForADay(Calendar selectedDate) {
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(selectedDate.getTime());
        endOfDay.add(Calendar.DATE, 1);

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + START_DATE + ">='" + Util.dateFormat.format(selectedDate.getTime()) + "' AND "
                + START_DATE + "<'" + Util.dateFormat.format(endOfDay.getTime()) + "'";
        return queryDb(selectQuery);
    }

    public Observable<List<PhotoSession>> getPhotoSessionsListForADayAsync2(Calendar selectedDate) {
        return Observable.just(getPhotoSessionsListForADay(selectedDate))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<PhotoSession>> getPhotoSessionsListForADayAsync(Calendar selectedDate) {
        Callable<List<PhotoSession>> callableTasksList = () -> getPhotoSessionsListForADay(selectedDate);
        return makeObservable(callableTasksList)
                .subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread());
    }

    private List<PhotoSession> getPhotoSessionsListForADay(Calendar selectedDate) {
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(selectedDate.getTime());
        endOfDay.add(Calendar.DATE, 1);
        String sortMethod = "ASC";

        emulateHeavyCallToDB();

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + START_DATE + ">='" + Util.dateFormat.format(selectedDate.getTime()) + "' AND "
                + START_DATE + "<'" + Util.dateFormat.format(endOfDay.getTime()) + "'"
                + " ORDER BY " + START_DATE + " " + sortMethod;
        Cursor cursor = queryDb(selectQuery);

        // looping through all rows and adding to list
        ArrayList<PhotoSession> tasksList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                tasksList.add(new PhotoSession(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        Logger.d(LOG_TAG, "getPhotoSessionsListForADay() tasksList: " + String.valueOf(tasksList));
        return tasksList;
    }

    private void emulateHeavyCallToDB() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Date getFirstPhotoSessionDate() {
        String sortMethod = "ASC";
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " ORDER BY " + START_DATE + " " + sortMethod
                + " LIMIT 1";
        Cursor cursor = queryDb(selectQuery);

        Date firstPhotoSessionDate = null;
        if (cursor != null && cursor.moveToFirst()) {
            String firstPhotoSessionDateString = cursor.getString(cursor.getColumnIndex(START_DATE));
            try {
                firstPhotoSessionDate = Util.dateFormat.parse(firstPhotoSessionDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cursor.close();
        }

        Logger.d(LOG_TAG, "getFirstPhotoSessionDate() firstPhotoSessionDate: "
				+ (firstPhotoSessionDate == null ? "null" : Util.dateFormat.format(firstPhotoSessionDate.getTime())));
        return firstPhotoSessionDate;
    }

    public ArrayList<PhotoSession> getPhotoSessionsListToBeNotifiedNow(Calendar startOfMinute) {
        Calendar endOfMinute = Calendar.getInstance();
        endOfMinute.setTime(startOfMinute.getTime());
        endOfMinute.add(Calendar.MINUTE, 1);

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + DEADLINE + ">='" + Util.dateFormat.format(startOfMinute.getTime()) + "' AND "
                + DEADLINE + "<'" + Util.dateFormat.format(endOfMinute.getTime()) + "'";
        Cursor cursor = queryDb(selectQuery);

        // looping through all rows and adding to list
        ArrayList<PhotoSession> tasksList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                tasksList.add(new PhotoSession(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        Logger.d(LOG_TAG, "getPhotoSessionsListToBeNotifiedNow() tasksList: " + String.valueOf(tasksList));
        return tasksList;
    }


    @NonNull
    private <T> Observable<T> makeObservable(Callable<T> func) {
        return Observable.create(
                subscriber -> {
					try {
						T observed = func.call();
						if (observed != null) {
							subscriber.onNext(observed);
						}
						subscriber.onCompleted();
					} catch (Exception ex) {
						subscriber.onError(ex);
					}
                });
    }

    private Cursor queryDb(String selectQuery) {
        Logger.d(LOG_TAG, "selectQuery: " + String.valueOf(selectQuery));
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
        } catch (SQLiteException e) {
            Logger.e(LOG_TAG, ERROR_TOAST, e);
            Toast.makeText(context, ERROR_TOAST, Toast.LENGTH_SHORT).show();
        }
        return cursor;
    }
}
