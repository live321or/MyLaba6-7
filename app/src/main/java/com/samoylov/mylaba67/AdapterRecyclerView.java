package com.samoylov.mylaba67;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Item> list;
    private OnNoteListener mOnNoteListener;


    public AdapterRecyclerView(Context context, ArrayList<Item> list, OnNoteListener onNoteListener) {
        this.context = context;
        this.list = list;
        mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_front, parent, false);
        return new ViewHolder(v, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Item currentItem = list.get(position);
        String name = currentItem.getName();
        String price = Float.toString(currentItem.getPrice());
        holder.textViewName.setText(name);
        holder.textViewPrice.setText(price);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textViewName;
        public TextView textViewPrice;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewPrice = itemView.findViewById(R.id.text_view_price);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
            notifyDataSetChanged();
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
