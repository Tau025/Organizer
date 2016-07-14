package com.devtau.organizer.database.tables;

import android.content.ContentValues;
import android.provider.BaseColumns;
import com.devtau.organizer.database.MySQLHelper;
import com.devtau.organizer.model.PhotoSession;
import com.devtau.organizer.util.Util;

public abstract class PhotoSessionsTable {
    public static final String TABLE_NAME = "PhotoSessions";

    public static final String START_DATE = "startDate";
    public static final String DEADLINE = "deadline";
    public static final String CLIENT_ID = "clientID";
    public static final String PHOTO_SESSION_TYPE_ID = "photoSessionTypeID";
    public static final String PHOTO_SESSION_ADDRESS = "photoSessionAddress";
    public static final String PRESENT_TO_CLIENT_DESCRIPTION = "presentToClientDescription";
    public static final String PRESENT_TO_CLIENT_COST = "presentToClientCost";
    public static final String TOTAL_COST = "totalCost";
    public static final String BALANCE = "balance";
    public static final String PRICE_PER_HOUR = "pricePerHour";
    public static final String HOURS_SPENT_PLAN = "hoursSpentPlan";
    public static final String HOURS_SPENT_FACT = "hoursSpentFact";


    public static final String FIELDS = MySQLHelper.PRIMARY_KEY
            + START_DATE + " TEXT, "
            + DEADLINE + " TEXT, "
            + CLIENT_ID + " INTEGER, "
            + PHOTO_SESSION_TYPE_ID + " INTEGER, "
            + PHOTO_SESSION_ADDRESS + " TEXT, "
            + PRESENT_TO_CLIENT_DESCRIPTION + " TEXT, "
            + PRESENT_TO_CLIENT_COST + " INTEGER, "
            + TOTAL_COST + " INTEGER, "
            + BALANCE + " INTEGER, "
            + PRICE_PER_HOUR + " INTEGER, "
            + HOURS_SPENT_PLAN + " REAL, "
            + HOURS_SPENT_FACT + " REAL";

    public static ContentValues getContentValues(PhotoSession photoSession) {
        ContentValues cv = new ContentValues();
        if (photoSession.getPhotoSessionID() != -1) {
            cv.put(BaseColumns._ID, photoSession.getPhotoSessionID());
        }
        cv.put(START_DATE, Util.dateFormat.format(photoSession.getPhotoSessionDate().getTime()));
        cv.put(DEADLINE, Util.dateFormat.format(photoSession.getDeadline().getTime()));
        cv.put(CLIENT_ID, photoSession.getClientID());
        cv.put(PHOTO_SESSION_TYPE_ID, photoSession.getPhotoSessionTypeID());
        cv.put(PHOTO_SESSION_ADDRESS, photoSession.getPhotoSessionAddress());
        cv.put(PRESENT_TO_CLIENT_DESCRIPTION, photoSession.getPresentToClientDescription());
        cv.put(PRESENT_TO_CLIENT_COST, photoSession.getPresentToClientCost());
        cv.put(TOTAL_COST, photoSession.getTotalCost());
        cv.put(BALANCE, photoSession.getBalance());
        cv.put(PRICE_PER_HOUR, photoSession.getPricePerHour());
        cv.put(HOURS_SPENT_PLAN, photoSession.getHoursSpentPlan());
        cv.put(HOURS_SPENT_FACT, photoSession.getHoursSpentFact());
        return cv;
    }
}
