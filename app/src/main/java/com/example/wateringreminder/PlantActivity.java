package com.example.wateringreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.wateringreminder.adapters.ReminderAdapter;
import com.example.wateringreminder.entity.Item;
import com.example.wateringreminder.entity.Reminder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlantActivity extends AppCompatActivity implements OnDataReceived {

    private ImageView imPA;
    private EditText etNamePA, etDescriptionPA, etDatePA;
    private TextInputLayout etDescriptionPAContainer;
    private ImageButton ibPADelete, ibPAEdit;
    private DbManager dbManager;
    private String tempUri = "empty";
    private Item item;
    private RecyclerView rvRemindersPA;
    private ReminderAdapter reminderAdapter;
    private SimpleDateFormat sdfDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_plant);
        init();
        getMyIntents();
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
                dbManager.getFromDb(text, PlantActivity.this);
            }
        });
    }

    private void init(){
        etNamePA = findViewById(R.id.etNamePA);
        etDescriptionPA = findViewById(R.id.etDescriptionPA);
        etDatePA = findViewById(R.id.etDatePA);
        etDescriptionPAContainer = findViewById(R.id.etDescriptionContainerPA);
        ibPAEdit = findViewById(R.id.ibPAEdit);
        sdfDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        imPA = findViewById(R.id.imPA1);
        dbManager = new DbManager(this);
        rvRemindersPA = (RecyclerView) findViewById(R.id.rvRemindersPA);
        rvRemindersPA.setLayoutManager(new LinearLayoutManager(this));
        reminderAdapter = new ReminderAdapter(this);
        rvRemindersPA.setAdapter(reminderAdapter);
        ibPAEdit = findViewById(R.id.ibPAEdit);
        ibPAEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlantActivity.this, EditActivity.class);
                i.putExtra(Constants.ITEM_INTENT, item);
                i.putExtra(Constants.EDIT_STATE_PLANT, true);
                startActivity(i);
            }
        });
    }

    private void getMyIntents(){

        Intent i = getIntent();
        if(i != null){
            item = (Item)i.getSerializableExtra(Constants.ITEM_INTENT);

            etNamePA.setText(item.getName());
            if (!item.getDescription().equals(""))
                etDescriptionPA.setText(item.getDescription());
            else
                etDescriptionPAContainer.setVisibility(View.GONE);
            etDatePA.setText(sdfDate.format(new Date(item.getDate())));
            if (!item.getUri().equals("empty")) {
                tempUri = item.getUri();
                imPA.setImageURI(Uri.parse(item.getUri()));
            }
            ibPADelete = findViewById(R.id.ibPADelete);
            ibPADelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertDialog();
                }
            });
        }
    }

    private void showAlertDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Предупреждение")
                .setMessage("Вы действительно хотите удалить?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppExecuter.getInstance().getSubIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                dbManager.deleteItem(item.getId());
                            }
                        });
                        PlantActivity.this.finish();
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
    public void onReceived(final List<Item> items, List<Reminder> reminders) {

        AppExecuter.getInstance().getMainIO().execute(new Runnable() {
            @Override
            public void run() {
                if (item != null)
                    reminderAdapter.updateAdapter(items, reminders, item.getId());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.closeDb();
    }
}