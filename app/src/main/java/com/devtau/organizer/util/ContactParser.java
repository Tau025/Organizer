package com.devtau.organizer.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import com.devtau.organizer.model.Client;
import java.util.Calendar;
/**
 * Хелпер для получения данных по конкретному контакту
 * Поиск контакта по contactId и lookupKey вместе доступен только для имени контакта
 * Остальные данные запрашиваются с использованием только contactId и не гарантируют точный результат
 */
public abstract class ContactParser {
    private static final String LOG_TAG = ContactParser.class.getSimpleName();

    public static Client getContactInfo(Intent data, Activity activity) {
        CursorLoader cursorLoader = new CursorLoader(activity, data.getData(), null, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int columnID = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int columnLookupKey = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);

        long contactId = 0;
        String lookupKey = "";

        while(cursor.moveToNext()) {
            contactId = cursor.getLong(columnID);
            lookupKey = cursor.getString(columnLookupKey);
        }

        return getContactInfoById(contactId, lookupKey, activity);
    }


    //позволяет получить подробности по контакту, id и lookupKey которого нам уже известны
    public static Client getContactInfoById(long contactId, String lookupKey, Activity activity) {
//        Logger.d(LOG_TAG, "contactId: " + String.valueOf(contactId));
//        Logger.d(LOG_TAG, "lookupKey: " + lookupKey);
        ContentResolver cr = activity.getContentResolver();

        String name = getName(contactId, lookupKey, cr);
        String phoneNo = getPhoneNo(contactId, cr);
        String address = getAddress(contactId, cr);
        String website = getWebsite(contactId, cr);
        String email = getEmail(contactId, cr);

//        Logger.d(LOG_TAG, "name: " + name);
//        Logger.d(LOG_TAG, "phoneNo: " + phoneNo);
//        Logger.d(LOG_TAG, "address: " + address);
//        Logger.d(LOG_TAG, "website: " + website);
//        Logger.d(LOG_TAG, "email: " + email);
        Calendar now = Calendar.getInstance();

        return new Client(contactId, lookupKey, name, phoneNo, address, website, email, now);
    }


    //запросим и обработаем информацию касательно имени
    public static String getName(long contactId, String lookupKey, ContentResolver cr) {
        String name = "";
        Uri contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey);
        String[] projection = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
        Cursor cursor = cr.query(contactUri, projection, null, null, null);

//        Logger.d(LOG_TAG, "getName() cursor rows: " + String.valueOf(cursor.getCount()) + ", columns: " + String.valueOf(cursor.getColumnCount()));
        try {
            cursor.moveToFirst();
            name = cursor.getString(0);
        } finally {
            cursor.close();
        }
        return name;
    }


    //запросим и обработаем информацию касательно номера телефона
    public static String getPhoneNo(long contactId, ContentResolver cr) {
        String phoneNo = "";
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{String.valueOf(contactId),
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
        Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);

//        Logger.d(LOG_TAG, "getPhoneNo() cursor rows: " + String.valueOf(cursor.getCount()) + ", columns: " + String.valueOf(cursor.getColumnCount()));
        while(cursor.moveToNext()) {
            phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursor.close();
        return phoneNo;
    }


    //запросим и обработаем информацию касательно адреса
    public static String getAddress(long contactId, ContentResolver cr) {
        String address = "";
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{String.valueOf(contactId),
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
        Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);

//        Logger.d(LOG_TAG, "getAddress() cursor rows: " + String.valueOf(cursor.getCount()) + ", columns: " + String.valueOf(cursor.getColumnCount()));
        while(cursor.moveToNext()) {
            address = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
        }
        cursor.close();
        return address;
    }


    //запросим и обработаем информацию касательно сайта
    public static String getWebsite(long contactId, ContentResolver cr) {
        String website = "";
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{String.valueOf(contactId),
                ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE};
        Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);

//        Logger.d(LOG_TAG, "getWebsite() cursor rows: " + String.valueOf(cursor.getCount()) + ", columns: " + String.valueOf(cursor.getColumnCount()));
        while(cursor.moveToNext()) {
            website = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
        }
        cursor.close();
        return website;
    }


    //запросим и обработаем информацию касательно email
    public static String getEmail(long contactId, ContentResolver cr) {
        String email = "";
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{String.valueOf(contactId),
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
        Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
//        Logger.d(LOG_TAG, "getEmail() cursor rows: " + String.valueOf(cursor.getCount()) + ", columns: " + String.valueOf(cursor.getColumnCount()));

        while(cursor.moveToNext()) {
            email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
        }
        cursor.close();
        return email;
    }
}