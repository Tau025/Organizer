package com.devtau.organizer.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.devtau.organizer.R;
import com.devtau.organizer.database.DataSource;
import com.devtau.organizer.fragments.DateTimeButtonsFrag;
import com.devtau.organizer.fragments.SingleChoiceListDF;
import com.devtau.organizer.fragments.SingleChoiceListDF.SingleChoiceListDFInterface;
import com.devtau.organizer.model.Client;
import com.devtau.organizer.model.PhotoSession;
import com.devtau.organizer.util.Logger;
import com.devtau.organizer.util.Util;
import java.util.ArrayList;
import java.util.Calendar;

public class PhotoSessionDetailsActivity extends AppCompatActivity implements
        View.OnClickListener,
        DateTimeButtonsFrag.DateTimeButtonsInterface,
        SingleChoiceListDFInterface {
    public static final String PHOTO_SESSION_EXTRA = "PhotoSessionExtra";
    public static final String CLIENT_EXTRA = "ClientExtra";
    private static final String LOG_TAG = PhotoSessionDetailsActivity.class.getSimpleName();
    private PhotoSession photoSession;
    private Client client;

    private DataSource dataSource;
    private TableLayout expandableClientDetails;
    private EditText etClientName, etClientPhone, etClientAddress, etClientSocial, etClientEmail;
    private AnimatorSet animatorSet;
    private LinearLayout moveableLayout;
    private DateTimeButtonsFrag startDateTimeButtonsFrag, endDateTimeButtonsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_session_details);

        dataSource = new DataSource(this);

        if(savedInstanceState == null) {
            photoSession = getIntent().getParcelableExtra(PHOTO_SESSION_EXTRA);
        } else {
            photoSession = savedInstanceState.getParcelable(PHOTO_SESSION_EXTRA);
            client = savedInstanceState.getParcelable(CLIENT_EXTRA);
        }
        if(photoSession == null) {
            photoSession = new PhotoSession(Calendar.getInstance());
        }

        if(photoSession.getClientID() == 0) {
            client = new Client();
        } else {
            client = dataSource.getClientsSource().getItemByID(photoSession.getClientID());
        }

        Util.hideSoftKeyboard(this);
        initControls();

        insertStartDateTimeButtonsFrag(photoSession.getPhotoSessionDate());
        insertEndDateTimeButtonsFrag(photoSession.getDeadline());
    }

    private void initControls() {
        if(photoSession == null) return;

        initButtons();

        etClientName = (EditText) findViewById(R.id.etClientName);
        expandableClientDetails = (TableLayout) findViewById(R.id.expandableClientDetails);
        etClientPhone = (EditText) findViewById(R.id.etClientPhone);
        etClientAddress = (EditText) findViewById(R.id.etClientAddress);
        etClientSocial = (EditText) findViewById(R.id.etClientSocial);
        etClientEmail = (EditText) findViewById(R.id.etClientEmail);

        moveableLayout = (LinearLayout) findViewById(R.id.moveableLayout);
        Spinner spnPhotoSessionType = (Spinner) findViewById(R.id.spnPhotoSessionType);
        EditText etPhotoSessionAddress = (EditText) findViewById(R.id.etPhotoSessionAddress);
        EditText etPresentToClientName = (EditText) findViewById(R.id.etPresentToClientDescription);
        EditText etPresentToClientCost = (EditText) findViewById(R.id.etPresentToClientCost);
        EditText etPhotoSessionTotalCost = (EditText) findViewById(R.id.etPhotoSessionTotalCost);
        TextView tvBalance = (TextView) findViewById(R.id.tvPhotoSessionBalance);

        EditText etPricePerHour = (EditText) findViewById(R.id.etPhotoSessionPricePerHour);
        EditText etHoursSpentPlan = (EditText) findViewById(R.id.etHoursSpentPlan);
        EditText etHoursSpentFact = (EditText) findViewById(R.id.etHoursSpentFact);


        populateClientDetails(client);


        if(spnPhotoSessionType != null) {
            spnPhotoSessionType.setSelection(photoSession.getPhotoSessionTypeID(), true);
            spnPhotoSessionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View itemSelected,
                                           int selectedItemPosition, long selectedId) {
                    photoSession.setPhotoSessionTypeID(selectedItemPosition);
                }
                public void onNothingSelected(AdapterView<?> parent) {/*NOP*/}
            });
        }


        if(etPhotoSessionAddress != null) {
            if(!"".equals(photoSession.getPhotoSessionAddress())) {
                etPhotoSessionAddress.setText(photoSession.getPhotoSessionAddress());
            }
            etPhotoSessionAddress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*NOP*/}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    photoSession.setPhotoSessionAddress(charSequence.toString());
                }
                @Override
                public void afterTextChanged(Editable editable) {/*NOP*/}
            });
        }


        if(etPresentToClientName != null) {
            if(!"".equals(photoSession.getPresentToClientDescription())) {
                etPresentToClientName.setText(photoSession.getPresentToClientDescription());
            }
            etPresentToClientName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*NOP*/}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    photoSession.setPresentToClientDescription(charSequence.toString());
                }
                @Override
                public void afterTextChanged(Editable editable) {/*NOP*/}
            });
        }


        if(etPresentToClientCost != null) {
            if(photoSession.getPresentToClientCost() != 0) {
                etPresentToClientCost.setText(String.valueOf(photoSession.getPresentToClientCost()));
            }
            etPresentToClientCost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*NOP*/}

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    int newPresentToClientCost = 0;
                    if(!"".equals(charSequence.toString())) {
                        try {
                            newPresentToClientCost = Integer.parseInt(charSequence.toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), R.string.integerFormatException, Toast.LENGTH_SHORT).show();
                        }
                    }
                    photoSession.setPresentToClientCost(newPresentToClientCost);
                }

                @Override
                public void afterTextChanged(Editable editable) {/*NOP*/}
            });
        }


        if(etPhotoSessionTotalCost != null) {
            if(photoSession.getTotalCost() != 0) {
                etPhotoSessionTotalCost.setText(String.valueOf(photoSession.getTotalCost()));
            }
            etPhotoSessionTotalCost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*NOP*/}

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    //TODO: связать с tvBalance
                    int newTotalCost = 0;
                    if(!"".equals(charSequence.toString())) {
                        try {
                            newTotalCost = Integer.parseInt(charSequence.toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), R.string.integerFormatException, Toast.LENGTH_SHORT).show();
                        }
                    }
                    photoSession.setTotalCost(newTotalCost);
                }

                @Override
                public void afterTextChanged(Editable editable) {/*NOP*/}
            });
        }


        if(tvBalance != null && photoSession.getBalance() != 0) {
            tvBalance.setText(String.valueOf(photoSession.getBalance()));
        }


        if(etPricePerHour != null) {
            if(photoSession.getPricePerHour() != 0) {
                etPricePerHour.setText(String.valueOf(photoSession.getPricePerHour()));
            }
            etPricePerHour.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*NOP*/}

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    int newPricePerHour = 0;
                    if(!"".equals(charSequence.toString())) {
                        try {
                            newPricePerHour = Integer.parseInt(charSequence.toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), R.string.integerFormatException, Toast.LENGTH_SHORT).show();
                        }
                    }
                    photoSession.setPricePerHour(newPricePerHour);
                }

                @Override
                public void afterTextChanged(Editable editable) {/*NOP*/}
            });
        }


        if(etHoursSpentPlan != null) {
            if(photoSession.getHoursSpentPlan() != 0) {
                etHoursSpentPlan.setText(String.valueOf(photoSession.getHoursSpentPlan()));
            }
            etHoursSpentPlan.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*NOP*/}

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    double newHoursSpentPlan = 0;
                    if(!"".equals(charSequence.toString())) {
                        try {
                            newHoursSpentPlan = Double.parseDouble(charSequence.toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), R.string.doubleFormatException, Toast.LENGTH_SHORT).show();
                        }
                    }
                    photoSession.setHoursSpentPlan(newHoursSpentPlan);
                }

                @Override
                public void afterTextChanged(Editable editable) {/*NOP*/}
            });
        }


        if(etHoursSpentFact != null) {
            if(photoSession.getHoursSpentFact() != 0) {
                etHoursSpentFact.setText(String.valueOf(photoSession.getHoursSpentFact()));
            }
            etHoursSpentFact.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*NOP*/}

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    double newHoursSpentFact = 0;
                    if(!"".equals(charSequence.toString())) {
                        try {
                            newHoursSpentFact = Double.parseDouble(charSequence.toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), R.string.doubleFormatException, Toast.LENGTH_SHORT).show();
                        }
                    }
                    photoSession.setHoursSpentFact(newHoursSpentFact);
                }

                @Override
                public void afterTextChanged(Editable editable) {/*NOP*/}
            });
        }
    }

    private void initButtons() {
        Button btnClientDetails = (Button) findViewById(R.id.btnClientDetails);
        ImageButton btnChooseClient = (ImageButton) findViewById(R.id.btnChooseClient);
        Button btnAccountingDetails = (Button) findViewById(R.id.btnAccountingDetails);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        if(btnClientDetails != null && btnChooseClient != null && btnAccountingDetails != null
               && btnSave != null && btnCancel != null) {
            btnClientDetails.setOnClickListener(this);
            btnChooseClient.setOnClickListener(this);
            btnAccountingDetails.setOnClickListener(this);
            btnSave.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
        }
    }

    private void populateClientDetails(final Client client) {
        if(client != null && etClientName != null && etClientPhone != null && etClientAddress != null
                && etClientSocial != null && etClientEmail != null) {
            etClientName.setText(client.getName());
            etClientPhone.setText(client.getPhone());
            etClientAddress.setText(client.getAddress());
            etClientSocial.setText(client.getSocial());
            etClientEmail.setText(client.getEmail());

            etClientName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*NOP*/}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    client.setName(charSequence.toString());
                }
                @Override
                public void afterTextChanged(Editable editable) {/*NOP*/}
            });

            etClientPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*NOP*/}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    client.setPhone(charSequence.toString());
                }
                @Override
                public void afterTextChanged(Editable editable) {/*NOP*/}
            });

            etClientAddress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*NOP*/}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    client.setAddress(charSequence.toString());
                }
                @Override
                public void afterTextChanged(Editable editable) {/*NOP*/}
            });

            etClientSocial.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*NOP*/}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    client.setSocial(charSequence.toString());
                }
                @Override
                public void afterTextChanged(Editable editable) {/*NOP*/}
            });

            etClientEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {/*NOP*/}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    client.setEmail(charSequence.toString());
                }
                @Override
                public void afterTextChanged(Editable editable) {/*NOP*/}
            });
        }
    }

    private void insertStartDateTimeButtonsFrag(Calendar initDate) {
        if(photoSession == null) return;

        FragmentManager fragmentManager = getSupportFragmentManager();

        startDateTimeButtonsFrag = (DateTimeButtonsFrag) fragmentManager.findFragmentByTag(DateTimeButtonsFrag.FRAGMENT_START_TAG);
        if (startDateTimeButtonsFrag == null) {
            startDateTimeButtonsFrag = new DateTimeButtonsFrag();
            Bundle args = new Bundle();
            args.putLong(DateTimeButtonsFrag.DATE_TIME_EXTRA, initDate.getTimeInMillis());
            args.putInt(DateTimeButtonsFrag.FRAGMENT_ID_EXTRA, R.id.startDateTimePlaceHolder);
            startDateTimeButtonsFrag.setArguments(args);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.startDateTimePlaceHolder, startDateTimeButtonsFrag, DateTimeButtonsFrag.FRAGMENT_START_TAG);
            ft.commit();
        } else {
            startDateTimeButtonsFrag.setDateTime(photoSession.getPhotoSessionDate());
        }
    }

    private void insertEndDateTimeButtonsFrag(Calendar initDate) {
        if(photoSession == null) return;

        FragmentManager fragmentManager = getSupportFragmentManager();

        endDateTimeButtonsFrag = (DateTimeButtonsFrag) fragmentManager.findFragmentByTag(DateTimeButtonsFrag.FRAGMENT_END_TAG);
        if (endDateTimeButtonsFrag == null) {
            endDateTimeButtonsFrag = new DateTimeButtonsFrag();
            Bundle args = new Bundle();
            args.putLong(DateTimeButtonsFrag.DATE_TIME_EXTRA, initDate.getTimeInMillis());
            args.putInt(DateTimeButtonsFrag.FRAGMENT_ID_EXTRA, R.id.deadlineDateTimePlaceHolder);
            endDateTimeButtonsFrag.setArguments(args);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.deadlineDateTimePlaceHolder, endDateTimeButtonsFrag, DateTimeButtonsFrag.FRAGMENT_END_TAG);
            ft.commit();
        } else {
            endDateTimeButtonsFrag.setDateTime(photoSession.getDeadline());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnClientDetails:
                if(expandableClientDetails.getLayoutParams().height == 0) {
                    animate(expandableClientDetails, moveableLayout, 500, 0, 200);
                } else {
                    animate(expandableClientDetails, moveableLayout, 500, 0, -200);
                }
                break;

            case R.id.btnChooseClient:
                openClientsList();
                break;

            case R.id.btnAccountingDetails:
                Intent intent = new Intent(this, AccountingDetailsActivity.class);
                intent.putExtra(PHOTO_SESSION_EXTRA, photoSession);
                startActivity(intent);
                break;

            case R.id.btnSave:
                //сохраним новую фотосессию в бд или обновим старую
                if(photoSession.getPhotoSessionID() == -1) {
                    photoSession.setPhotoSessionID(dataSource.getPhotoSessionsSource().create(photoSession));
                } else {
                    dataSource.getPhotoSessionsSource().update(photoSession);
                }

                //сохраним нового клиента или обновим старого только если у него есть имя
                if(!"".equals(client.getName())) {
//                    Logger.d("client.getName(): " + String.valueOf(client.getName()));
                    if (client.getClientID() == -1) {
                        client.setClientID(dataSource.getClientsSource().create(client));
                    } else {
                        dataSource.getClientsSource().update(client);
                    }
                }

                //вернемся в CalendarActivity
                Util.notifyBroadcastListeners(this);
                super.onBackPressed();
                break;

            case R.id.btnCancel:
                super.onBackPressed();
                break;
        }
    }

    private void openClientsList() {
        SingleChoiceListDF dialog = new SingleChoiceListDF();
        Bundle args = new Bundle();
        ArrayList<Client> clientsList = dataSource.getClientsSource().getClientsList();
        args.putParcelableArrayList(SingleChoiceListDF.ARG_ITEMS_LIST, clientsList);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), SingleChoiceListDF.FRAGMENT_TAG);
    }

    private void animate(final View scalableView, final View moveableView,
                         int duration, int fromYDelta, int toYDelta) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Logger.d(LOG_TAG, "metrics.density: " + String.valueOf(metrics.density));
        fromYDelta *= metrics.density;
        toYDelta *= metrics.density;
        final int scalableViewHeight = scalableView.getHeight() + toYDelta - fromYDelta;

        ObjectAnimator mover = ObjectAnimator.ofFloat(moveableView, "translationY", (float) fromYDelta, (float) toYDelta);
        scalableView.setAlpha(0);
        animatorSet = new AnimatorSet();
        animatorSet.play(mover);
        animatorSet.setDuration(duration);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //необходимо для корректного завершения анимации перемещения
                animator = ObjectAnimator.ofFloat(moveableView, "translationY", 0.0f, 0.0f);
                animator.setDuration(1);
                animator.start();

                scalableView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, scalableViewHeight));
                scalableView.setAlpha(1);
                moveableView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        });
        animatorSet.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PHOTO_SESSION_EXTRA, photoSession);
        outState.putParcelable(CLIENT_EXTRA, client);
    }


    @Override
    public void onDateOrTimeSet(Calendar newDate, int fragmentID) {
        switch (fragmentID) {
            case R.id.startDateTimePlaceHolder:
                if(newDate.after(photoSession.getDeadline())) {
                    startDateTimeButtonsFrag.setDateTime(photoSession.getPhotoSessionDate());
                    Toast.makeText(getApplicationContext(), R.string.dateError1, Toast.LENGTH_SHORT).show();
                } else {
                    photoSession.setPhotoSessionDate(newDate);
                }
                break;

            case R.id.deadlineDateTimePlaceHolder:
                if(newDate.before(photoSession.getPhotoSessionDate())) {
                    endDateTimeButtonsFrag.setDateTime(photoSession.getDeadline());
                    Toast.makeText(getApplicationContext(), R.string.dateError2, Toast.LENGTH_SHORT).show();
                } else {
                    photoSession.setDeadline(newDate);
                }
                break;
        }
        Logger.d(LOG_TAG, "startDate: " + Util.dateFormat.format(photoSession.getPhotoSessionDate().getTime()) +
                ", endDate: " + Util.dateFormat.format(photoSession.getDeadline().getTime()));
    }

    @Override
    public void processListItem(Client client) {
        this.client = client;
        photoSession.setClientID(client.getClientID());
        populateClientDetails(client);
    }
}
