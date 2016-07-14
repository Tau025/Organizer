package com.devtau.organizer.model;

import java.util.Comparator;
/**
 * Компараторы необходимы, если вы собираетесь сортировать лист объектов этого класса
 * ответ <0 говорит о том, что сравнение не прошло проверку и нужна перестановка
 */
public class TransactionComparators {
    public static Comparator<Transaction> FIRST_OLD = (first, second) -> {
        long firstLong = first.getDate().getTimeInMillis();
        long secondLong = second.getDate().getTimeInMillis();
        int result;
        if (firstLong < secondLong) result = -1;
        else if (firstLong == secondLong) result = 0;
        else result = 1;
        return result;
    };


    public static Comparator provideComparator(int indexOfSortMethod) {
        switch (indexOfSortMethod) {
            case 0: return FIRST_OLD;
            default: return FIRST_OLD;
        }
    }
}
