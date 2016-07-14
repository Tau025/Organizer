package com.devtau.organizer.database.tables;

import android.content.ContentValues;
import android.provider.BaseColumns;
import com.devtau.organizer.database.MySQLHelper;
import com.devtau.organizer.model.Reminder;
import com.devtau.organizer.util.Util;

public abstract class RemindersTable {
    public static final String TABLE_NAME = "Reminders";

    public static final String TASK_ID = "taskId";
    public static final String DATE = "date";
    public static final String DESCRIPTION = "description";

    public static final String FIELDS = MySQLHelper.PRIMARY_KEY
            + TASK_ID + " INTEGER, "
            + DATE + " TEXT, "
            + DESCRIPTION + " TEXT";

    public static ContentValues getContentValues(Reminder reminder) {
        ContentValues cv = new ContentValues();
        if (reminder.getReminderID() != -1) {
            cv.put(BaseColumns._ID, reminder.getReminderID());
        }
        cv.put(TASK_ID, reminder.getTaskID());
        cv.put(DATE, Util.dateFormat.format(reminder.getDate().getTime()));
        cv.put(DESCRIPTION, reminder.getDescription());
        return cv;
    }
}
