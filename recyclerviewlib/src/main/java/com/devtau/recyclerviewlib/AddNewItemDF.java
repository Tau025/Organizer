package com.devtau.recyclerviewlib;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import com.devtau.recyclerviewlib.util.Logger;
import java.util.ArrayList;
import java.util.List;

public class AddNewItemDF extends DialogFragment {
    public static final String FRAGMENT_TAG = "AddNewItemDF";
    private EditText etPrice;
    private EditText etDescription;
    private onAddNewItemDFListener listener;

    @Override
    public void onAttach(Context context) {
        //если фрагмент является вложенным, context - это активность, держащая фрагмент-родитель, а не сам родитель
        Logger.d("ItemFragment.onAttach()");
        super.onAttach(context);
        try {
            //проверим, реализован ли нужный интерфейс родительским фрагментом или активностью
            listener = (onAddNewItemDFListener) getParentFragment();
            if (listener == null) {
                listener = (onAddNewItemDFListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onAddNewItemDFListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_custom_view, null);
        etPrice = (EditText) rootView.findViewById(R.id.etPrice);
        etDescription = (EditText) rootView.findViewById(R.id.etDescription);

        builder.setView(rootView)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(etPrice != null && etDescription != null) {
                            // TODO: настройте создание нового хранимого объекта
                            //подготовим компоненты
                            List<String> newItemParams = new ArrayList<>();
                            newItemParams.add(etPrice.getText().toString());
                            newItemParams.add(etDescription.getText().toString());

                            //передадим собранный массив строк на обработку слушателю
                            listener.onAddNewItemDialogResult(newItemParams);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*NOP*/
                    }
                });
        return builder.create();
    }

    public interface onAddNewItemDFListener {
        void onAddNewItemDialogResult(List<String> newItemParams);
    }
}