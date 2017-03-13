package com.devtau.organizer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.devtau.organizer.R;
import com.devtau.organizer.database.DataSource;
import com.devtau.organizer.fragments.ConfirmDeleteDF;
import com.devtau.organizer.model.PhotoSession;
import com.devtau.organizer.model.PhotoSessionComparators;
import com.devtau.organizer.util.Constants;
import com.devtau.organizer.util.ContactParser;
import com.devtau.organizer.util.DBWorkState;
import com.devtau.organizer.util.Logger;
import com.devtau.organizer.util.Util;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import com.devtau.recyclerviewlib.MyItemRVAdapter;
import com.devtau.recyclerviewlib.RVHelper;
import com.devtau.recyclerviewlib.RVHelperInterface;
import rx.Observable;

public class PhotoSessionsListActivity extends AppCompatActivity implements
        RVHelperInterface,
        ConfirmDeleteDF.ConfirmDeleteDFListener<PhotoSession> {
    private static final String LOG_TAG = PhotoSessionsListActivity.class.getSimpleName();
    private DataSource dataSource;
    private BroadcastReceiver receiver;
    private RVHelper<PhotoSession> rvHelper;
    private TextView rvHelperMessage;
    private ImageView errorSign;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_sessions_list);

        dataSource = new DataSource(this);

        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTimeInMillis(getIntent().getLongExtra(Constants.SELECTED_DATE_EXTRA, 0));

        initControls(selectedDate);
        if (savedInstanceState == null) {
            initRecycler(selectedDate);
            initBroadcastReceiver(selectedDate);
        }
    }

    private void initControls(Calendar selectedDate) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String actionBarTitle = String.format(Locale.getDefault(), getResources().getString(R.string.photoSessionsListTitleFormatter),
                    selectedDate.get(Calendar.DAY_OF_MONTH), selectedDate.get(Calendar.MONTH) + 1,
                    selectedDate.get(Calendar.YEAR) % 1000);
            actionBar.setTitle(actionBarTitle);
        }

        rvHelperMessage = (TextView) findViewById(R.id.rv_helper_message);
        errorSign = (ImageView) findViewById(R.id.error_sign);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> startTaskDetailsActivity(new PhotoSession(selectedDate)));
    }

    private void initRecycler(Calendar selectedDate) {
        showDBWorkState(DBWorkState.GETTING_DATA);
        Observable<List<PhotoSession>> photoSessionsListObservable = dataSource.getPhotoSessionsSource().getPhotoSessionsListForADayAsync(selectedDate);
        photoSessionsListObservable.subscribe(itemsList -> {
            showDBWorkState(itemsList.size() == 0 ? DBWorkState.WORK_COMPLETED_NO_DATA : DBWorkState.WORK_COMPLETED);
            rvHelper = RVHelper.Builder.<PhotoSession>start(PhotoSessionsListActivity.this, R.id.rv_helper_placeholder)
                    .setList((ArrayList<PhotoSession>) itemsList)
                    .withDividers(true)
                    .build();
            progressBar.setVisibility(View.INVISIBLE);
            rvHelper.addItemFragmentToLayout(PhotoSessionsListActivity.this, R.id.rv_helper_placeholder);
        }, throwable -> {
            showDBWorkState(DBWorkState.ERROR);
            Logger.e(LOG_TAG, "Error while initializing list: " + throwable.getMessage());
        });
    }

    private void showDBWorkState(DBWorkState state) {
        if (rvHelperMessage == null || progressBar == null) return;
        switch (state) {
            case GETTING_DATA:
                rvHelperMessage.setText(R.string.getting_list_from_database);
                progressBar.setVisibility(View.VISIBLE);
                break;

            case WORK_COMPLETED:
                rvHelperMessage.setText("");
                errorSign.setVisibility(View.INVISIBLE);
                break;

            case WORK_COMPLETED_NO_DATA:
                rvHelperMessage.setText(R.string.nothing_planned);
                errorSign.setVisibility(View.INVISIBLE);
                break;

            case ERROR:
                rvHelperMessage.setText(R.string.error_while_initializing_list);
                errorSign.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initBroadcastReceiver(Calendar selectedDate) {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initRecycler(selectedDate);
            }
        };
        registerReceiver(receiver, new IntentFilter(Constants.BROADCAST_REFRESH_TAG));
    }

    private void startTaskDetailsActivity(PhotoSession selectedPhotoSession) {
        Intent intent = new Intent(PhotoSessionsListActivity.this, PhotoSessionDetailsActivity.class);
        intent.putExtra(PhotoSessionDetailsActivity.PHOTO_SESSION_EXTRA, selectedPhotoSession);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //деактивируем приемник широковещательных сообщений
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }


    @Override
    public void onBindViewHolder(MyItemRVAdapter.ViewHolder holder, final int rvHelperId) {
        //здесь выбираем, какие поля хранимого объекта отобразятся в каких частях CardView
        //TextView в разметке по умолчанию такие: tvMain, tvAdditional1, tvAdditional2
        final PhotoSession photoSession = (PhotoSession) holder.getItem();

        String clientName = "client not defined";
        if(photoSession.getClientID() != 0 && !"".equals(photoSession.getClientLookupKey())) {
            clientName = ContactParser.getName(photoSession.getClientID(), photoSession.getClientLookupKey(), getContentResolver());
        }
        ((TextView) holder.getView().findViewById(R.id.tvMain)).setText(clientName);
        String additionalText = String.format(Locale.getDefault(), getResources().getString(R.string.date_time_and_price_formatter),
                Util.getStringDateTimeFromCal(photoSession.getPhotoSessionDate()), photoSession.getTotalCost());
        ((TextView) holder.getView().findViewById(R.id.tvAdditional1)).setText(additionalText);
        ImageButton btnDelete = ((ImageButton) holder.getView().findViewById(R.id.btnDelete));

        //здесь устанавливаем слушатели
        holder.getView().setOnClickListener(view -> onListItemClick(photoSession, 0, rvHelperId));
        btnDelete.setOnClickListener(view -> onListItemClick(photoSession, 1, rvHelperId));
    }

    private void onListItemClick(PhotoSession photoSession, int clickedActionId, int rvHelperId) {
        switch (clickedActionId) {
            case 0://клик по строке. перейдем к подробностям по выбранной задаче
                //перейдем к подробностям по выбранной задаче
                startTaskDetailsActivity(photoSession);
                break;
            case 1://запрос на удаление
                if(rvHelper != null) {
                    ConfirmDeleteDF dialog = new ConfirmDeleteDF();
                    Bundle args = new Bundle();
                    args.putParcelable(Constants.OBJECT_ID_EXTRA, photoSession);
                    dialog.setArguments(args);
                    dialog.show(getSupportFragmentManager(), ConfirmDeleteDF.FRAGMENT_TAG);
                }
                break;
        }
    }

    @Override
    public void onAddNewItemDialogResult(List<String> newItemParams, int rvHelperId) {
    }

    @Override
    public Comparator provideComparator(int indexOfSortMethod) {
        return PhotoSessionComparators.provideComparator(indexOfSortMethod);
    }

    @Override
    public void deleteConfirmed(PhotoSession item) {
        dataSource.getPhotoSessionsSource().remove(item);
        rvHelper.removeItemFromList(item);
        Util.notifyBroadcastListeners(this);
        Toast.makeText(this, R.string.photoSessionDeletedMSG, Toast.LENGTH_SHORT).show();
    }
}
