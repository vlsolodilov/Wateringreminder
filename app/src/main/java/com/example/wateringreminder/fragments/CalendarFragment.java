package com.example.wateringreminder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wateringreminder.AppExecuter;
import com.example.wateringreminder.MainActivity;
import com.example.wateringreminder.adapters.CalendarAdapter;
import com.example.wateringreminder.DbManager;
import com.example.wateringreminder.EditActivity;
import com.example.wateringreminder.entity.Item;
import com.example.wateringreminder.OnDataReceived;
import com.example.wateringreminder.R;
import com.example.wateringreminder.entity.Reminder;

import java.util.List;

public class CalendarFragment extends Fragment implements OnDataReceived {

    private DbManager dbManager;
    private RecyclerView rvCalendar;
    private CalendarAdapter calendarAdapter;

    public CalendarFragment() {
    }

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        init(rootView);

        return rootView;
    }
    private void init(View rootView) {
        dbManager = new DbManager(getContext());
        rvCalendar = (RecyclerView) rootView.findViewById(R.id.rvCalendar);
        rvCalendar.setLayoutManager(new LinearLayoutManager(getContext()));
        calendarAdapter = new CalendarAdapter(getContext());
        rvCalendar.setAdapter(calendarAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        dbManager.openDb();
        readFromDb("");
    }


    public void onClickAdd(View view) {

        Intent i = new Intent(PlantsFragment.newInstance().getContext(), EditActivity.class);
        startActivity(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.closeDb();
    }

    private void readFromDb(final String text){

        AppExecuter.getInstance().getSubIO().execute(new Runnable() {
            @Override
            public void run() {
                dbManager.getFromDb(text, CalendarFragment.this);
            }
        });

    }

    @Override
    public void onReceived(final List<Item> items, List<Reminder> reminders) {

        AppExecuter.getInstance().getMainIO().execute(new Runnable() {
            @Override
            public void run() {
                calendarAdapter.updateAdapter(items, reminders);
            }
        });
    }
}
