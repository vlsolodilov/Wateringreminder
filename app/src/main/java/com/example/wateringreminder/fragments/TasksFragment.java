package com.example.wateringreminder.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wateringreminder.AppExecuter;
import com.example.wateringreminder.DbManager;

import com.example.wateringreminder.OnDataReceived;
import com.example.wateringreminder.R;
import com.example.wateringreminder.adapters.TasksAdapter;
import com.example.wateringreminder.entity.Item;
import com.example.wateringreminder.entity.Reminder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class TasksFragment extends Fragment implements OnDataReceived {

    private DbManager dbManager;
    private RecyclerView rvTasks;
    private TasksAdapter tasksAdapter;
    private TextView tvEmpty;
    private ExtendedFloatingActionButton fabExecute, fabSelectAll;

    public TasksFragment() {
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        dbManager = new DbManager(getContext());
        rvTasks = rootView.findViewById(R.id.rvTasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksAdapter = new TasksAdapter(getContext());
        rvTasks.setAdapter(tasksAdapter);
        tvEmpty = rootView.findViewById(R.id.tvEmpty);
        fabExecute = rootView.findViewById(R.id.fabExecute);
        fabSelectAll = rootView.findViewById(R.id.fabSelectAll);
        fabExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksAdapter.executeCheckedTasks(dbManager);
                checkForEmptyList();
            }
        });
        fabSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksAdapter.selectAll();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        dbManager.openDb();
        readFromDb("");
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
                dbManager.getFromDb(text, TasksFragment.this);
            }
        });
    }

    private void checkForEmptyList() {
        if (tasksAdapter.getItemCount() == 0) {
            rvTasks.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        else {
            rvTasks.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onReceived(final List<Item> items, List<Reminder> reminders) {

        AppExecuter.getInstance().getMainIO().execute(new Runnable() {
            @Override
            public void run() {
                tasksAdapter.updateAdapter(items, reminders);
                checkForEmptyList();
            }
        });
    }



}

