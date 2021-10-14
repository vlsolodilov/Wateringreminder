package com.example.wateringreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.wateringreminder.entity.Reminder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditReminderActivity extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteTextView;
    private EditText etTime, etPeriod, etLast;
    private Button bDeleteReminder;
    private DbManager dbManager;
    private boolean isEditState = false;
    private Reminder reminder;
    private final Calendar dateAndTime = Calendar.getInstance();
    private int plant;
    private SimpleDateFormat sdfTime, sdfDate;
    private AlarmReceiver alarmReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_reminder);
        init();
        getMyIntents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbManager.openDb();
    }

    private void init() {
        dbManager = new DbManager(this);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        String[] reminders = getResources().getStringArray(R.array.reminders);
        List<String> list = Arrays.asList(reminders);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, list);
        autoCompleteTextView.setAdapter(adapter);

        etTime = findViewById(R.id.etTime);
        etPeriod = findViewById(R.id.etPeriod);
        etLast = findViewById(R.id.etLast);

        etPeriod.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1 && s.toString().equals("0"))
                    etPeriod.setText("");
            }
        });

        sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdfDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        setInitialTime();
        setInitialDate();
        alarmReceiver = new AlarmReceiver();
        bDeleteReminder = findViewById(R.id.bDeleteReminder);
        bDeleteReminder.setVisibility(View.GONE);
    }

    private void getMyIntents(){

        Intent i = getIntent();
        if(i != null){
            reminder = (Reminder) i.getSerializableExtra(Constants.REMINDER_INTENT);
            isEditState = i.getBooleanExtra(Constants.EDIT_STATE_REMINDER, false);
            plant = i.getIntExtra(Constants.ID_ITEM_INTENT, -1);

            if(isEditState){
                plant = reminder.getPlant();
                autoCompleteTextView.setText(reminder.getName());
                etTime.setText(sdfTime.format(new Date(reminder.getTime())));
                etPeriod.setText(String.valueOf(reminder.getPeriod()));
                etLast.setText(sdfDate.format(new Date(reminder.getLast())));

                bDeleteReminder.setVisibility(View.VISIBLE);
                bDeleteReminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialog(reminder.getId());
                    }
                });
            }
        }
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(EditReminderActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(EditReminderActivity.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }
    // установка начальных даты и времени
    private void setInitialTime() {
        etTime.setText(sdfTime.format(dateAndTime.getTimeInMillis()));
    }

    private void setInitialDate() {
        etLast.setText(sdfDate.format(dateAndTime.getTimeInMillis()));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDate();
        }
    };


    public void onClickSave(View view) {

        long time = 0L;
        long last = 0L;
        int period = 0;
        String name = autoCompleteTextView.getText().toString();
        try {
            time = sdfTime.parse(etTime.getText().toString()).getTime();
            last = sdfDate.parse(etLast.getText().toString()).getTime();
            period = Integer.parseInt(etPeriod.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this, R.string.fields_empty, Toast.LENGTH_SHORT).show();
        }

        if (name.equals("") || etTime.getText().toString().equals("")
                || etPeriod.getText().toString().equals("") || etLast.getText().toString().equals("")) {
            Toast.makeText(this, R.string.fields_empty, Toast.LENGTH_SHORT).show();
        } else {
            if(!isEditState){
                long finalTime = time;
                long finalLast = last;
                int finalPeriod = period;
                // TODO: убрать, очень вредно заранее считать айдишник
                int id = dbManager.getLastId(Constants.TABLE_REMINDERS_NAME) + 1;
                AppExecuter.getInstance().getSubIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        dbManager.insertToDbReminder(name, plant, finalTime, finalPeriod, finalLast);
                    }
                });
                alarmReceiver.setAlarm(this, id, time,
                        period, last);
                Toast.makeText(this, R.string.reminder_saved, Toast.LENGTH_SHORT).show();
            } else {
                dbManager.updateReminder(name, plant, time, period, last, reminder.getId());
                alarmReceiver.setAlarm(this, reminder.getId(), time,
                        period, last);
                Toast.makeText(this, R.string.reminder_saved, Toast.LENGTH_SHORT).show();
            }
            dbManager.closeDb();
            finish();
        }
    }

    private void showAlertDialog(int id) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Предупреждение")
                .setMessage("Вы действительно хотите удалить?")
        .setPositiveButton("Да", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppExecuter.getInstance().getSubIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        dbManager.deleteReminder(id);
                    }
                });
                alarmReceiver.cancelAlarm(EditReminderActivity.this, id);
                EditReminderActivity.this.finish();
            }
        })
        .setNegativeButton("Отмена", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        })
        .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.closeDb();
    }
}