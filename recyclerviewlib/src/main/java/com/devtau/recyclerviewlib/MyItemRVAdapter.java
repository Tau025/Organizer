package com.devtau.recyclerviewlib;

import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.devtau.recyclerviewlib.util.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * Адаптер списка и вьюхолдер строки списка
 */
public class MyItemRVAdapter<T extends Parcelable> extends RecyclerView.Adapter<MyItemRVAdapter.ViewHolder> {
    private ArrayList<T> itemsList;
    private final int listItemLayoutId;
    private final RVFragment.OnRVFragmentListener listener;

    public MyItemRVAdapter(ArrayList<T> itemsList, int listItemLayoutId, Comparator comparator,
                           RVFragment.OnRVFragmentListener listener) {
        Logger.d("MyItemRVAdapter constructor");
        this.itemsList = itemsList;
        this.listItemLayoutId = listItemLayoutId;
        sort(comparator);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        Logger.d("MyItemRVAdapter.onBindViewHolder()");
        holder.item = itemsList.get(position);
        listener.onBindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    //МЕТОДЫ ВЗАИМОДЕЙСТВИЯ RVFragment С MyItemRVAdapter-----------------------------------------------

    //вставляет новую строку в лист
    public int addItemToList(T item, Comparator comparator) {
        itemsList.add(item);
        sort(comparator);
        int position = itemsList.indexOf(item);
        notifyItemInserted(position);
        return position;
    }

    //удаляет строку из листа
    //для корректного удаления элемента из списка реализуйте equals и hashCode у класса хранимого объекта
    public void removeItemFromList(T item){
        int positionInList = itemsList.indexOf(item);
        itemsList.remove(item);
        if(positionInList != -1) {
            notifyItemRemoved(positionInList);
        }
    }

    //переназначает лист адаптера
    public void setList(ArrayList<T> itemsList){
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    //сортирует лист
    public void sortAndNotify(Comparator comparator) {
        sort(comparator);
        notifyDataSetChanged();
    }
    private void sort(Comparator comparator) {
        //сортировка будет применяться только если клиент передаст компаратор в колбэке provideComparator()
        if(comparator != null) {
            Collections.sort(itemsList, comparator);
        } else {
            Collections.sort(itemsList, new Comparator<T>() {
                @Override
                public int compare(T t, T t1) {
                    return 0;
                }
            });
        }
    }


    //вьюхолдер-класс, объединяющий данные строки списка с его пользовательским представлением
    public class ViewHolder<T extends Parcelable> extends RecyclerView.ViewHolder {
        private final View view;
        private T item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }

        public View getView() {
            return view;
        }

        public T getItem() {
            return item;
        }
    }
}
