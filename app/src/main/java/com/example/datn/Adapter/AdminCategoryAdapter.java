package com.example.datn.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datn.Entity.Category;
import com.example.datn.R;

import java.util.ArrayList;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.ViewHolder> {

    private final ArrayList<Category> categories;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Category category);

        void onDeleteClick(Category category);
    }

    public AdminCategoryAdapter(ArrayList<Category> categories, OnItemClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.titleTextView.setText(category.getTitle());
        Glide.with(holder.itemView.getContext()).load(category.getPicUrl()).into(holder.picImageView);

        holder.editButton.setOnClickListener(v -> listener.onEditClick(category));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView titleTextView;
        final ImageView picImageView;
        final ImageView editButton;
        final ImageView deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            picImageView = itemView.findViewById(R.id.imageViewPic);
            editButton = itemView.findViewById(R.id.btnEdit);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }
}
