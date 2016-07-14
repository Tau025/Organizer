package com.devtau.organizer.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.devtau.organizer.R;
import com.devtau.organizer.database.DataSource;
import com.devtau.organizer.fragments.ConfirmDeleteDF;
import com.devtau.organizer.fragments.EditTransactionDF;
import com.devtau.organizer.model.PhotoSession;
import com.devtau.organizer.model.Transaction;
import com.devtau.organizer.model.TransactionComparators;
import com.devtau.organizer.util.Constants;
import com.devtau.organizer.util.Logger;
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
        EditTransactionDF.onAddNewItemDFListener{
    private PhotoSession photoSession;
    private RVHelper rvHelper;
    private DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounting_details);

        if(savedInstanceState == null) {
            photoSession = getIntent().getParcelableExtra(PhotoSessionDetailsActivity.PHOTO_SESSION_EXTRA);
        } else {
            photoSession = savedInstanceState.getParcelable(PhotoSessionDetailsActivity.PHOTO_SESSION_EXTRA);
        }

        dataSource = new DataSource(this);

        initRecycler(photoSession);
    }

    private void initRecycler(PhotoSession currentPhotoSession) {
        if(photoSession == null) return;
        //запросим из бд список, который нам нужно показать
        ArrayList<Transaction> itemsList = dataSource.getTransactionsSource()
                .getTransactionsListForAPhotoSession(currentPhotoSession);

        //соберем из подготовленных вводных данных хелпер
        rvHelper = RVHelper.Builder.<Transaction> start(this, R.id.accountingDetailsRVPlaceholder)
                .setList(itemsList)
                .withAddButton()
                .build();
        rvHelper.addItemFragmentToLayout(this, R.id.accountingDetailsRVPlaceholder);
    }

    @Override
    public void onBindViewHolder(MyItemRVAdapter.ViewHolder holder, int rvHelperId) {
        //здесь выбираем, какие поля хранимого объекта отобразятся в каких частях CardView
        //TextView в разметке по умолчанию такие: tvMain, tvAdditional1, tvAdditional2
        final Transaction item = (Transaction) holder.getItem();

        Locale locale = getResources().getConfiguration().locale;
        String mainText = String.format(locale, getResources().getString(R.string.receivedFormatter),
                Util.getStringDateFromCal(item.getDate(), this),item.getAmount());
        ((TextView) holder.getView().findViewById(R.id.tvMain)).setText(mainText);

        ((TextView) holder.getView().findViewById(R.id.tvAdditional1)).setText(item.getComment());

        ImageButton btnDelete = ((ImageButton) holder.getView().findViewById(R.id.btnDelete));

        //здесь устанавливаем слушатели
        holder.getView().setOnClickListener(view -> onListItemClick(item, 0, rvHelperId));
        btnDelete.setOnClickListener(view -> onListItemClick(item, 1, rvHelperId));
    }

    private void onListItemClick(Transaction item, int clickedActionId, int rvHelperId) {
        switch (clickedActionId) {
            case 0://клик по строке. просто покажем тост, по чему мы кликнули
                //TODO: сделать
                String msg = item.toString(getApplicationContext());
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                new EditTransactionDF().show(getSupportFragmentManager(), EditTransactionDF.FRAGMENT_TAG);
                break;
            case 1://запрос на удаление
                if(rvHelper != null) {
                    ConfirmDeleteDF dialog = new ConfirmDeleteDF();
                    Bundle args = new Bundle();
                    args.putParcelable(Constants.OBJECT_ID_EXTRA, item);
                    dialog.setArguments(args);
                    dialog.show(getSupportFragmentManager(), ConfirmDeleteDF.FRAGMENT_TAG);
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PhotoSessionDetailsActivity.PHOTO_SESSION_EXTRA, photoSession);
    }

    @Override
    public void onAddNewItemDialogResult(List<String> newItemParams, int rvHelperId) {
        //TODO: сделать
        Toast.makeText(getApplicationContext(), newItemParams.get(0) + newItemParams.get(1), Toast.LENGTH_SHORT).show();
    }

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
        dataSource.getTransactionsSource().remove(item);
        rvHelper.removeItemFromList(item);
        Util.notifyBroadcastListeners(this);
        Toast.makeText(this, R.string.transactionDeletedMSG, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditTransactionDialogResult(List<String> newItemParams) {
        //TODO: сделать
        Toast.makeText(getApplicationContext(), newItemParams.get(0) + newItemParams.get(1), Toast.LENGTH_SHORT).show();
    }
}
