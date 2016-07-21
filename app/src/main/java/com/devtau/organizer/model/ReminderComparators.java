package com.devtau.organizer.model;

import android.content.Context;
import android.content.res.Resources;

import com.devtau.organizer.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
/**
 * Компараторы необходимы, если вы собираетесь сортировать лист объектов этого класса
 * ответ <0 говорит о том, что сравнение не прошло проверку и нужна перестановка
 */
public class ReminderComparators {
    public static Comparator<Reminder> FIRST_FRESH = new Comparator<Reminder>() {
        @Override
        public int compare(Reminder first, Reminder second) {
            long firstLong = first.getDate().getTimeInMillis();
            long secondLong = second.getDate().getTimeInMillis();
            int result;
            if (secondLong < firstLong) result = -1;
            else if (firstLong == secondLong) result = 0;
            else result = 1;
            return result;
        }
    };
    public static Comparator<Reminder> FIRST_OLD = new Comparator<Reminder>() {
        @Override
        public int compare(Reminder first, Reminder second) {
            long firstLong = first.getDate().getTimeInMillis();
            long secondLong = second.getDate().getTimeInMillis();
            int result;
            if (firstLong < secondLong) result = -1;
            else if (firstLong == secondLong) result = 0;
            else result = 1;
            return result;
        }
    };
    public static Comparator<Reminder> ALPHABETICAL = new Comparator<Reminder>() {
        @Override
        public int compare(Reminder first, Reminder second) {
            return first.getDescription().compareTo(second.getDescription());
        }
    };
    public static Comparator<Reminder> REV_ALPHABETICAL = new Comparator<Reminder>() {
        @Override
        public int compare(Reminder first, Reminder second) {
            return second.getDescription().compareTo(first.getDescription());
        }
    };

    //альтернатива без лямбды
//        public static Comparator<PhotoSession> FIRST_LOWER_PRICE = new Comparator<PhotoSession>() {
//            @Override
//            public int compare(PhotoSession first, PhotoSession second) {
//                return first.getPrice() - second.getPrice();
//            }
//        };

    public static HashMap<Integer, Comparator> getComparatorsMap() {
        HashMap<Integer, Comparator> comparators = new HashMap<>();
        comparators.put(0, FIRST_FRESH);
        comparators.put(1, FIRST_OLD);
        comparators.put(4, ALPHABETICAL);
        comparators.put(5, REV_ALPHABETICAL);
        return comparators;
    }

    public static ArrayList<String> getComparatorsNames(Context context) {
        Resources res = context.getResources();
        ArrayList<String> comparatorsNames = new ArrayList<>();
        comparatorsNames.add(res.getString(R.string.first_fresh));
        comparatorsNames.add(res.getString(R.string.first_old));
        comparatorsNames.add(res.getString(R.string.alphabetical));
        comparatorsNames.add(res.getString(R.string.rev_alphabetical));
        return comparatorsNames;
    }
}
