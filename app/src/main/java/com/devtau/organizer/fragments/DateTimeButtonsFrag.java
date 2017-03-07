package com.devtau.organizer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.devtau.organizer.R;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import java.util.Calendar;
import java.util.Locale;

public class DateTimeButtonsFrag extends Fragment implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener{
    public static final String FRAGMENT_START_TAG = "StartDateTimeButtons";
    public static final String FRAGMENT_END_TAG = "EndDateTimeButtons";
    public static final String FRAGMENT_TRANSACTION_TIME_TAG = "TransactionTimeDateTimeButtons";
    public static final String DATE_TIME_EXTRA = "dateTimeExtra";
    public static final String FRAGMENT_ID_EXTRA = "fragmentIDExtra";
    private DateTimeButtonsInterface mListener;
    private Calendar dateTimeLocal;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private Button btnDate, btnTime;
    private boolean singleTapTimePick;
    private int fragmentID;

    public DateTimeButtonsFrag() { }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //проверим, реализован ли нужный интерфейс родительским фрагментом или активностью
        try {
            mListener = (DateTimeButtonsInterface) getParentFragment();
            if (mListener == null) mListener = (DateTimeButtonsInterface) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DateTimeButtonsFrag interface");
        }

        dateSetListener = this;
        timeSetListener = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        singleTapTimePick = false;

        //прочитаем дату-время из интента или бандла при пересоздании
        dateTimeLocal = Calendar.getInstance();
        if (savedInstanceState != null) {
            dateTimeLocal.setTimeInMillis(savedInstanceState.getLong(DATE_TIME_EXTRA));
            fragmentID = savedInstanceState.getInt(FRAGMENT_ID_EXTRA);
        } else {
            dateTimeLocal.setTimeInMillis(getArguments().getLong(DATE_TIME_EXTRA));
            fragmentID = getArguments().getInt(FRAGMENT_ID_EXTRA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_date_time_buttons, container, false);

        btnDate = (Button) rootView.findViewById(R.id.btnDate);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePD = DatePickerDialog.newInstance(dateSetListener,
                        dateTimeLocal.get(Calendar.YEAR), dateTimeLocal.get(Calendar.MONTH), dateTimeLocal.get(Calendar.DAY_OF_MONTH), false);
                datePD.setVibrate(false);
                datePD.setYearRange(2016, 2020);
                datePD.setCloseOnSingleTapDay(true);
                datePD.show(getChildFragmentManager(), "datepicker");
            }
        });

        btnTime = (Button) rootView.findViewById(R.id.btnTime);
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePD = TimePickerDialog.newInstance(timeSetListener,
                        dateTimeLocal.get(Calendar.HOUR_OF_DAY), dateTimeLocal.get(Calendar.MINUTE), true, false);
                timePD.setVibrate(false);
                timePD.setCloseOnSingleTapMinute(singleTapTimePick);
                timePD.show(getChildFragmentManager(), "timepicker");
            }
        });
        setDateTime(dateTimeLocal);
        return rootView;
    }

    //вызывается после завершения ввода в диалоге даты
    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        dateTimeLocal.set(Calendar.YEAR, year);
        dateTimeLocal.set(Calendar.MONTH, month);
        dateTimeLocal.set(Calendar.DAY_OF_MONTH, day);
        btnDate.setText(getStringDateFromCal(dateTimeLocal, getContext()));
        mListener.onDateOrTimeSet(dateTimeLocal, fragmentID);
    }

    //вызывается после завершения ввода в диалоге времени
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        dateTimeLocal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateTimeLocal.set(Calendar.MINUTE, minute);
        dateTimeLocal.set(Calendar.SECOND, 0);
        btnTime.setText(getStringTimeFromCal(dateTimeLocal, getContext()));
        mListener.onDateOrTimeSet(dateTimeLocal, fragmentID);
    }

    public void setDateTime(Calendar cal){
        dateTimeLocal.setTimeInMillis(cal.getTimeInMillis());
        if(btnDate != null && btnTime != null) {
            btnDate.setText(getStringDateFromCal(dateTimeLocal, getContext()));
            btnTime.setText(getStringTimeFromCal(dateTimeLocal, getContext()));
        }
    }


    //эти два метода можно поместить в Util класс
    public static String getStringDateFromCal(Calendar date, Context context){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTimeInMillis());
        return String.format(Locale.getDefault(), "%02d.%02d.%02d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR) % 100);
    }

    public static String getStringTimeFromCal(Calendar date, Context context){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTimeInMillis());
        return String.format(Locale.getDefault(), "%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DATE_TIME_EXTRA, dateTimeLocal.getTimeInMillis());
        outState.putInt(FRAGMENT_ID_EXTRA, fragmentID);
    }

    public interface DateTimeButtonsInterface {
        void onDateOrTimeSet(Calendar cal, int fragmentID);
    }
}
