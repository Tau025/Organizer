package com.devtau.organizer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.devtau.organizer.R;
import com.devtau.organizer.database.DataSource;
import com.devtau.organizer.model.PhotoSession;
import com.devtau.organizer.util.Constants;
import com.squareup.timessquare.CalendarPickerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {
    private CalendarPickerView calendar;
    private DataSource dataSource;
    private BroadcastReceiver receiver;
    private BroadcastReceiver myBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        dataSource = new DataSource(this);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        Date today = new Date();
        calendar.init(today, nextYear.getTime())
                .withSelectedDate(today)
                .inMode(CalendarPickerView.SelectionMode.SINGLE);

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date selectedDate) {
                //при старте активности подробностей на выбранную дату передадим этут дату в интенте
                Intent intent = new Intent(CalendarActivity.this, PhotoSessionsListActivity.class);
                intent.putExtra(Constants.SELECTED_DATE_EXTRA, selectedDate.getTime());
                startActivity(intent);
            }

            @Override
            public void onDateUnselected(Date date) {/*NOP*/}
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //обновляем выделенные цветом даты
                refreshHighlightedDates();
            }
        };
        registerReceiver(receiver, new IntentFilter(Constants.BROADCAST_REFRESH_TAG));
//        myBroadcastReceiver = new MyBroadcastReceiver();
//        registerReceiver(myBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        refreshHighlightedDates();
    }

    private void refreshHighlightedDates() {
        ArrayList<Date> datesWithTasks = new ArrayList<>();
        for (PhotoSession photoSession : dataSource.getPhotoSessionsSource().getItemsList()) {
            Date newDate = new Date(photoSession.getPhotoSessionDate().getTimeInMillis());
            if(!datesWithTasks.contains(newDate)) {
                datesWithTasks.add(newDate);
            }
        }
        calendar.clearHighlightedDates();
        calendar.highlightDates(datesWithTasks);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //деактивируем приемник(и) широковещательных сообщений
        unregisterReceiver(receiver);
//        unregisterReceiver(myBroadcastReceiver);
    }
}
