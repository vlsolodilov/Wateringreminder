package com.example.wateringreminder.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wateringreminder.AlarmReceiver;
import com.example.wateringreminder.Constants;
import com.example.wateringreminder.DbManager;
import com.example.wateringreminder.EditActivity;
import com.example.wateringreminder.R;
import com.example.wateringreminder.entity.Item;
import com.example.wateringreminder.entity.Reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> {
    private Context context;
    private List<TaskItem> taskItems;
    private long currentDate;
    private List<TaskItem> checkedTaskItems;
    private AlarmReceiver alarmReceiver;
    //private boolean isSelectedAll;

    public TasksAdapter(Context context) {
        this.context = context;
        taskItems = new ArrayList<>();
        checkedTaskItems = new ArrayList<>();
        alarmReceiver = new AlarmReceiver();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_tasks_layout, parent, false);
        return new MyViewHolder(view, context, taskItems);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TaskItem taskItem = taskItems.get(position);
        holder.setData(taskItem);
        //if (taskItem.getSelected()) holder.checkBox.setChecked(true);
        holder.setItemClickListener(new MyViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                CheckBox myCheckBox= (CheckBox) v;
                TaskItem currentTaskItem=taskItems.get(pos);

                if(myCheckBox.isChecked()) {
                    currentTaskItem.setSelected(true);
                    checkedTaskItems.add(currentTaskItem);
                }
                else if(!myCheckBox.isChecked()) {
                    currentTaskItem.setSelected(false);
                    checkedTaskItems.remove(currentTaskItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskItems.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivTask;
        private TextView tvNameTask, tvNamePlant;
        private CheckBox checkBox;
        private Context context;
        private List<TaskItem> taskItems;
        private ItemClickListener itemClickListener;


        public MyViewHolder(@NonNull View itemView, Context context, List<TaskItem> taskItems) {
            super(itemView);
            this.context = context;
            this.taskItems = taskItems;
            ivTask = itemView.findViewById(R.id.ivTask);
            tvNameTask = itemView.findViewById(R.id.tvNameTask);
            tvNamePlant = itemView.findViewById(R.id.tvNamePlant);
            checkBox = itemView.findViewById(R.id.checkBox);
            //itemView.setOnClickListener(this);
            checkBox.setOnClickListener(this);
        }

        public void setData(TaskItem taskItem){
            if (!taskItem.getImage().equals("empty"))
                ivTask.setImageURI(Uri.parse(taskItem.getImage()));
            else
                ivTask.setImageResource(R.drawable.ic_plant_in_pot2);

            tvNameTask.setText(taskItem.getNameTask());
            tvNamePlant.setText(taskItem.getNamePlant());
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(taskItem.getSelected());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    taskItem.setSelected(isChecked);
                }
            });
        }

        public void setItemClickListener(ItemClickListener ic)
        {
            this.itemClickListener=ic;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v,getLayoutPosition());
        }
        interface ItemClickListener {

            void onItemClick(View v,int pos);
        }

    }
    public void updateAdapter(List<Item> items, List<Reminder> reminders){
        taskItems.clear();
        taskItems = fillList(items, reminders);
        notifyDataSetChanged();
    }

    /*public void removeItem(int pos, DbManager dbManager){
        dbManager.updateReminderDate(currentDate, taskItems.get(pos).getIdReminder());
        taskItems.remove(pos);
        notifyItemRangeChanged(0, taskItems.size());
        notifyItemRemoved(pos);
    }*/

    public void executeCheckedTasks(DbManager dbManager){
        for (TaskItem ti : checkedTaskItems) {
            dbManager.updateReminderDate(currentDate, ti.getIdReminder());
            alarmReceiver.setAlarm(context, ti.getIdReminder(), ti.getTime(), ti.getPeriod(), currentDate);
            taskItems.remove(ti);
        }
        checkedTaskItems.clear();
        //isSelectedAll = false;
        notifyItemRangeChanged(0, taskItems.size());
        notifyDataSetChanged();
    }

    private List<TaskItem> fillList(List<Item> items, List<Reminder> reminders) {
        List<TaskItem> taskItems = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        currentDate = calendar.getTimeInMillis();
        long reminderDate;
        TaskItem taskItem;

        for (Reminder reminder : reminders) {
           reminderDate = reminder.getLast() + (long) (24 * 60 * 60 * 1000) * reminder.getPeriod();
           if (currentDate >= reminderDate) {
               taskItem = new TaskItem();
               taskItem.setIdReminder(reminder.getId());
               taskItem.setImage(getImageById(items, reminder.getPlant()));
               taskItem.setNameTask(reminder.getName());
               taskItem.setNamePlant(getNameById(items, reminder.getPlant()));
               taskItem.setTime(reminder.getTime());
               taskItem.setPeriod(reminder.getPeriod());
               taskItem.setLast(reminder.getLast());
               taskItems.add(taskItem);
           }
        }
        return taskItems;
    }

    private String getNameById(List<Item> items, int plant) {
        for (Item item : items) {
            if (item.getId() == plant)
                return item.getName();
        }
        return "";
    }

    private String getImageById(List<Item> items, int plant) {
        for (Item item : items) {
            if (item.getId() == plant)
                return item.getUri();
        }
        return "empty";
    }

    public void selectAll() {
        checkedTaskItems.clear();
        for (TaskItem taskItem : taskItems) {
            taskItem.setSelected(true);
        }
        checkedTaskItems.addAll(taskItems);
        notifyDataSetChanged();
    }

    private static class TaskItem {
        private int idReminder;
        private String image;
        private String nameTask;
        private String namePlant;
        private Boolean isSelected = false;
        private long time;
        private int period;
        private long last;

        public int getIdReminder() {
            return idReminder;
        }

        public void setIdReminder(int idReminder) {
            this.idReminder = idReminder;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getNameTask() {
            return nameTask;
        }

        public void setNameTask(String nameTask) {
            this.nameTask = nameTask;
        }

        public String getNamePlant() {
            return namePlant;
        }

        public void setNamePlant(String namePlant) {
            this.namePlant = namePlant;
        }

        public Boolean getSelected() {
            return isSelected;
        }

        public void setSelected(Boolean selected) {
            isSelected = selected;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public int getPeriod() {
            return period;
        }

        public void setPeriod(int period) {
            this.period = period;
        }

        public long getLast() {
            return last;
        }

        public void setLast(long last) {
            this.last = last;
        }
    }
}
