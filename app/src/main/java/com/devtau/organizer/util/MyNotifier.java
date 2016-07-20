package com.devtau.organizer.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;
import com.devtau.organizer.R;
import java.util.Calendar;
/**
 *
 */
public class MyNotifier {
    private static final String LOG_TAG = MyNotifier.class.getSimpleName();
    private int notifyId = 0;//недоступно извне

    private Intent[] intentStack;
    private String contentTitle = "Уведомление";//заголовок уведомления
    private String ticker = contentTitle;//текст в строке состояния. не работает в апи 21+
    private String contentText = "Что-то интересное произошло!";//текст уведомления

    private Calendar whenToShow = Calendar.getInstance();
    private Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//если скормить сюда правильный Uri, то можно воспроизводить произвольный звук уведомления
    private int smallIconResId = R.mipmap.ic_launcher;//выводится в строке состояния и в правой части открытого уведомления
    private int largeIconResId = android.R.drawable.ic_dialog_alert;
    private boolean autoCancel = true;
    private int maxProgress = 100;
    private long[] vibration = new long[] { 100, 500, 100, 200, 100, 500, 500, 1000 };//пауза-вибро-пауза-... необходимо разрешение в манифесте android.permission.VIBRATE
    private int maxNotificationsQueueLength = 10;//сообщений в очереди
    private int defaults = Notification.DEFAULT_SOUND;
//    Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE; | Notification.DEFAULT_LIGHTS; Notification.DEFAULT_ALL;

    public MyNotifier(Intent[] intentStack) {
        this.intentStack = intentStack;
    }

    public MyNotifier(Intent[] intentStack, String contentTitle, String contentText) {
        this.intentStack = intentStack;
        this.contentTitle = contentTitle;
        this.ticker = contentTitle;
        this.contentText = contentText;
    }

    public MyNotifier(Intent[] intentStack, String contentTitle, String ticker, String contentText,
                      Calendar whenToShow, Uri ringUri, int smallIconResId, int largeIconResId, boolean autoCancel,
                      int maxProgress, long[] vibration, int maxNotificationsQueueLength, int defaults) {
        if(isParamNotNull(intentStack)) this.intentStack = intentStack;
        if(isParamNotNull(contentTitle)) this.contentTitle = contentTitle;
        if(isParamNotNull(ticker)) this.ticker = ticker;
        if(isParamNotNull(contentText)) this.contentText = contentText;
        if(isParamNotNull(whenToShow)) this.whenToShow = whenToShow;
        if(isParamNotNull(ringUri)) this.ringUri = ringUri;
        if(isParamNotNull(smallIconResId)) this.smallIconResId = smallIconResId;
        if(isParamNotNull(largeIconResId)) this.largeIconResId = largeIconResId;
        if(isParamNotNull(autoCancel)) this.autoCancel = autoCancel;
        if(isParamNotNull(maxProgress)) this.maxProgress = maxProgress;
        if(isParamNotNull(vibration)) this.vibration = vibration;
        if(isParamNotNull(maxNotificationsQueueLength)) this.maxNotificationsQueueLength = maxNotificationsQueueLength;
        if(isParamNotNull(defaults)) this.defaults = defaults;
    }

    private boolean isParamNotNull(Object parameter) {
        if(parameter instanceof Boolean) {
            Logger.d(LOG_TAG, "Boolean");
            return true;
        }
        else if(parameter instanceof Integer) {
            Logger.d(LOG_TAG, "Integer");
            return 0 != (int) parameter;
        }
        else if(parameter instanceof String) {
            Logger.d(LOG_TAG, "String");
            return !"".equals(parameter);
        }
        else {
            Logger.d(LOG_TAG, "Object: Calendar, Intent[], long[], Uri");
            return null != parameter;
        }
    }


    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public void sendNotification(Context context) {
        //соберем уведомление
        Notification.Builder builder = new Notification.Builder(context)
                .setContentIntent(formBackStack(intentStack, context))
                .setContentTitle(contentTitle)
                .setTicker(ticker)
                .setContentText(contentText)
                .setWhen(whenToShow.getTimeInMillis())

                .setSmallIcon(smallIconResId)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIconResId))
                .setAutoCancel(autoCancel)
                .setProgress(maxProgress, notifyId * 20, false);
        Notification notification = builder.build();// требуется API 16+

        notification.sound = ringUri;
        notification.vibrate = vibration;
        notification.defaults = defaults;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notifyId, notification);
        notificationManager.cancel(notifyId - maxNotificationsQueueLength);
        notifyId++;
    }

    private PendingIntent formBackStack(Intent[] intents, Context context) {
        //подготовим очередь возврата, добавив к запускаемому намерению SecondActivity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(SecondActivity.class);
        //строим очередь сверху-вниз
        for (Intent intent: intents) {
            stackBuilder.addNextIntent(intent);
        }
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
