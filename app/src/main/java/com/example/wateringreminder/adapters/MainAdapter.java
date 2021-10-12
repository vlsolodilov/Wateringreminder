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
import com.example.wateringreminder.EditActivity;
import com.example.wateringreminder.entity.Item;
import com.example.wateringreminder.R;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private Context context;
    private List<Item> mainArray;


    public MainAdapter(Context context) {
        this.context = context;
        mainArray = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_layout, parent, false);
        return new MyViewHolder(view, context, mainArray);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(mainArray.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mainArray.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle;
        private Context context;
        private List<Item> mainArray;

        public MyViewHolder(@NonNull View itemView, Context context, List<Item> mainArray) {
            super(itemView);
            this.context = context;
            this.mainArray = mainArray;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            itemView.setOnClickListener(this);
        }

        public void setData(String title){
            tvTitle.setText(title);
        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(context, EditActivity.class);
            i.putExtra(Constants.REMINDER_INTENT, mainArray.get(getAdapterPosition()));
            i.putExtra(Constants.EDIT_STATE_PLANT, false);
            context.startActivity(i);
        }
    }
    public void updateAdapter(List<Item> newList){

        mainArray.clear();
        mainArray.addAll(newList);
        notifyDataSetChanged();
    }

    public void removeItem(int pos, DbManager dbManager){
        dbManager.deleteItem(mainArray.get(pos).getId());
        mainArray.remove(pos);
        notifyItemRangeChanged(0, mainArray.size());
        notifyItemRemoved(pos);
    }

}
