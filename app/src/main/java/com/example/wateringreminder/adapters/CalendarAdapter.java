package com.example.wateringreminder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wateringreminder.entity.Item;
import com.example.wateringreminder.R;
import com.example.wateringreminder.entity.Reminder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    LayoutInflater inflater;
    private Context context;
    //private List<Reminder> mainArray;
    private List<Item> items;
    private List<Reminder> reminders;
    private List<CalendarItem> calendarItems;

    public CalendarAdapter(Context context){
        items = new ArrayList<>();
        reminders = new ArrayList<>();
        calendarItems = new ArrayList<>();
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public CalendarAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_calendar_layout, parent, false);
        return new CalendarAdapter.MyViewHolder(view, context, calendarItems);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.MyViewHolder holder, int position) {
        CalendarItem calendarItem = calendarItems.get(position);
        holder.setData(calendarItem.getDate(), calendarItem.getTasks());
    }

    @Override
    public int getItemCount() {
        return calendarItems.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvDate;
        private TextView tvListToday;
        private Context context;
        private List<CalendarItem> calendarItems;

        public MyViewHolder(@NonNull View itemView, Context context, List<CalendarItem> calendarItems) {
            super(itemView);
            this.context = context;
            this.calendarItems = calendarItems;
            tvDate = itemView.findViewById(R.id.tvNameReminder);
            tvListToday = itemView.findViewById(R.id.tv11);
            itemView.setOnClickListener(this);
        }

        public void setData(Date date, String tasks){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM, EEEE", Locale.getDefault());
            tvDate.setText(simpleDateFormat.format(date));
            tvListToday.setText(tasks);
        }

        @Override
        public void onClick(View v) {

            //Intent i = new Intent(context, EditActivity.class);
            //i.putExtra(Constants.ITEM_INTENT, mainArray.get(getAdapterPosition()));
            //i.putExtra(Constants.EDIT_STATE, false);
            //context.startActivity(i);
        }
    }
    public void updateAdapter(List<Item> items, List<Reminder> reminders){
        calendarItems.clear();
        calendarItems = fillList(items, reminders);
        notifyDataSetChanged();
    }

    private List<CalendarItem> fillList(List<Item> items, List<Reminder> reminders) {
        Map<Date, String> map = new TreeMap<>();
        List<CalendarItem> calendarItems = new ArrayList<>();

        Date date;
        String task;
        reminders = increaseReminders(reminders);
        for (Reminder reminder : reminders) {
            date = new Date(reminder.getLast());
            task = getNameById(items, reminder.getPlant()) + " - " + reminder.getName();
            if (!map.containsKey(date))
                map.put(date, task);
            else
                map.put(date, map.get(date) + "\n" + task);
        }
        for (Date d : map.keySet())
            calendarItems.add(new CalendarItem(d, map.get(d)));

        return calendarItems;
    }

    private String getNameById(List<Item> items, int plant) {
        for (Item item : items) {
            if (item.getId() == plant)
                return item.getName();
        }
        return "";
    }

    private List<Reminder> increaseReminders(List<Reminder> reminders) {
        List<Reminder> result = new ArrayList<>();
        for (Reminder reminder : reminders) {
            for (int i = 1; i <= 10; i++) {
                long newDate = reminder.getLast() + (long) (i * 24 * 60 * 60 * 1000) * reminder.getPeriod();
                Reminder r = new Reminder(reminder.getId(), reminder.getName(), reminder.getPlant(),
                        reminder.getTime(), reminder.getPeriod(), newDate);
                result.add(r);
            }
        }
        return result;
    }

    private class CalendarItem {
        private Date date;
        private String tasks;

        public CalendarItem(Date date, String tasks) {
            this.date = date;
            this.tasks = tasks;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getTasks() {
            return tasks;
        }

        public void setTasks(String tasks) {
            this.tasks = tasks;
        }
    }
}
