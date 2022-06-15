package com.example.appbirthday.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbirthday.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvNameList, tvDateList;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNameList = (TextView) itemView.findViewById(R.id.tvNameList);
            tvDateList = (TextView) itemView.findViewById(R.id.tvDateList);
        }
    }

    public List<ItemModel> itemLista;

    public RecyclerViewAdapter(List<ItemModel> itemLista) {
        this.itemLista = itemLista;
    }

    //Create the view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    //Bind the view holder with the correct items
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvNameList.setText(itemLista.get(position).getName());
        holder.tvDateList.setText(itemLista.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return itemLista.size();
    }
}
