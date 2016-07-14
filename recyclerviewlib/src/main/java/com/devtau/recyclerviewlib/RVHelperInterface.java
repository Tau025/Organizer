package com.devtau.recyclerviewlib;

import java.util.Comparator;
import java.util.List;
/**
 * Интерфейс общения хелпера с клиентом
 */
public interface RVHelperInterface {
    void onBindViewHolder(MyItemRVAdapter.ViewHolder holder, int rvHelperId);
    void onAddNewItemDialogResult(List<String> newItemParams, int rvHelperId);
    Comparator provideComparator(int indexOfSortMethod);
}
