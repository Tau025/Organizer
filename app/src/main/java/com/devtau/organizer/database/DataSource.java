package com.devtau.organizer.database;

import android.content.Context;

import com.devtau.organizer.database.sources.ClientsSource;
import com.devtau.organizer.database.sources.RemindersSource;
import com.devtau.organizer.database.sources.PhotoSessionsSource;
import com.devtau.organizer.database.sources.TransactionsSource;
import com.devtau.organizer.model.Client;
import com.devtau.organizer.model.PhotoSession;
import com.devtau.organizer.model.Reminder;
import com.devtau.organizer.model.Transaction;
import com.devtau.organizer.util.Logger;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DataSource {
    private PhotoSessionsSource photoSessionsSource;
    private RemindersSource remindersSource;
    private TransactionsSource transactionsSource;
    private ClientsSource clientsSource;

    public DataSource(Context context) {
        photoSessionsSource = new PhotoSessionsSource(context);
        remindersSource = new RemindersSource(context);
        transactionsSource = new TransactionsSource(context);
        clientsSource = new ClientsSource(context);

        if (photoSessionsSource.getTasksCount() == 0) {
            photoSessionsSource.dropAllTablesInDB();
            populateDB();
        }
    }

    public PhotoSessionsSource getPhotoSessionsSource() {
        return photoSessionsSource;
    }
    public RemindersSource getRemindersSource() {
        return remindersSource;
    }
    public TransactionsSource getTransactionsSource() {
        return transactionsSource;
    }
    public ClientsSource getClientsSource() {
        return clientsSource;
    }

    private void populateDB() {
        addMockClients();

        addMockPhotoSession(createDate(5, 8, 2016, 8, 40), 1);
        addMockPhotoSession(createDate(7, 8, 2016, 10, 0), 2);
        addMockPhotoSession(createDate(8, 8, 2016, 12, 30), 3);
        addMockPhotoSession(createDate(9, 8, 2016, 9, 20), 1);
        addMockPhotoSession(createDate(10, 8, 2016, 10, 20), 2);

        addMockPhotoSession(createDate(5, 8, 2016, 20, 40), 3);
        addMockPhotoSession(createDate(7, 8, 2016, 15, 0), 1);
        addMockPhotoSession(createDate(8, 8, 2016, 17, 30), 2);
        addMockPhotoSession(createDate(9, 8, 2016, 19, 20), 3);
        addMockPhotoSession(createDate(10, 8, 2016, 17, 20), 3);
    }

    private Calendar createDate(int day, int month, int year, int hour, int minute) {
        return new GregorianCalendar(year, --month, day, hour, minute);//месяц +1
    }

    private void addMockPhotoSession(Calendar startDate, long clientID) {
        PhotoSession mockPhotoSession = new PhotoSession(startDate);//создадим PhotoSession
        mockPhotoSession.setClientID(clientID);

        Calendar deadLine = Calendar.getInstance();
        deadLine.setTime(startDate.getTime());
        deadLine.add(Calendar.DATE, 14);
        mockPhotoSession.setDeadline(deadLine);

        mockPhotoSession.setPhotoSessionAddress("some address which in fact can be very long");
        mockPhotoSession.setTotalCost(15000);
        mockPhotoSession.setPhotoSessionID(photoSessionsSource.create(mockPhotoSession));//сохраним его в бд
        addMockTransactions(mockPhotoSession);
        addMockReminders(mockPhotoSession);
    }

    private void addMockClients() {
        clientsSource.create(new Client("Denis Russkikh", "8921-978-1372", "Svetlanowsky av. 109-1-78",
                "vk.com/devtau", "d29025@yandex.ru", new GregorianCalendar(2016, 5, 10, 10, 20)));
        clientsSource.create(new Client("Jay Gatsby", "8921-978-9999", "West Egg, Long Island, New York, United States",
                "facebook.com/jgatz", "jgatz@gmail.com", new GregorianCalendar(1890, 6, 10, 10, 20)));
        clientsSource.create(new Client("Ernest Hemingway", "8911-555-9229", "Oak Park, Illinois, United States",
                "", "erhem@gmail.com", new GregorianCalendar(1899, 6, 21, 8, 0)));
    }

    private void addMockTransactions(PhotoSession photoSession) {
        transactionsSource.create(new Transaction(photoSession.getPhotoSessionID(), Calendar.getInstance(), 4000, "advance"));
        transactionsSource.create(new Transaction(photoSession.getPhotoSessionID(), Calendar.getInstance(), 8000, "after photo session"));
        transactionsSource.create(new Transaction(photoSession.getPhotoSessionID(), Calendar.getInstance(), 8000, "payoff"));
    }

    private void addMockReminders(PhotoSession photoSession) {
        //определим время для напоминаний
        Calendar mockReminderDate1 = photoSession.getDeadline();
        Calendar mockReminderDate2 = mockReminderDate1;
        mockReminderDate2.add(Calendar.MINUTE, -5);

        Logger.d("photoSession.getTaskID(): " + String.valueOf(photoSession.getPhotoSessionID()));
        //создадим Reminder
        Reminder mockReminder1 = new Reminder(photoSession.getPhotoSessionID(), mockReminderDate1, "photoSession end date");
        Reminder mockReminder2 = new Reminder(photoSession.getPhotoSessionID(), mockReminderDate2, "5min to photoSession end");

        //сохраним его в бд
        remindersSource.create(mockReminder1);
        remindersSource.create(mockReminder2);
    }
}
