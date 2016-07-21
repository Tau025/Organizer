package com.devtau.organizer.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.devtau.organizer.R;
import com.devtau.organizer.model.Transaction;
import com.devtau.organizer.util.Constants;
import com.devtau.organizer.util.Logger;
import com.devtau.organizer.util.Util;
import java.util.Calendar;

public class EditTransactionDF extends DialogFragment implements
        DateTimeButtonsFrag.DateTimeButtonsInterface,
        View.OnClickListener{
    public static final String FRAGMENT_TAG = EditTransactionDF.class.getSimpleName();
    private static final String LOG_TAG = EditTransactionDF.class.getSimpleName();
    private EditText etAmount;
    private EditText etComment;
    private onEditTransactionDFListener listener;
    private Transaction transaction;

    @Override
    public void onAttach(Context context) {
        //если фрагмент является вложенным, context - это активность, держащая фрагмент-родитель, а не сам родитель
        Logger.d(LOG_TAG, "onAttach()");
        super.onAttach(context);
        try {
            //проверим, реализован ли нужный интерфейс родительским фрагментом или активностью
            listener = (onEditTransactionDFListener) getParentFragment();
            if (listener == null) {
                listener = (onEditTransactionDFListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onEditTransactionDFListener");
        }
        transaction = getArguments().getParcelable(Constants.OBJECT_ID_EXTRA);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Logger.d(LOG_TAG, "onCreateDialog()");

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;

//        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_transaction, null);
//        etAmount = (EditText) rootView.findViewById(R.id.etAmount);
//        etComment = (EditText) rootView.findViewById(R.id.etComment);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder
//                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if(etAmount != null && etComment != null) {
//                            //подготовим компоненты
//                            List<String> newItemParams = new ArrayList<>();
//                            newItemParams.add(etAmount.getText().toString());
//                            newItemParams.add(etComment.getText().toString());
//
//                            //передадим собранный массив строк на обработку слушателю
//                            listener.onEditTransactionDialogResult(transaction, newItemParams);
//                        }
//                    }
//                })
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        /*NOP*/
//                    }
//                });
//        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d(LOG_TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.dialog_edit_transaction, container, false);

        insertStartDateTimeButtonsFrag(transaction.getDate());
        initControls(rootView);
        return rootView;
    }

    private void insertStartDateTimeButtonsFrag(Calendar initDate) {
        if(transaction == null) return;

        FragmentManager fragmentManager = getChildFragmentManager();

        DateTimeButtonsFrag dateTimeButtonsFrag = (DateTimeButtonsFrag) fragmentManager.findFragmentByTag(
                DateTimeButtonsFrag.FRAGMENT_TRANSACTION_TIME_TAG);
        if (dateTimeButtonsFrag == null) {
            dateTimeButtonsFrag = new DateTimeButtonsFrag();
            Bundle args = new Bundle();
            args.putLong(DateTimeButtonsFrag.DATE_TIME_EXTRA, initDate.getTimeInMillis());
            dateTimeButtonsFrag.setArguments(args);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.dateTimePlaceHolder, dateTimeButtonsFrag, DateTimeButtonsFrag.FRAGMENT_TRANSACTION_TIME_TAG);
            ft.commit();
        } else {
            dateTimeButtonsFrag.setDateTime(initDate);
        }
    }

    private void initControls(View rootView) {
        if(transaction == null) return;

        etAmount = (EditText) rootView.findViewById(R.id.etAmount);
        etComment = (EditText) rootView.findViewById(R.id.etComment);
        Button btnSave = (Button) rootView.findViewById(R.id.btnSave);
        Button btnCancel = (Button) rootView.findViewById(R.id.btnCancel);

        if(etAmount != null && etComment != null && btnSave != null && btnCancel != null) {
            etAmount.setText(String.valueOf(transaction.getAmount()));
            etComment.setText(transaction.getComment());
            btnSave.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                //дата транзакции обновляется в onDateOrTimeSet()
                transaction.setAmount(Integer.valueOf(etAmount.getText().toString()));
                transaction.setComment(etComment.getText().toString());
                listener.onEditTransactionDialogResult(transaction);
                dismiss();
                break;

            case R.id.btnCancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onDateOrTimeSet(Calendar newDate, int fragmentID) {
        transaction.setDate(newDate);
        Logger.d(LOG_TAG, "transactionDate: " + Util.dateFormat.format(transaction.getDate().getTime()));
    }

    public interface onEditTransactionDFListener {
        void onEditTransactionDialogResult(Transaction transaction);
    }
}