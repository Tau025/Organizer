package com.devtau.organizer.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.devtau.organizer.R;
import com.devtau.organizer.model.Client;
import com.devtau.organizer.util.Constants;

import java.util.ArrayList;

public class SingleChoiceListDF extends DialogFragment {
    public static final String FRAGMENT_TAG = "SingleChoiceListDF";
    public static final String ARG_ITEMS_LIST = "itemsList";
    private SingleChoiceListDFInterface listener;
    private long selectedObjectID;
    private int positionInRVList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            //проверим, реализован ли нужный интерфейс родительским фрагментом или активностью
            listener = (SingleChoiceListDFInterface) getParentFragment();
            if (listener == null) {
                listener = (SingleChoiceListDFInterface) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SingleChoiceListDFInterface");
        }

        selectedObjectID = getArguments().getLong(Constants.OBJECT_ID_EXTRA, -1);
        positionInRVList = getArguments().getInt(Constants.POSITION_EXTRA, -1);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayList<Client> list = getArguments().getParcelableArrayList(ARG_ITEMS_LIST);
        String[] clientNamesList = new String[list.size()];
        for (int i = 0; i < clientNamesList.length; i++) {
            clientNamesList[i] = list.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_client)
                .setItems(clientNamesList, (dialog, which) -> {
                    listener.processListItem(list.get(which));
                });
        return builder.create();
    }

    public interface SingleChoiceListDFInterface{
        void processListItem(Client client);
    }
}