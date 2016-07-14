package com.devtau.organizer.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.devtau.organizer.util.Util;
import java.text.ParseException;
import java.util.Calendar;
import static com.devtau.organizer.database.tables.ClientsTable.*;
/**
 * Все данные, связанные с заказчиком
 * Нового заказчика мы добавляем в базу в момент сохранения заказа и только если у него есть имя
 */
public class Client implements Parcelable {
    private long clientID = -1;
    private String name = "";
    private String phone = "";
    private String address = "";
    private String social = "";
    private String email = "";
    private Calendar dateFirstMet = Calendar.getInstance();

    public Client(Parcel parcel) {
        clientID = parcel.readLong();
        name = parcel.readString();
        phone = parcel.readString();
        address = parcel.readString();
        social = parcel.readString();
        email = parcel.readString();
        dateFirstMet = Calendar.getInstance();
        dateFirstMet.setTimeInMillis(parcel.readLong());
    }

    public Client(Cursor cursor) {
        clientID = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        name = cursor.getString(cursor.getColumnIndex(NAME));
        phone = cursor.getString(cursor.getColumnIndex(PHONE));
        address = cursor.getString(cursor.getColumnIndex(ADDRESS));
        social = cursor.getString(cursor.getColumnIndex(SOCIAL));
        email = cursor.getString(cursor.getColumnIndex(EMAIL));

        dateFirstMet = Calendar.getInstance();
        String dateString = cursor.getString(cursor.getColumnIndex(DATE_FIRST_MET));
        try {
            dateFirstMet.setTime(Util.dateFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Client() {
        clientID = -1;
        dateFirstMet = Calendar.getInstance();
    }

    public Client(String name, String phone, String address, String social, String email, Calendar dateFirstMet) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.social = social;
        this.email = email;
        this.dateFirstMet = dateFirstMet;
    }

    public static final Creator<Client> CREATOR = new Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel parcel) {
            return new Client(parcel);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };


    public long getClientID() {
        return clientID;
    }
    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }
    public String getSocial() {
        return social;
    }
    public String getEmail() {
        return email;
    }
    public Calendar getDateFirstMet() {
        return dateFirstMet;
    }


    public void setClientID(long clientID) {
        this.clientID = clientID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setSocial(String social) {
        this.social = social;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setDateFirstMet(Calendar dateFirstMet) {
        this.dateFirstMet = dateFirstMet;
    }

    @Override
    public String toString() {
        return String.format("имя: %s, тф: %s, адрес: %s, соц.сеть: %s, email: %s, email: %s, знакомство: ",
                name, phone, address, social, email, Util.dateFormat.format(dateFirstMet.getTime()));
    }

    //equals+hashCode необходимы в том числе для удаления из коллекции. например из адаптера recycler
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass() || clientID == -1) return false;
        Client that = (Client) obj;
        if (that.clientID == -1) return false;
        return clientID == that.clientID;
    }

    @Override
    public int hashCode() {
        return (int) (clientID != -1 ? 31 * clientID : 0);
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(clientID);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(address);
        parcel.writeString(social);
        parcel.writeString(email);
        parcel.writeLong(dateFirstMet.getTimeInMillis());
    }
}
