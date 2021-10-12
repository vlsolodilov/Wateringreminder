package com.example.wateringreminder.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wateringreminder.Constants;
import com.example.wateringreminder.DbManager;
import com.example.wateringreminder.EditActivity;
import com.example.wateringreminder.PlantActivity;
import com.example.wateringreminder.entity.Item;
import com.example.wateringreminder.R;

import java.util.ArrayList;
import java.util.List;

public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.MyViewHolder> {

    LayoutInflater inflater;
    private Context context;
    private List<Item> mainArray;

    public PlantsAdapter(Context context){
        mainArray = new ArrayList<>();
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @NonNull
    @Override
    public PlantsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid_layout, parent, false);
        return new PlantsAdapter.MyViewHolder(view, context, mainArray);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantsAdapter.MyViewHolder holder, int position) {
        holder.setData(mainArray.get(position).getName(), mainArray.get(position).getUri());
    }

    @Override
    public int getItemCount() {
        return mainArray.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName;
        private ImageView ivDefaultPlant;
        private Context context;
        private List<Item> mainArray;

        public MyViewHolder(@NonNull View itemView, Context context, List<Item> mainArray) {
            super(itemView);
            this.context = context;
            this.mainArray = mainArray;
            tvName = itemView.findViewById(R.id.tvName);
            ivDefaultPlant = itemView.findViewById(R.id.ivDefaultPlant);
            itemView.setOnClickListener(this);

        }
        public void setData(String name, String uri){
            tvName.setText(name);
            if (!uri.equals("empty"))
                ivDefaultPlant.setImageURI(Uri.parse(uri));
            else
                ivDefaultPlant.setImageResource(R.drawable.ic_plant_in_pot2);
        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(context, PlantActivity.class);
            i.putExtra(Constants.ITEM_INTENT, mainArray.get(getAdapterPosition()));
            //i.putExtra(Constants.EDIT_STATE_PLANT, false);
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
