package com.devtau.recyclerviewlib;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.devtau.recyclerviewlib.util.Constants;
import com.devtau.recyclerviewlib.util.Logger;
import com.devtau.recyclerviewlib.util.Util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 * Фрагмент обобщающий сортировку, вставку новой записи с отображением самого списка
 */
public class ItemFragment<T extends Parcelable> extends Fragment implements
        SpinnerFragment.SpinnerFragmentListener,
        AddButtonFragment.AddButtonFragmentListener,
        RVFragment.OnRVFragmentListener {
    public static final String ARG_RV_HELPER_ID = "rvHelperId";
    public static final String ARG_ITEMS_LIST = "itemsList";
    public static final String ARG_COLUMN_COUNT = "columnCount";
    public static final String ARG_LIST_ITEM_LAYOUT_ID = "listItemLayoutId";
    public static final String ARG_INCLUDE_ADD_BUTTON_IN_LAYOUT = "includeAddButtonInLayout";
    public static final String ARG_INCLUDE_DIVIDERS = "includeDividers";
    public static final String ARG_INCLUDE_SPINNER_IN_LAYOUT = "includeSpinnerInLayout";
    public static final String ARG_COMPARATORS_NAMES = "comparatorsNames";
    public static final String ARG_INDEX_OF_SORT_METHOD = "indexOfSortMethod";

    private int rvHelperId;
    private RVHelperInterface listener;
    boolean includeSpinnerInLayout = Constants.DEFAULT_ADD_SPINNER;
    private int indexOfSortMethod = Constants.DEFAULT_SORT_BY;

    private SpinnerFragment spinnerFragment;
    private RVFragment<T> rvFragment;

    //Обязательный пустой конструктор
    public ItemFragment() { }
    //мы не можем использовать статический метод newInstance() для создания фрагмента с дженериками

    @Override
    public void onAttach(Context context) {
        //если фрагмент является вложенным, context - это активность, держащая фрагмент-родитель, а не сам родитель
        Logger.d("ItemFragment.onAttach()");
        super.onAttach(context);
        try {
            //проверим, реализован ли нужный интерфейс родительским фрагментом или активностью
            listener = (RVHelperInterface) getParentFragment();
            if (listener == null) {
                listener = (RVHelperInterface) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RVHelperInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("ItemFragment.onCreateView()");
        View view = inflater.inflate(R.layout.fragment_rv_helper, container, false);

        //рекомендовано читать аргументы из бандла не здесь, а в onCreate, но тогда нам нужно создавать
        //все переменные на уровне фрагмента, а не локальные для метода, которые мы сразу отдаем в конструкторы
        ArrayList<T> itemsList = new ArrayList<>();
        int columnCount = Constants.DEFAULT_COLUMN_COUNT;
        int listItemLayoutId = Constants.DEFAULT_LIST_ITEM_LAYOUT;
        boolean includeAddButtonInLayout = Constants.DEFAULT_INCLUDE_ADD_BUTTON;
        boolean includeDividers = Constants.DEFAULT_INCLUDE_DIVIDERS;
        ArrayList<String> comparatorsNames = Util.getDefaultComparatorsNames(getContext());

        if (getArguments() != null) {
            rvHelperId = getArguments().getInt(ARG_RV_HELPER_ID);
            itemsList = getArguments().getParcelableArrayList(ARG_ITEMS_LIST);
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            listItemLayoutId = getArguments().getInt(ARG_LIST_ITEM_LAYOUT_ID);
            includeAddButtonInLayout = getArguments().getBoolean(ARG_INCLUDE_ADD_BUTTON_IN_LAYOUT);
            includeDividers = getArguments().getBoolean(ARG_INCLUDE_DIVIDERS);
            if(includeSpinnerInLayout = getArguments().getBoolean(ARG_INCLUDE_SPINNER_IN_LAYOUT)) {
                comparatorsNames = getArguments().getStringArrayList(ARG_COMPARATORS_NAMES);
                indexOfSortMethod = getArguments().getInt(ARG_INDEX_OF_SORT_METHOD);
            }
        }

        if(includeSpinnerInLayout) {
            spinnerFragment = createSpinnerFragment(indexOfSortMethod, comparatorsNames);
            addFragmentToLayout(R.id.spinner_fragment_placeholder, spinnerFragment);
        }
        if(includeAddButtonInLayout) {
            addFragmentToLayout(R.id.add_button_fragment_placeholder, new AddButtonFragment());
        }

        rvFragment = createRVFragment(itemsList, columnCount, listItemLayoutId, indexOfSortMethod, includeDividers);
        addFragmentToLayout(R.id.recycler_view_placeholder, rvFragment);

        return view;
    }

    public SpinnerFragment createSpinnerFragment(int indexOfSortMethod, ArrayList<String> comparatorsNames) {
        SpinnerFragment fragment = new SpinnerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX_OF_SORT_METHOD, indexOfSortMethod);
        args.putStringArrayList(ARG_COMPARATORS_NAMES, comparatorsNames);
        fragment.setArguments(args);
        return fragment;
    }

    public RVFragment<T> createRVFragment(ArrayList<T> itemsList, int columnCount,
                                       int listItemLayoutId, int indexOfSortMethod, boolean includeDividers) {
        RVFragment<T> fragment = new RVFragment<>();
        Bundle args = new Bundle();

        args.putParcelableArrayList(ARG_ITEMS_LIST, itemsList);
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_LIST_ITEM_LAYOUT_ID, listItemLayoutId);
        args.putInt(ARG_INDEX_OF_SORT_METHOD, indexOfSortMethod);
        args.putBoolean(ARG_INCLUDE_DIVIDERS, includeDividers);

        fragment.setArguments(args);
        return fragment;
    }

    private void addFragmentToLayout(int placeholderID, Fragment fragment){
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(placeholderID, fragment);
        ft.commit();
    }

    @Override
    public void onDetach() {
        Logger.d("ItemFragment.onDetach()");
        super.onDetach();
        listener = null;
    }


    //МЕТОДЫ ВЗАИМОДЕЙСТВИЯ RVHelper С ItemFragment-------------------------------------------------

    //метод необходим для saveInstanceState
    public int getIndexOfSortMethod() {
        return indexOfSortMethod;
    }


    //вставляет новую строку в лист
    public void addItemToList(T item) {
        rvFragment.addItemToList(item, listener.provideComparator(indexOfSortMethod));
    }

    //удаляет строку из листа
    public void removeItemFromList(T item) {
        rvFragment.removeItemFromList(item);
    }

    //переназначает лист адаптера
    public void setList(ArrayList<T> itemsList){
        rvFragment.setList(itemsList);
    }

    //сортирует лист
    //этот же метод используется и для обработки запросов на сортировку извне RVHelper
    @Override
    public void onSpinnerItemSelected(int indexOfSortMethod) {
        this.indexOfSortMethod = indexOfSortMethod;
        rvFragment.sort(listener.provideComparator(indexOfSortMethod));
    }


    //МЕТОДЫ ВЗАИМОДЕЙСТВИЯ ДОЧЕРНИХ ФРАГМЕНТОВ С ItemFragment--------------------------------------

    //выполняется после завершения ввода данных для нового хранимого объекта
    @Override
    public void onAddNewItemDialogResult(List<String> newItemParams) {
        listener.onAddNewItemDialogResult(newItemParams, rvHelperId);
    }

    //проброс вызова onBindViewHolder от MyItemRVAdapter через RVFragment в ItemFragment и далее клиенту
    @Override
    public void onBindViewHolder(MyItemRVAdapter.ViewHolder holder) {
        listener.onBindViewHolder(holder, rvHelperId);
    }

    //возвращает Comparator по его индексу. метод необходим, т.к. Comparator не упаковать в Bundle
    @Override
    public Comparator provideComparator(int indexOfSortMethod) {
        return listener.provideComparator(indexOfSortMethod);
    }
}
