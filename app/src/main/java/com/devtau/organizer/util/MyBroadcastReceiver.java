package com.devtau.organizer.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.devtau.organizer.R;
import com.devtau.organizer.activities.CalendarActivity;
import com.devtau.organizer.activities.PhotoSessionDetailsActivity;
import com.devtau.organizer.activities.PhotoSessionsListActivity;
import com.devtau.organizer.database.DataSource;
import com.devtau.organizer.model.PhotoSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
/**
 * MyBroadcastReceiver будет перезапускаться при перезагрузке девайса благодаря регистрации в манифесте
 * ресивера с интент-фильтром android.intent.action.BOOT_COMPLETED
 * в то время, как android.intent.action.TIME_TICK регистрировать в манифесте наоборот запрещено,
 * т.к. это вызовет ошибку выполнения
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //на выполнение всего метода у нас есть 10 секунд, иначе появится диалоговое окно о принудительном закрытии ANR
        String msg = "-";
        switch (intent.getAction()){
            case Intent.ACTION_TIME_TICK:
                Calendar now = Calendar.getInstance();
                Locale locale = context.getResources().getConfiguration().locale;
                msg = String.format(locale, context.getString(R.string.currentTime),
                        now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

                //сделаем запрос к бд, есть ли задачи с уведомлением в эту минуту
                DataSource dataSource = new DataSource(context);
                ArrayList<PhotoSession> tasksToBeNotifiedNow = dataSource.getPhotoSessionsSource().getTasksListToBeNotifiedNow(now);

                //TODO: тестовые строки
//                PhotoSession mockPhotoSession1 = new PhotoSession(new GregorianCalendar(2016, 6, 5, 9, 20));
//                mockPhotoSession1.setDescription("mockPhotoSession1");
//                tasksToBeNotifiedNow.add(mockPhotoSession1);
//
//                PhotoSession mockPhotoSession2 = new PhotoSession(new GregorianCalendar(2016, 6, 5, 9, 20));
//                mockPhotoSession2.setDescription("mockPhotoSession2");
//                tasksToBeNotifiedNow.add(mockPhotoSession2);

                //если такие задачи есть, для каждой создадим уведомление
                if(tasksToBeNotifiedNow.size() != 0) {
                    createNotificationsFromList(tasksToBeNotifiedNow, context);
                }
                break;

            case Intent.ACTION_BOOT_COMPLETED:
                //в этом блоке описывается, что нужно сделать устройству сразу после включения (перезагрузки)
//                context.startService(new Intent(context, NotificationService.class));

//                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//                Calendar now1 = Calendar.getInstance();
//                Intent myIntent = new Intent(context, PhotoSessionsListActivity.class);
//                PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, myIntent, 0);
//                manager.set(AlarmManager.RTC_WAKEUP, now1.getTimeInMillis(), mPendingIntent);
                break;
        }
    }

    private void createNotificationsFromList(ArrayList<PhotoSession> tasksToBeNotifiedNow, Context context) {
        //выбираем, в какую активность попадет пользователь после тапа на уведомлении
        //опционально указываем, какие еще активности нужно поместить в стэк возврата сверху-вниз
        Intent[] intents = {
                new Intent(context, CalendarActivity.class),//будет в стэке
                new Intent(context, PhotoSessionsListActivity.class),//будет в стэке
                new Intent(context, PhotoSessionDetailsActivity.class)//откроется
        };
        MyNotifier myNotifier = new MyNotifier(intents, "Вы просили напомнить", "");
        for (PhotoSession photoSession : tasksToBeNotifiedNow) {
            myNotifier.setContentText(photoSession.getPhotoSessionAddress());
            myNotifier.sendNotification(context);
        }
    }
}
