package com.devtau.organizer.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.devtau.organizer.util.Util;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import static com.devtau.organizer.database.tables.PhotoSessionsTable.*;
/**
 * Главный класс хранимых объектов всего приложения
 */
public class PhotoSession implements Parcelable{
    private long photoSessionID = -1;
    private Calendar photoSessionDate;
    private Calendar deadline;

    //по двум полям ниже проверяется, привязан ли клиент к фотосессии
    private long clientID = 0;
    private String clientLookupKey = "";
    private int photoSessionTypeID;
    private String photoSessionAddress;
    private String presentToClientDescription;
    private int presentToClientCost;
    private int totalCost;
    private int balance;
    private int pricePerHour;
    private double hoursSpentPlan;
    private double hoursSpentFact;


    //конструкторы
    public PhotoSession(Parcel parcel) {
        photoSessionID = parcel.readLong();

        photoSessionDate = Calendar.getInstance();
        photoSessionDate.setTime(new Date(parcel.readLong()));

        deadline = Calendar.getInstance();
        deadline.setTime(new Date(parcel.readLong()));

        clientID = parcel.readLong();
        clientLookupKey = parcel.readString();
        photoSessionTypeID = parcel.readInt();
        photoSessionAddress = parcel.readString();
        presentToClientDescription = parcel.readString();
        presentToClientCost = parcel.readInt();
        totalCost = parcel.readInt();
        balance = parcel.readInt();
        pricePerHour = parcel.readInt();
        hoursSpentPlan = parcel.readDouble();
        hoursSpentFact = parcel.readDouble();
    }

    public PhotoSession(Calendar photoSessionDate) {
        photoSessionID = -1;
        this.photoSessionDate = photoSessionDate;
        deadline = Calendar.getInstance();
        if(photoSessionDate.after(deadline)) deadline = photoSessionDate;
    }

    public PhotoSession(Cursor cursor) {
        photoSessionID = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));

        //инициализируем ссылки
        photoSessionDate = Calendar.getInstance();
        deadline = Calendar.getInstance();

        //получим хранимые строки из БД
        String startDateString = cursor.getString(cursor.getColumnIndex(START_DATE));
        String endDateString = cursor.getString(cursor.getColumnIndex(DEADLINE));

        //присвоим значения датам
        try {
            photoSessionDate.setTime(Util.dateFormat.parse(startDateString));
            deadline.setTime(Util.dateFormat.parse(endDateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        clientID = cursor.getLong(cursor.getColumnIndex(CLIENT_ID));
        clientLookupKey = cursor.getString(cursor.getColumnIndex(CLIENT_LOOKUP_KEY));
        photoSessionTypeID = cursor.getInt(cursor.getColumnIndex(PHOTO_SESSION_TYPE_ID));
        photoSessionAddress = cursor.getString(cursor.getColumnIndex(PHOTO_SESSION_ADDRESS));
        presentToClientDescription = cursor.getString(cursor.getColumnIndex(PRESENT_TO_CLIENT_DESCRIPTION));
        presentToClientCost = cursor.getInt(cursor.getColumnIndex(PRESENT_TO_CLIENT_COST));
        totalCost = cursor.getInt(cursor.getColumnIndex(TOTAL_COST));
        balance = cursor.getInt(cursor.getColumnIndex(BALANCE));
        pricePerHour = cursor.getInt(cursor.getColumnIndex(PRICE_PER_HOUR));
        hoursSpentPlan = cursor.getDouble(cursor.getColumnIndex(HOURS_SPENT_PLAN));
        hoursSpentFact = cursor.getDouble(cursor.getColumnIndex(HOURS_SPENT_FACT));
    }

    public static final Creator<PhotoSession> CREATOR = new Creator<PhotoSession>() {
        @Override
        public PhotoSession createFromParcel(Parcel parcel) {
            return new PhotoSession(parcel);
        }

        @Override
        public PhotoSession[] newArray(int size) {
            return new PhotoSession[size];
        }
    };



    //сеттеры
    public void setPhotoSessionID(long photoSessionID) {
        this.photoSessionID = photoSessionID;
    }

    public void setPhotoSessionDate(Calendar photoSessionDate) {
        this.photoSessionDate = photoSessionDate;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public void setClientID(long clientID) {
        this.clientID = clientID;
    }

    public void setClientLookupKey(String clientLookupKey) {
        this.clientLookupKey = clientLookupKey;
    }

    public void setPhotoSessionTypeID(int photoSessionTypeID) {
        this.photoSessionTypeID = photoSessionTypeID;
    }

    public void setPhotoSessionAddress(String photoSessionAddress) {
        this.photoSessionAddress = photoSessionAddress;
    }

    public void setPresentToClientDescription(String presentToClientDescription) {
        this.presentToClientDescription = presentToClientDescription;
    }

    public void setPresentToClientCost(int presentToClientCost) {
        this.presentToClientCost = presentToClientCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setPricePerHour(int pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public void setHoursSpentPlan(double hoursSpentPlan) {
        this.hoursSpentPlan = hoursSpentPlan;
    }

    public void setHoursSpentFact(double hoursSpentFact) {
        this.hoursSpentFact = hoursSpentFact;
    }



    //геттеры
    public long getPhotoSessionID() {
        return photoSessionID;
    }

    public Calendar getPhotoSessionDate() {
        return photoSessionDate;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public long getClientID() {
        return clientID;
    }

    public String getClientLookupKey() {
        return clientLookupKey;
    }

    public int getPhotoSessionTypeID() {
        return photoSessionTypeID;
    }

    public String getPhotoSessionAddress() {
        return photoSessionAddress;
    }

    public String getPresentToClientDescription() {
        return presentToClientDescription;
    }

    public int getPresentToClientCost() {
        return presentToClientCost;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getBalance() {
        return balance;
    }

    public int getPricePerHour() {
        return pricePerHour;
    }

    public double getHoursSpentPlan() {
        return hoursSpentPlan;
    }

    public double getHoursSpentFact() {
        return hoursSpentFact;
    }

    //equals+hashCode необходимы в том числе для удаления из коллекции. например из адаптера recycler
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass() || photoSessionID == -1) return false;
        PhotoSession that = (PhotoSession) obj;
        if (that.photoSessionID == -1) return false;
        return photoSessionID == that.photoSessionID;
    }

    @Override
    public int hashCode() {
        return (int) (photoSessionID != -1 ? 31 * photoSessionID : 0);
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(photoSessionID);
        parcel.writeLong(photoSessionDate.getTimeInMillis());
        parcel.writeLong(deadline.getTimeInMillis());
        parcel.writeLong(clientID);
        parcel.writeString(clientLookupKey);
        parcel.writeInt(photoSessionTypeID);
        parcel.writeString(photoSessionAddress);
        parcel.writeString(presentToClientDescription);
        parcel.writeInt(presentToClientCost);
        parcel.writeInt(totalCost);
        parcel.writeInt(balance);
        parcel.writeInt(pricePerHour);
        parcel.writeDouble(hoursSpentPlan);
        parcel.writeDouble(hoursSpentFact);
    }

    @Override
    public String toString() {
        return "photoSessionID=" + photoSessionID
                + "\nphotoSessionDate=" + Util.dateFormat.format(photoSessionDate.getTime())
                + "\ndeadline=" + Util.dateFormat.format(deadline.getTime())
                + "\nclientID=" + clientID
                + "\nclientLookupKey=" + clientLookupKey
                + "\nphotoSessionTypeID=" + photoSessionTypeID
                + "\nphotoSessionAddress=" + photoSessionAddress
                + "\npresentToClientDescription=" + presentToClientDescription
                + "\npresentToClientCost=" + presentToClientCost
                + "\ntotalCost=" + totalCost
                + "\nbalance=" + balance
                + "\npricePerHour=" + pricePerHour
                + "\nhoursSpentPlan=" + hoursSpentPlan
                + "\nhoursSpentFact=" + hoursSpentFact;
    }
}
