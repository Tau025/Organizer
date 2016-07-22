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
 * Таблицы по клиентам нет. Храним только clientID и clientLookupKey в таблице PhotoSessionsTable
 */
public class Client implements Parcelable {
    private long id = -1;
    private String lookupKey = "";
    private String name = "";
    private String phone = "";
    private String address = "";
    private String social = "";
    private String email = "";
    private Calendar dateFirstMet = Calendar.getInstance();

    public Client(Parcel parcel) {
        id = parcel.readLong();
        lookupKey = parcel.readString();
        name = parcel.readString();
        phone = parcel.readString();
        address = parcel.readString();
        social = parcel.readString();
        email = parcel.readString();
        dateFirstMet = Calendar.getInstance();
        dateFirstMet.setTimeInMillis(parcel.readLong());
    }

    public Client(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        lookupKey = cursor.getString(cursor.getColumnIndex(LOOKUP_KEY));
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
        id = -1;
        lookupKey = "";
        dateFirstMet = Calendar.getInstance();
    }

    public Client(long id, String lookupKey, String name, String phone, String address,
                  String social, String email, Calendar dateFirstMet) {
        this.id = id;
        this.lookupKey = lookupKey;
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


    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getLookupKey() {
        return lookupKey;
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


    public void setId(long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
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
        if (obj == null || getClass() != obj.getClass() || id == -1 || "".equals(lookupKey)) return false;
        Client that = (Client) obj;
        if (that.id == -1 || "".equals(that.lookupKey)) return false;
        return id == that.id && lookupKey.equals(that.lookupKey);
    }

    @Override
    public int hashCode() {
        return (int) (id != -1 ? 31 * id : 0);
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(lookupKey);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(address);
        parcel.writeString(social);
        parcel.writeString(email);
        parcel.writeLong(dateFirstMet.getTimeInMillis());
    }
}
