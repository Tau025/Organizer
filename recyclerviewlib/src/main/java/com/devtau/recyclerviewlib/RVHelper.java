package com.devtau.recyclerviewlib;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import com.devtau.recyclerviewlib.util.Constants;
import com.devtau.recyclerviewlib.util.Logger;
import com.devtau.recyclerviewlib.util.Util;
/**
 * Сохраните папку проекта локально. Добавьте в ваш проект модуль библиотеки через диалог File - New - Import module...
 * Выберите модуль библиотеки recyclerviewlib из скачанного проекта.
 *
 * Клиент создает экземпляр хелпера посредством его Builder и реализует интерфейс RVHelperInterface.
 * Минимальный список параметров - это лист объектов.
 *
 * Класс хранимого объекта должен:
 * 1 - переопределить методы equals() и hashCode() - для корректного удаления и вставки;
 * 2 - реализовать Parcelable.
 *
 * При необходимости хелпер добавит в разметку спиннер для сортировки и кнопку для добавления новых записей.
 * Для спиннера создайте в strings список названий для каждого варианта.
 * Компараторы удобно реализовывать отдельным классом. Пример реализации есть в классе DummyItemComparators.
 *
 *
 * Если в списке должно быть больше одной колонки, нужное количество можно передать в методе withColumnCount().
 * Клиент может создать произвольную разметку и передать ее файл билдеру в методе withListItemLayoutId().
 *
 * В модуле app можно посмотреть пример организации клиентского модуля.
 * Например, чтобы применить возможности JDK 1.8 обратите внимание на файл app/build.gradle.
 * Также библиотека поддерживает работу сразу с несколькими списками.
 */
public class RVHelper<T extends Parcelable> {
    private ItemFragment<T> itemFragment;

    private RVHelper(Builder<T> builder) {
        itemFragment = new ItemFragment<>();
        Bundle args = new Bundle();

        Logger.d("builder.rvHelperId: " + String.valueOf(builder.rvHelperId));
        args.putInt(ItemFragment.ARG_RV_HELPER_ID, builder.rvHelperId);
        args.putParcelableArrayList(ItemFragment.ARG_ITEMS_LIST, builder.itemsList);
        args.putInt(ItemFragment.ARG_COLUMN_COUNT, builder.columnCount);
        args.putInt(ItemFragment.ARG_LIST_ITEM_LAYOUT_ID, builder.listItemLayoutId);
        args.putBoolean(ItemFragment.ARG_INCLUDE_ADD_BUTTON_IN_LAYOUT, builder.includeAddButtonInLayout);
        args.putBoolean(ItemFragment.ARG_INCLUDE_DIVIDERS, builder.includeDividers);
        args.putBoolean(ItemFragment.ARG_INCLUDE_SPINNER_IN_LAYOUT, builder.includeSpinnerInLayout);
        if(builder.includeSpinnerInLayout) {
            args.putStringArrayList(ItemFragment.ARG_COMPARATORS_NAMES, builder.comparatorsNames);
            args.putInt(ItemFragment.ARG_INDEX_OF_SORT_METHOD, builder.indexOfSortMethod);
        }

        itemFragment.setArguments(args);
    }

    public void addItemFragmentToLayout(AppCompatActivity activity, int placeholderId){
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(placeholderId, itemFragment);
        ft.commit();
    }


    //метод необходим для saveInstanceState
    public int getIndexOfSortMethod() {
        return itemFragment.getIndexOfSortMethod();
    }


    //вставляет новую строку в лист
    //метод публичный, т.к. при работе с бд _id хранимого объекта создается только после
    //вставки записи в бд, а к ней у списка доступа нет
    public void addItemToList(T item) {
        itemFragment.addItemToList(item);
    }


    //удаляет строку из листа
    //физическое удаление из бд - ответственность клиента, не входящая в функционал RVHelper
    public void removeItemFromList(T item) {
        itemFragment.removeItemFromList(item);
    }

    //переназначает лист адаптера
    //клиент сам проверяет, что к новому передаваемому листу могут быть применены старые компараторы
    public void setList(ArrayList<T> itemsList){
        itemFragment.setList(itemsList);
    }

    //сортирует лист
    //обычно эта команда генерируется внутри RVHelper выбором одного из вариантов в спиннере
    public void sort(int indexOfSortMethod) {
        itemFragment.onSpinnerItemSelected(indexOfSortMethod);
    }



    public static class Builder<T extends Parcelable>{
        private int rvHelperId;
        private ArrayList<T> itemsList; //обязательный параметр. нет дефолта
        //все параметры ниже не обязательны
        private int columnCount = Constants.DEFAULT_COLUMN_COUNT;
        private int listItemLayoutId = Constants.DEFAULT_LIST_ITEM_LAYOUT;
        private boolean includeAddButtonInLayout = Constants.DEFAULT_INCLUDE_ADD_BUTTON;
        private boolean includeDividers = Constants.DEFAULT_INCLUDE_DIVIDERS;
        //4 параметра ниже нужны для использования сортировки
        private boolean includeSpinnerInLayout = Constants.DEFAULT_ADD_SPINNER;
        private ArrayList<String> comparatorsNames;//дефолт назначается в конструкторе
        private int indexOfSortMethod = Constants.DEFAULT_SORT_BY;

        private Builder(Context context) {
            comparatorsNames = Util.getDefaultComparatorsNames(context);
        }

        //при необходимости работать одновременно с несколькими списками используйте rvHelperId
        //для идентификации списка, от которого пришел колбэк
        public static <T extends Parcelable>Builder<T> start(Context context, int rvHelperId) {
            Builder<T> newBuilder = new Builder<>(context);
            newBuilder.rvHelperId = rvHelperId;
            return newBuilder;
        }

        public Builder<T> setList(ArrayList<T> itemsList) {
            this.itemsList = itemsList;
            return this;
        }

        public Builder<T> withColumnCount(int columnCount) {
            this.columnCount = columnCount;
            return this;
        }

        public Builder<T> withListItemLayoutId(int listItemLayoutId) {
            this.listItemLayoutId = listItemLayoutId;
            return this;
        }

        public Builder<T> withAddButton() {
            includeAddButtonInLayout = true;
            return this;
        }

        public Builder<T> withSortSpinner(ArrayList<String> comparatorsNames, int indexOfSortMethod) {
            includeSpinnerInLayout = true;
            this.comparatorsNames = comparatorsNames;
            this.indexOfSortMethod = indexOfSortMethod;
            return this;
        }

        public Builder<T> withDividers(boolean include) {
            includeDividers = include;
            return this;
        }

        public RVHelper<T> build() {
            return new RVHelper<>(this);
        }
    }
}
