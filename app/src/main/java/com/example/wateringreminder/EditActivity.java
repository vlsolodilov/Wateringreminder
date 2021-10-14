package com.example.wateringreminder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wateringreminder.adapters.ReminderAdapter;
import com.example.wateringreminder.entity.Item;
import com.example.wateringreminder.entity.Reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditActivity extends AppCompatActivity implements OnDataReceived {


    private final int PICK_IMAGE_CODE = 111;
    private ImageView imNewImage;
    private EditText etName, etDescription, etDate;
    private DbManager dbManager;
    private String tempUri = "empty";
    private boolean isEditState;
    private Item item;
    private RecyclerView rvReminders;
    private ReminderAdapter reminderAdapter;
    private final Calendar dateAndTime = Calendar.getInstance();
    private SimpleDateFormat sdfDate;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit);
        init();
        getMyIntents();
        dbManager.openDb();
        id = getId();
    }
    @Override
    protected void onResume() {
        super.onResume();
        dbManager.openDb();
        readFromDb("");
    }

    private void readFromDb(final String text){

        AppExecuter.getInstance().getSubIO().execute(new Runnable() {
            @Override
            public void run() {
                dbManager.getFromDb(text, EditActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE_CODE && data != null){
            tempUri = data.getData().toString();
            imNewImage.setImageURI(data.getData());
            getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);

        }
    }

    private void init(){
        etName = findViewById(R.id.etNameEA);
        etDescription = findViewById(R.id.etDescriptionEA);
        etDate = findViewById(R.id.etDateEA);
        sdfDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        imNewImage = findViewById(R.id.imEA1);
        dbManager = new DbManager(this);
        rvReminders = (RecyclerView) findViewById(R.id.rvRemindersEA);
        rvReminders.setLayoutManager(new LinearLayoutManager(this));
        reminderAdapter = new ReminderAdapter(this);
        rvReminders.setAdapter(reminderAdapter);
        setInitialDate();
    }

    private void getMyIntents(){

        Intent i = getIntent();
        if(i != null){
            item = (Item)i.getSerializableExtra(Constants.ITEM_INTENT);
            isEditState = i.getBooleanExtra(Constants.EDIT_STATE_PLANT, false);

            if(isEditState){
                etName.setText(item.getName());
                etDescription.setText(item.getDescription());
                if (item.getDate() != 0)
                    etDate.setText(sdfDate.format(new Date(item.getDate())));
                if(!item.getUri().equals("empty")){
                    tempUri = item.getUri();
                    imNewImage.setImageURI(Uri.parse(item.getUri()));
                }
            }
        }

    }

    public void setDate(View v) {
        new DatePickerDialog(EditActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDate();
        }
    };

    private void setInitialDate() {
        etDate.setText(sdfDate.format(dateAndTime.getTimeInMillis()));
    }

    public void onClickSave(View view) {
        if (saveItem(id))
        finish();
    }

    private boolean saveItem(int id) {
        final String name = etName.getText().toString();
        final String description = etDescription.getText().toString();
        long date = 0L;
        try {
            date = sdfDate.parse(etDate.getText().toString()).getTime();
        } catch (ParseException e) {
            Toast.makeText(this, R.string.date_error, Toast.LENGTH_SHORT).show();
        }

        if (name.equals("")) {
            Toast.makeText(this, R.string.text_empty, Toast.LENGTH_SHORT).show();
        } else {
            if(!isEditState){
                long finalDate = date;
                AppExecuter.getInstance().getSubIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        dbManager.insertToDbItem(name, description, finalDate,tempUri);
                    }
                });
                isEditState = true;
            } else {
                dbManager.updateItem(name, description, date, tempUri, id);
            }
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            dbManager.closeDb();
            return true;
        }
        return false;
    }

    public void onClickChooseImage(View view){

        Intent chooser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooser.setType("image/*");
        startActivityForResult(chooser, PICK_IMAGE_CODE);

    }

    private int getId() {
        // TODO: убрать, очень вредно заранее считать айдишник
        if (item == null) {
            id = dbManager.getLastId(Constants.TABLE_PLANTS_NAME) + 1;
            if (id == 0)
                id = 1;
        } else
            id = item.getId();
        return id;
    }

    public void onClickAddReminder(View view) {

        Intent i = new Intent(EditActivity.this, EditReminderActivity.class);
        /*int id;
        if (item == null) {
            id = dbManager.getLastId(Constants.TABLE_PLANTS_NAME) + 1;
            if (!saveItem(id)) return;
        } else
            id = item.getId();*/
        if (!saveItem(id)) return;
        i.putExtra(Constants.ID_ITEM_INTENT, id);
        startActivity(i);
    }

    @Override
    public void onReceived(final List<Item> items, List<Reminder> reminders) {

        AppExecuter.getInstance().getMainIO().execute(new Runnable() {
            @Override
            public void run() {
                reminderAdapter.updateAdapter(items, reminders, id);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.closeDb();
    }
}