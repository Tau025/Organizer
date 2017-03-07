package com.devtau.recyclerviewlib;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.devtau.recyclerviewlib.util.Constants;
import com.devtau.recyclerviewlib.util.Logger;
import java.util.ArrayList;
import java.util.Comparator;
/**
 * Фрагмент для самого списка
 */
public class RVFragment<T extends Parcelable> extends Fragment {
    private OnRVFragmentListener listener;
    private MyItemRVAdapter<T> adapter;
    private RecyclerView recyclerView;

    //Обязательный пустой конструктор
    public RVFragment() { }
    //мы не можем использовать статический метод newInstance() для создания фрагмента с дженериками

    @Override
    public void onAttach(Context context) {
        //если фрагмент является вложенным, context - это активность, держащая фрагмент-родитель, а не сам родитель
        Logger.d("RVFragment.onAttach()");
        super.onAttach(context);
        try {
            //проверим, реализован ли нужный интерфейс родительским фрагментом или активностью
            listener = (OnRVFragmentListener) getParentFragment();
            if (listener == null) {
                listener = (OnRVFragmentListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnRVFragmentListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("RVFragment.onCreateView()");
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        //рекомендовано читать аргументы из бандла не здесь, а в onCreate, но тогда нам нужно создавать
        //все переменные на уровне фрагмента, а не локальные для метода, которые мы сразу отдаем в адаптер
        ArrayList<T> itemsList = new ArrayList<>();
        int columnCount = Constants.DEFAULT_COLUMN_COUNT;
        int listItemLayoutId = Constants.DEFAULT_LIST_ITEM_LAYOUT;
        int indexOfSortMethod = Constants.DEFAULT_SORT_BY;
        boolean includeDividers = Constants.DEFAULT_INCLUDE_DIVIDERS;

        if (getArguments() != null) {
            itemsList = getArguments().getParcelableArrayList(ItemFragment.ARG_ITEMS_LIST);
            columnCount = getArguments().getInt(ItemFragment.ARG_COLUMN_COUNT);
            listItemLayoutId = getArguments().getInt(ItemFragment.ARG_LIST_ITEM_LAYOUT_ID);
            indexOfSortMethod = getArguments().getInt(ItemFragment.ARG_INDEX_OF_SORT_METHOD);
            includeDividers = getArguments().getBoolean(ItemFragment.ARG_INCLUDE_DIVIDERS);
        }
        Comparator comparator = listener.provideComparator(indexOfSortMethod);

        // установим адаптер списка
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (columnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                if (includeDividers) {
                    recyclerView.addItemDecoration(new DividerItemDecoration(context, RecyclerView.VERTICAL));
                }
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
            }
            adapter = new MyItemRVAdapter<>(itemsList, listItemLayoutId, comparator, listener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onDetach() {
        Logger.d("RVFragment.onDetach()");
        super.onDetach();
        listener = null;
    }


    //МЕТОДЫ ВЗАИМОДЕЙСТВИЯ ItemFragment С RVFragment-----------------------------------------------

    //вставляет новую строку в лист
    public void addItemToList(T item, Comparator comparator) {
        int position = adapter.addItemToList(item, comparator);
        recyclerView.scrollToPosition(position);
    }

    //удаляет строку из листа
    public void removeItemFromList(T item) {
        adapter.removeItemFromList(item);
    }

    //переназначает лист адаптера
    public void setList(ArrayList<T> itemsList){
        adapter.setList(itemsList);
    }

    //сортирует лист
    public void sort(Comparator comparator) {
        adapter.sortAndNotify(comparator);
    }


    //интерфейс для общения RVFragment со своим родителем
    interface OnRVFragmentListener {
        void onBindViewHolder(MyItemRVAdapter.ViewHolder holder);
        Comparator provideComparator(int indexOfSortMethod);
    }
}
