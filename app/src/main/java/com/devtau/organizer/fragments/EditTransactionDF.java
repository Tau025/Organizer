package com.devtau.organizer.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.devtau.organizer.R;
import com.devtau.organizer.util.Logger;
import java.util.ArrayList;
import java.util.List;

public class EditTransactionDF extends DialogFragment {
    public static final String FRAGMENT_TAG = "EditTransactionDF";
    private EditText etAmount;
    private EditText etComment;
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
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_transaction, null);
        etAmount = (EditText) rootView.findViewById(R.id.etAmount);
        etComment = (EditText) rootView.findViewById(R.id.etComment);

        builder.setView(rootView)
                .setPositiveButton(R.string.add, (dialogInterface, i) -> {
                    if(etAmount != null && etComment != null) {
                        //подготовим компоненты
                        List<String> newItemParams = new ArrayList<>();
                        newItemParams.add(etAmount.getText().toString());
                        newItemParams.add(etComment.getText().toString());

                        //передадим собранный массив строк на обработку слушателю
                        listener.onEditTransactionDialogResult(newItemParams);
                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> { /*NOP*/ });
        return builder.create();
    }

    public interface onAddNewItemDFListener {
        void onEditTransactionDialogResult(List<String> newItemParams);
    }
}