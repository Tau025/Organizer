package com.devtau.organizer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.devtau.organizer.R;
import com.devtau.organizer.database.DataSource;
import com.devtau.organizer.fragments.ConfirmDeleteDF;
import com.devtau.organizer.model.PhotoSession;
import com.devtau.organizer.model.PhotoSessionComparators;
import com.devtau.organizer.util.Constants;
import com.devtau.organizer.util.Util;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import com.devtau.recyclerviewlib.MyItemRVAdapter;
import com.devtau.recyclerviewlib.RVHelper;
import com.devtau.recyclerviewlib.RVHelperInterface;

public class PhotoSessionsListActivity extends AppCompatActivity implements
        RVHelperInterface,
        ConfirmDeleteDF.ConfirmDeleteDFListener<PhotoSession> {
    private static final String ARG_INDEX_OF_SORT_METHOD = "indexOfSortMethod";
    private DataSource dataSource;
    private BroadcastReceiver receiver;
    private RVHelper rvHelper;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_sessions_list);

        dataSource = new DataSource(this);

        selectedDate = Calendar.getInstance();
        selectedDate.setTimeInMillis(getIntent().getLongExtra(Constants.SELECTED_DATE_EXTRA, 0));

        initControls(selectedDate);
        initRecycler(selectedDate, savedInstanceState);
    }

    private void initControls(final Calendar selectedDate) {
        Button btnNewTask = (Button) findViewById(R.id.btnNewTask);
        ActionBar actionBar = getSupportActionBar();

        if(btnNewTask != null && actionBar != null) {
            btnNewTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startTaskDetailsActivity(null, selectedDate);
                }
            });

            Locale locale = getResources().getConfiguration().locale;
            String actionBarTitle = String.format(locale, getResources().getString(R.string.photoSessionsListTitleFormatter),
                    selectedDate.get(Calendar.DAY_OF_MONTH), selectedDate.get(Calendar.MONTH) + 1,
                    selectedDate.get(Calendar.YEAR) % 1000);

            actionBar.setTitle(actionBarTitle);
        }
    }

    private void initRecycler(final Calendar selectedDate, Bundle savedInstanceState) {
        //запросим из бд список, который нам нужно показать
        ArrayList<PhotoSession> itemsList = dataSource.getPhotoSessionsSource().getTasksListForADay(selectedDate);

        //соберем из подготовленных вводных данных хелпер
        rvHelper = RVHelper.Builder.<PhotoSession> start(this, R.id.rv_helper_placeholder).setList(itemsList)
                .build();
        rvHelper.addItemFragmentToLayout(this, R.id.rv_helper_placeholder);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<PhotoSession> tasksList = dataSource.getPhotoSessionsSource().getTasksListForADay(selectedDate);
                rvHelper.setList(tasksList);
            }
        };
        registerReceiver(receiver, new IntentFilter(Constants.BROADCAST_REFRESH_TAG));
    }

    private void startTaskDetailsActivity(PhotoSession selectedPhotoSession, Calendar selectedDate) {
        if(selectedPhotoSession == null) {
            selectedPhotoSession = new PhotoSession(selectedDate);
        }

        Intent intent = new Intent(PhotoSessionsListActivity.this, PhotoSessionDetailsActivity.class);
        intent.putExtra(PhotoSessionDetailsActivity.PHOTO_SESSION_EXTRA, selectedPhotoSession);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //деактивируем приемник широковещательных сообщений
        unregisterReceiver(receiver);
    }


    @Override
    public void onBindViewHolder(MyItemRVAdapter.ViewHolder holder, final int rvHelperId) {
        //здесь выбираем, какие поля хранимого объекта отобразятся в каких частях CardView
        //TextView в разметке по умолчанию такие: tvMain, tvAdditional1, tvAdditional2
        final PhotoSession item = (PhotoSession) holder.getItem();

        String clientName = "client not defined";
        if(item.getClientID() != 0) {
            clientName = dataSource.getClientsSource().getItemByID(item.getClientID()).getName();
        }
        ((TextView) holder.getView().findViewById(R.id.tvMain)).setText(clientName);
        Locale locale = getResources().getConfiguration().locale;
        String additionalText = String.format(locale, getResources().getString(R.string.date_time_and_price_formatter),
                Util.getStringDateTimeFromCal(item.getPhotoSessionDate()), item.getTotalCost());
        ((TextView) holder.getView().findViewById(R.id.tvAdditional1)).setText(additionalText);
        ImageButton btnDelete = ((ImageButton) holder.getView().findViewById(R.id.btnDelete));

        //здесь устанавливаем слушатели
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListItemClick(item, 0, rvHelperId);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListItemClick(item, 1, rvHelperId);
            }
        });
    }

    private void onListItemClick(PhotoSession item, int clickedActionId, int rvHelperId) {
        switch (clickedActionId) {
            case 0://клик по строке. перейдем к подробностям по выбранной задаче
                //перейдем к подробностям по выбранной задаче
                startTaskDetailsActivity(item, selectedDate);
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
