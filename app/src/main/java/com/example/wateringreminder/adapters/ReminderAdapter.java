package com.example.wateringreminder.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wateringreminder.Constants;
import com.example.wateringreminder.DbManager;
import com.example.wateringreminder.EditReminderActivity;
import com.example.wateringreminder.R;
import com.example.wateringreminder.entity.Item;
import com.example.wateringreminder.entity.Reminder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {
    private Context context;
    private List<Reminder> mainArray;


    public ReminderAdapter(Context context) {
        this.context = context;
        mainArray = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_reminder_layout, parent, false);
        return new MyViewHolder(view, context, mainArray);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Reminder reminder = mainArray.get(position);
        holder.setData(reminder.getName(), reminder.getPeriod(), reminder.getTime(), reminder.getLast());
    }

    @Override
    public int getItemCount() {
        return mainArray.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvNameReminder, tvPeriod, tvTime, tvLast;
        private Context context;
        private List<Reminder> mainArray;

        public MyViewHolder(@NonNull View itemView, Context context, List<Reminder> mainArray) {
            super(itemView);
            this.context = context;
            this.mainArray = mainArray;
            tvNameReminder = itemView.findViewById(R.id.tvNameReminder);
            tvPeriod = itemView.findViewById(R.id.tvPeriod);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLast = itemView.findViewById(R.id.tvLast);
            itemView.setOnClickListener(this);
        }

        public void setData(String name, int period, long time, long last){
            tvNameReminder.setText(name);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            tvPeriod.setText(String.valueOf(period));
            tvTime.setText(simpleDateFormat1.format(time));
            tvLast.setText(simpleDateFormat2.format(last));
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, EditReminderActivity.class);
            i.putExtra(Constants.REMINDER_INTENT, mainArray.get(getAdapterPosition()));
            i.putExtra(Constants.EDIT_STATE_REMINDER, true);
            context.startActivity(i);
        }
    }
    public void updateAdapter(List<Item> items, List<Reminder> reminders, int id){

        mainArray.clear();
        for (Reminder r : reminders) {
            if (r.getPlant() == id)
                mainArray.add(r);
        }
        notifyDataSetChanged();
    }

    public void removeItem(int pos, DbManager dbManager){
        dbManager.deleteItem(mainArray.get(pos).getId());
        mainArray.remove(pos);
        notifyItemRangeChanged(0, mainArray.size());
        notifyItemRemoved(pos);
    }

}
