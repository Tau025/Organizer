package com.devtau.organizer.database.tables;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.devtau.organizer.database.MySQLHelper;
import com.devtau.organizer.model.Client;
import com.devtau.organizer.model.Transaction;
import com.devtau.organizer.util.Util;

public abstract class ClientsTable {
    public static final String TABLE_NAME = "Clients";

    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final String SOCIAL = "social";
    public static final String EMAIL = "email";
    public static final String DATE_FIRST_MET = "dateFirstMet";

    public static final String FIELDS = MySQLHelper.PRIMARY_KEY
            + NAME + " TEXT, "
            + PHONE + " TEXT, "
            + ADDRESS + " TEXT, "
            + SOCIAL + " TEXT, "
            + EMAIL + " TEXT, "
            + DATE_FIRST_MET + " TEXT";

    public static ContentValues getContentValues(Client client) {
        ContentValues cv = new ContentValues();
        if (client.getClientID() != -1) {
            cv.put(BaseColumns._ID, client.getClientID());
        }
        cv.put(NAME, client.getName());
        cv.put(PHONE, client.getPhone());
        cv.put(ADDRESS, client.getAddress());
        cv.put(SOCIAL, client.getSocial());
        cv.put(EMAIL, client.getEmail());
        cv.put(DATE_FIRST_MET, Util.dateFormat.format(client.getDateFirstMet().getTime()));
        return cv;
    }
}
