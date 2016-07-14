package com.devtau.organizer.model;

import android.database.Cursor;
import android.provider.BaseColumns;
import com.devtau.organizer.util.Util;
import java.text.ParseException;
import java.util.Calendar;
import static com.devtau.organizer.database.tables.RemindersTable.*;
/**
 * Объекты этого класса - это поле reminders класса PhotoSession
 */
public class Reminder {
    private long reminderID = -1;
    private long taskID;
    private Calendar date;
    private String description;

    public Reminder(Cursor cursor) {
        taskID = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));

        date = Calendar.getInstance();
        String dateString = cursor.getString(cursor.getColumnIndex(DATE));
        try {
            date.setTime(Util.dateFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
    }

    public Reminder(long taskID, Calendar date, String description) {
        reminderID = -1;
        this.taskID = taskID;
        this.date = date;
        this.description = description;
    }


    //сеттеры
    public void setReminderID(long reminderID) {
        this.reminderID = reminderID;
    }

    public void setTaskID(long taskID) {
        this.taskID = taskID;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    //геттеры
    public long getReminderID() {
        return reminderID;
    }

    public long getTaskID() {
        return taskID;
    }

    public Calendar getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }


    //equals+hashCode необходимы в том числе для удаления из коллекции. например из адаптера recycler
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass() || taskID == -1) return false;
        Reminder that = (Reminder) obj;
        if (that.reminderID == -1) return false;
        return taskID == that.taskID;
    }

    @Override
    public int hashCode() {
        return (int) (taskID != -1 ? 31 * taskID : 0);
    }
}
