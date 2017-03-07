package com.devtau.organizer.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.devtau.organizer.database.DataSource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public abstract class Util {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
    private static final String LOG_TAG = Util.class.getSimpleName();

    public static void notifyBroadcastListeners(Context context) {
        Intent responseIntent = new Intent();
        responseIntent.setAction(Constants.BROADCAST_REFRESH_TAG);//тэг, используемый в IntentFilter приемника
        context.sendBroadcast(responseIntent);
    }

    public static String getStringDateTimeFromCal(Calendar date){
        return String.format(Locale.getDefault(), "%02d.%02d %02d:%02d",
                date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH) + 1,
                date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE));
    }

    public static void hideSoftKeyboard(final Activity activity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                View focused = activity.getCurrentFocus();
                if (focused != null) {
                    focused.clearFocus();
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focused.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                imm.hideSoftInputFromWindow(focused.getWindowToken(), 0);
                }
            }
        }, 200);
    }

    public static void logDate(String dateName, Calendar dateToLog, Context context){
        Locale locale = context.getResources().getConfiguration().locale;
        String log = String.format(locale, "%02d.%02d.%04d %02d:%02d:%02d", dateToLog.get(Calendar.DAY_OF_MONTH),
                dateToLog.get(Calendar.MONTH) + 1, dateToLog.get(Calendar.YEAR), dateToLog.get(Calendar.HOUR_OF_DAY),
                dateToLog.get(Calendar.MINUTE), dateToLog.get(Calendar.SECOND));
        if (dateName.length() >= 20) {
            Logger.d(LOG_TAG, dateName + log);
        } else {
            while (dateName.length() < 20) dateName += '.';
            Logger.d(LOG_TAG, dateName + log);
        }
    }

    //работает только с числом десятичных знаков 0-5
    public static double roundResult(double value, int decimalSigns) {
        if (decimalSigns < 0 || decimalSigns > 5) {
            Logger.d(LOG_TAG, "decimalSigns meant to be bw 0-5. Request is: " + String.valueOf(decimalSigns));
            if (decimalSigns < 0) decimalSigns = 0;
            if (decimalSigns > 5) decimalSigns = 5;
        }
        double multiplier = Math.pow(10.0, (double) decimalSigns);//всегда .0
        long numerator  = Math.round(value * multiplier);
        return numerator / multiplier;
    }

    public static int generateInt(int from, int to) {
        to -= from;
        return from + (int) (Math.random() * ++to);
    }

    public static String getStringDateFromCal(Calendar date, Context context){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTimeInMillis());
        Locale locale = context.getResources().getConfiguration().locale;
        return String.format(locale, "%02d.%02d.%02d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR) % 100);
    }

    public static String getStringTimeFromCal(Calendar date, Context context){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTimeInMillis());
        Locale locale = context.getResources().getConfiguration().locale;
        return String.format(locale, "%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    public static Date getCalendarStart(Context context) {
		Date startDate = new DataSource(context).getPhotoSessionsSource().getFirstPhotoSessionDate();
        Date now = new Date();
		if (startDate == null || startDate.after(now)) {
			Calendar yearStart = Calendar.getInstance();
			yearStart.set(Calendar.DAY_OF_MONTH, 1);
			yearStart.set(Calendar.MONTH, 0);
            startDate = yearStart.getTime();
		}
        return startDate;
    }
}
