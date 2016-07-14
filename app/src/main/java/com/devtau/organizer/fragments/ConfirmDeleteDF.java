package com.devtau.organizer.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.devtau.organizer.R;
import com.devtau.organizer.util.Constants;

public class ConfirmDeleteDF<T extends Parcelable> extends DialogFragment {
    public static final String FRAGMENT_TAG = "ConfirmDeleteDF";
    private ConfirmDeleteDFListener listener;
    private T item;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            //проверим, реализован ли нужный интерфейс родительским фрагментом или активностью
            listener = (ConfirmDeleteDFListener) getParentFragment();
            if (listener == null) {
                listener = (ConfirmDeleteDFListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ConfirmDeleteDFListener");
        }
        item = getArguments().getParcelable(Constants.OBJECT_ID_EXTRA);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.deleteConfirm)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    listener.deleteConfirmed(item);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> {/*NOP*/});
        return builder.create();
    }

    public interface ConfirmDeleteDFListener<T> {
        void deleteConfirmed(T item);
    }
}