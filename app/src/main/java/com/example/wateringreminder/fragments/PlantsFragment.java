package com.example.wateringreminder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wateringreminder.AppExecuter;
import com.example.wateringreminder.DbManager;
import com.example.wateringreminder.EditActivity;
import com.example.wateringreminder.PlantActivity;
import com.example.wateringreminder.adapters.PlantsAdapter;
import com.example.wateringreminder.entity.Item;
import com.example.wateringreminder.OnDataReceived;
import com.example.wateringreminder.R;
import com.example.wateringreminder.entity.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

public class PlantsFragment extends Fragment implements OnDataReceived {

    private DbManager dbManager;
    private RecyclerView plantsRecyclerView;
    private PlantsAdapter plantsAdapter;
    private FloatingActionButton fab;

    public PlantsFragment() {
    }

    public static PlantsFragment newInstance() {
        return new PlantsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plants, container, false);
        init(rootView);
        setHasOptionsMenu(true);
        return rootView;

    }

    private void init(View rootView) {
        dbManager = new DbManager(getContext());
        plantsRecyclerView = (RecyclerView) rootView.findViewById(R.id.plantsRecyclerView);
        plantsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        plantsAdapter = new PlantsAdapter(getContext());
        plantsRecyclerView.setAdapter(plantsAdapter);
        fab = rootView.findViewById(R.id.fabAddPlant);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditActivity.class);
                startActivity(i);
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
                dbManager.getFromDb(text, PlantsFragment.this);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.id_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                readFromDb(newText);
                return false;
            }
        });
    }

    @Override
    public void onReceived(final List<Item> items, List<Reminder> reminders) {

        AppExecuter.getInstance().getMainIO().execute(new Runnable() {
            @Override
            public void run() {
                plantsAdapter.updateAdapter(items);
            }
        });
    }
}
