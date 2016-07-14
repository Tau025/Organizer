package com.devtau.organizer.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.devtau.organizer.R;
import com.devtau.organizer.util.Util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import static com.devtau.organizer.database.tables.TransactionsTable.*;
/**
 * Отражает одну передачу денег от клиента фотографу
 */
public class Transaction implements Parcelable {
    private long transactionID = -1;
    private long taskID;
    private Calendar date;
    private int amount;
    private String comment;

    public Transaction(Parcel parcel) {
        transactionID = parcel.readLong();
        taskID = parcel.readLong();
        date = Calendar.getInstance();
        date.setTimeInMillis(parcel.readLong());
        amount = parcel.readInt();
        comment = parcel.readString();
    }

    public Transaction(Cursor cursor) {
        transactionID = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        taskID = cursor.getLong(cursor.getColumnIndex(TASK_ID));

        //инициализируем ссылки
        date = Calendar.getInstance();
        //получим хранимые строки из БД
        String dateString = cursor.getString(cursor.getColumnIndex(DATE));
        //присвоим значение дате
        try {
            date.setTime(Util.dateFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        amount = cursor.getInt(cursor.getColumnIndex(AMOUNT));
        comment = cursor.getString(cursor.getColumnIndex(COMMENT));
    }

    public Transaction(long taskID) {
        transactionID = -1;
        this.taskID = taskID;
        date = Calendar.getInstance();
    }

    public Transaction(long taskID, Calendar date, int amount, String comment) {
        transactionID = -1;
        this.taskID = taskID;
        this.date = date;
        this.amount = amount;
        this.comment = comment;
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel parcel) {
            return new Transaction(parcel);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public long getTransactionID() {
        return transactionID;
    }
    public long getTaskID() {
        return taskID;
    }
    public Calendar getDate() {
        return date;
    }
    public int getAmount() {
        return amount;
    }
    public String getComment() {
        return comment;
    }


    public void setTransactionID(long transactionID) {
        this.transactionID = transactionID;
    }
    public void setTaskID(long taskID) {
        this.taskID = taskID;
    }
    public void setDate(Calendar date) {
        this.date = date;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }


    public String toString(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return String.format(locale, context.getResources().getString(R.string.transactionToStringFormatter),
                Util.dateFormat.format(date.getTime()), amount, comment);
    }

    //equals+hashCode необходимы в том числе для удаления из коллекции. например из адаптера recycler
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass() || transactionID == -1) return false;
        Transaction that = (Transaction) obj;
        if (that.transactionID == -1) return false;
        return transactionID == that.transactionID;
    }

    @Override
    public int hashCode() {
        return (int) (transactionID != -1 ? 31 * transactionID : 0);
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(transactionID);
        parcel.writeLong(taskID);
        parcel.writeLong(date.getTimeInMillis());
        parcel.writeInt(amount);
        parcel.writeString(comment);
    }
}
