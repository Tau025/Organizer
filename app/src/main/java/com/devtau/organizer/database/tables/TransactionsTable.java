package com.devtau.organizer.database.tables;

import android.content.ContentValues;
import android.provider.BaseColumns;
import com.devtau.organizer.database.MySQLHelper;
import com.devtau.organizer.model.Transaction;
import com.devtau.organizer.util.Util;

public abstract class TransactionsTable {
    public static final String TABLE_NAME = "Transactions";

    public static final String TASK_ID = "taskID";
    public static final String DATE = "date";
    public static final String AMOUNT = "amount";
    public static final String COMMENT = "comment";

    public static final String FIELDS = MySQLHelper.PRIMARY_KEY
            + TASK_ID + " INTEGER, "
            + DATE + " TEXT, "
            + AMOUNT + " INTEGER, "
            + COMMENT + " TEXT";

    public static ContentValues getContentValues(Transaction transaction) {
        ContentValues cv = new ContentValues();
        if (transaction.getTransactionID() != -1) {
            cv.put(BaseColumns._ID, transaction.getTransactionID());
        }
        cv.put(TASK_ID, transaction.getTaskID());
        cv.put(DATE, Util.dateFormat.format(transaction.getDate().getTime()));
        cv.put(AMOUNT, transaction.getAmount());
        cv.put(COMMENT, transaction.getComment());
        return cv;
    }
}
