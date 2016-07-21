package com.devtau.organizer.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import com.devtau.organizer.model.Client;
import java.util.Calendar;
/**
 * хелпер для получения данных по конкретному контакту
 */
public abstract class ContactParser {
    private static final String LOG_TAG = ContactParser.class.getSimpleName();

    public static Client getContactInfo(Intent data, Activity activity) {
        ContentResolver cr = activity.getContentResolver();
        Cursor cursor = activity.managedQuery(data.getData(), null, null, null, null);

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

            Logger.d(LOG_TAG, "contactId: " + String.valueOf(contactId));
            Logger.d(LOG_TAG, "hasPhone: " + String.valueOf(hasPhone));
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
        Cursor cursor = cr.query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

        while(cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        return name;
    }


    //запросим и обработаем информацию касательно номера телефона
    public static String getPhoneNo(String contactId, ContentResolver cr) {
        String phoneNo = "";
        Cursor cursor = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

        while(cursor.moveToNext()) {
            phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursor.close();
        return phoneNo;
    }


    //запросим и обработаем информацию касательно адреса
    public static String getAddress(String contactId, ContentResolver cr) {
        StringBuilder builder = new StringBuilder();
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{contactId,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
        Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);

        while(cursor.moveToNext()) {
            String formatter = "%s, ";
            String poBox = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
            builder.append(String.format(formatter, poBox));

            //самая главная часть адреса
            String street = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.StructuredPostal.STREET));
            builder.append(String.format(formatter, street));

            String city = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.StructuredPostal.CITY));
            builder.append(String.format(formatter, city));

            String state = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.StructuredPostal.REGION));
            builder.append(String.format(formatter, state));

            String postalCode = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
            builder.append(String.format(formatter, postalCode));

            String country = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
            builder.append(String.format(formatter, country));

            String type = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
            builder.append(String.format(formatter, type));
        }
        cursor.close();
        return builder.toString();
    }


    //запросим и обработаем информацию касательно сайта
    public static String getWebsite(String contactId, ContentResolver cr) {
        String website = "";
        Cursor cursor = cr.query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Website.CONTACT_ID + " = ?",
                new String[]{contactId}, null);

        while(cursor.moveToNext()) {
            website = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
        }
        cursor.close();
        return website;
    }


    //запросим и обработаем информацию касательно email
    public static String getEmail(String contactId, ContentResolver cr) {
        String email = "";
        Cursor cursor = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contactId}, null);

        while(cursor.moveToNext()) {
            email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
        }
        cursor.close();
        return email;
    }
}