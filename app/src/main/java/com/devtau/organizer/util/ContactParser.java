package com.devtau.organizer.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import com.devtau.organizer.model.Client;
import java.util.Calendar;
/**
 * хелпер для получения данных по конкретному контакту
 */
public abstract class ContactParser {
    private static final String LOG_TAG = ContactParser.class.getSimpleName();

    public static Client getContactInfo(Intent data, Activity activity) {
        ContentResolver cr = activity.getContentResolver();
        CursorLoader cursorLoader = new CursorLoader(activity, data.getData(), null, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int columnID = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int columnName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        int columnHasPhoneNumber = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

        String contactId = "";
        String name = "";
        String hasPhone = "";

        while (cursor.moveToNext()) {
            contactId = cursor.getString(columnID);
            name = cursor.getString(columnName);
            hasPhone = cursor.getString(columnHasPhoneNumber);

//            Logger.d(LOG_TAG, "contactId: " + String.valueOf(contactId));
//            Logger.d(LOG_TAG, "hasPhone: " + String.valueOf(hasPhone));
        }

        String phoneNo = "";
        if (hasPhone.equalsIgnoreCase("1")) {
            phoneNo = ContactParser.getPhoneNo(contactId, cr);
        }
        String address = ContactParser.getAddress(contactId, cr);
        String website = ContactParser.getWebsite(contactId, cr);
        String email = ContactParser.getEmail(contactId, cr);

//        Logger.d(LOG_TAG, "name: " + name);
//        Logger.d(LOG_TAG, "phoneNo: " + phoneNo);
//        Logger.d(LOG_TAG, "address: " + address);
//        Logger.d(LOG_TAG, "website: " + website);
//        Logger.d(LOG_TAG, "email: " + email);
        Calendar now = Calendar.getInstance();

        Client client = new Client(name, phoneNo, address, website, email, now);
        client.setClientID(Long.valueOf(contactId));
        return client;
    }


    //позволяет получить подробности по контакту, id которого нам уже известен
    public static Client getContactInfoById(String contactId, Activity activity) {
//        Logger.d(LOG_TAG, "contactId: " + String.valueOf(contactId));
        ContentResolver cr = activity.getContentResolver();

        String name = getName(contactId, cr);
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

        Client client = new Client(name, phoneNo, address, website, email, now);
        client.setClientID(Long.valueOf(contactId));
        return client;
    }


    //запросим и обработаем информацию касательно имени
    public static String getName(String contactId, ContentResolver cr) {
        String name = "";
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        Cursor cursor = cr.query(contactUri, null, null, null, null);

//        Logger.d(LOG_TAG, "getName() cursor rows: " + String.valueOf(cursor.getCount()) + ", columns: " + String.valueOf(cursor.getColumnCount()));
        while(cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        return name;
    }


    //запросим и обработаем информацию касательно номера телефона
    public static String getPhoneNo(String contactId, ContentResolver cr) {
        String phoneNo = "";
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{contactId,
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
    public static String getAddress(String contactId, ContentResolver cr) {
        String address = "";
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{contactId,
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
    public static String getWebsite(String contactId, ContentResolver cr) {
        String website = "";
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{contactId,
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
    public static String getEmail(String contactId, ContentResolver cr) {
        String email = "";
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{contactId,
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