package com.devtau.recyclerviewlib;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.devtau.recyclerviewlib.util.Logger;
import java.util.List;
/**
 * Фрагмент для опционально добавляемых контролов сортировки и вставки новой записи в список
 */
public class AddButtonFragment extends Fragment implements
        AddNewItemDF.onAddNewItemDFListener {
    private AddButtonFragmentListener listener;

    //Обязательный пустой конструктор
    public AddButtonFragment() { }

    @Override
    public void onAttach(Context context) {
        //если фрагмент является вложенным, context - это активность, держащая фрагмент-родитель, а не сам родитель
        Logger.d("AddButtonFragment.onAttach()");
        super.onAttach(context);
        try {
            //проверим, реализован ли нужный интерфейс родительским фрагментом или активностью
            listener = (AddButtonFragmentListener) getParentFragment();
            if (listener == null) {
                listener = (AddButtonFragmentListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddButtonFragmentListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("AddButtonFragment.onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_add_button, container, false);
        initControls(rootView);
        return rootView;
    }

    private void initControls(View rootView) {
        Button btnAdd = (Button) rootView.findViewById(R.id.btnAdd);
        if(btnAdd != null) {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AddNewItemDF().show(getChildFragmentManager(), AddNewItemDF.FRAGMENT_TAG);
                }
            });
        }
    }

    @Override
    public void onDetach() {
        Logger.d("AddButtonFragment.onDetach()");
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAddNewItemDialogResult(List<String> newItemParams) {
        listener.onAddNewItemDialogResult(newItemParams);
    }


    //интерфейс для общения AddButtonFragment со своим родителем
    public interface AddButtonFragmentListener {
        void onAddNewItemDialogResult(List<String> newItemParams);
    }
}
