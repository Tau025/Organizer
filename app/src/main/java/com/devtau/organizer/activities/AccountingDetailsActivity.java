package com.devtau.organizer.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.devtau.organizer.R;
import com.devtau.organizer.database.DataSource;
import com.devtau.organizer.database.sources.TransactionsSource;
import com.devtau.organizer.fragments.ConfirmDeleteDF;
import com.devtau.organizer.fragments.EditTransactionDF;
import com.devtau.organizer.model.PhotoSession;
import com.devtau.organizer.model.Transaction;
import com.devtau.organizer.model.TransactionComparators;
import com.devtau.organizer.util.Constants;
import com.devtau.organizer.util.Util;
import com.devtau.recyclerviewlib.MyItemRVAdapter;
import com.devtau.recyclerviewlib.RVHelper;
import com.devtau.recyclerviewlib.RVHelperInterface;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AccountingDetailsActivity extends AppCompatActivity implements
        RVHelperInterface,
        ConfirmDeleteDF.ConfirmDeleteDFListener<Transaction>,
        EditTransactionDF.onEditTransactionDFListener {
    private PhotoSession photoSession;
    private RVHelper<Transaction> rvHelper;
    private TransactionsSource transactionsSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounting_details);

        if(savedInstanceState == null) {
            photoSession = getIntent().getParcelableExtra(PhotoSessionDetailsActivity.PHOTO_SESSION_EXTRA);
        } else {
            photoSession = savedInstanceState.getParcelable(PhotoSessionDetailsActivity.PHOTO_SESSION_EXTRA);
        }

        transactionsSource = new DataSource(this).getTransactionsSource();

        initFAB();
        initRecycler(photoSession);
    }

    private void initFAB() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditTransactionDF(new Transaction(photoSession.getPhotoSessionID()));
            }
        });
    }

    private void initRecycler(PhotoSession currentPhotoSession) {
        if(photoSession == null) return;
        //запросим из бд список, который нам нужно показать
        ArrayList<Transaction> itemsList = transactionsSource
                .getTransactionsListForAPhotoSession(currentPhotoSession);

        //соберем из подготовленных вводных данных хелпер
        rvHelper = RVHelper.Builder.<Transaction> start(this, R.id.rv_helper_placeholder)
                .setList(itemsList)
                .withDividers(true)
                .build();
        rvHelper.addItemFragmentToLayout(this, R.id.rv_helper_placeholder);
    }

    @Override
    public void onBindViewHolder(MyItemRVAdapter.ViewHolder holder, final int rvHelperId) {
        //здесь выбираем, какие поля хранимого объекта отобразятся в каких частях строки
        //TextView в разметке по умолчанию такие: tvMain, tvAdditional1, tvAdditional2
        final Transaction transaction = (Transaction) holder.getItem();

        String mainText = String.format(Locale.getDefault(), getResources().getString(R.string.receivedFormatter),
                Util.getStringDateFromCal(transaction.getDate()),transaction.getAmount());
        ((TextView) holder.getView().findViewById(R.id.tvMain)).setText(mainText);

        ((TextView) holder.getView().findViewById(R.id.tvAdditional1)).setText(transaction.getComment());

        ImageButton btnDelete = ((ImageButton) holder.getView().findViewById(R.id.btnDelete));

        //здесь устанавливаем слушатели
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListItemClick(transaction, 0, rvHelperId);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListItemClick(transaction, 1, rvHelperId);
            }
        });
    }

    private void onListItemClick(Transaction transaction, int clickedActionId, int rvHelperId) {
        switch (clickedActionId) {
            case 0://клик по строке. откроем запись на редактирование
                openEditTransactionDF(transaction);
                break;
            case 1://запрос на удаление
                if(rvHelper != null) {
                    ConfirmDeleteDF dialog = new ConfirmDeleteDF();
                    Bundle args2 = new Bundle();
                    args2.putParcelable(Constants.OBJECT_ID_EXTRA, transaction);
                    dialog.setArguments(args2);
                    dialog.show(getSupportFragmentManager(), ConfirmDeleteDF.FRAGMENT_TAG);
                }
                break;
        }
    }

    //этот диалог используется и для создания новой транзакции и для редактирования уже существующей
    private void openEditTransactionDF(Transaction transaction) {
        EditTransactionDF editTransactionDF = new EditTransactionDF();
        Bundle args = new Bundle();
        args.putParcelable(Constants.OBJECT_ID_EXTRA, transaction);
        editTransactionDF.setArguments(args);
        editTransactionDF.show(getSupportFragmentManager(), EditTransactionDF.FRAGMENT_TAG);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PhotoSessionDetailsActivity.PHOTO_SESSION_EXTRA, photoSession);
    }

    @Override
    public void onAddNewItemDialogResult(List<String> newItemParams, int rvHelperId) { /*не используется*/ }

    @Override
    public Comparator provideComparator(int indexOfSortMethod) {
        return TransactionComparators.provideComparator(indexOfSortMethod);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //TODO: сохранить и вернуть photoSession
    }

    @Override
    public void deleteConfirmed(Transaction item) {
        transactionsSource.remove(item);
        rvHelper.removeItemFromList(item);
        Util.notifyBroadcastListeners(this);
        Toast.makeText(this, R.string.transactionDeletedMSG, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditTransactionDialogResult(Transaction transaction) {
        if(transaction.getTransactionID() != -1) {
            transactionsSource.update(transaction);
        } else {
            transactionsSource.create(transaction);
        }
        ArrayList<Transaction> itemsList = transactionsSource
                .getTransactionsListForAPhotoSession(photoSession);
        rvHelper.setList(itemsList);
    }
}
