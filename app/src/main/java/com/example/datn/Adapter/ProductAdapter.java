package com.example.datn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.datn.Entity.Category;
import com.example.datn.Entity.Product;
import com.example.datn.databinding.ManageProductItemBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.Viewholder> {

    ArrayList<Product> items;
    OnItemClickListener listener;
    Context context;
    DecimalFormat decimalFormat = new DecimalFormat("#,###,###");


    public interface OnItemClickListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }


    public ProductAdapter(ArrayList<Product> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ManageProductItemBinding binding = ManageProductItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.Viewholder holder, int position) {
        holder.binding.productName.setText(items.get(position).getProductName());
//        holder.binding.numberReview.setText("" + items.get(position).getReviews());
        holder.binding.productPrice.setText(decimalFormat.format(items.get(position).getPrice()) + " VNĐ");
//        holder.binding.numberReview.setText(String.valueOf(items.get(position).getReviewCount()));
//        holder.binding.numberRating.setText("(" + (items.get(position).getReviewCount()) + ")");
//        holder.binding.ratingBar.setRating((float) items.get(position).getAverageRating());


        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop());

        Glide.with(context)
                .load(items.get(position).getPicUrls().get(0))
                .apply(requestOptions)
                .into(holder.binding.productImage);

        Product product = items.get(position);
        holder.binding.btnEdit.setOnClickListener(v -> listener.onEditClick(product));
        holder.binding.btnDelete.setOnClickListener(v -> listener.onDeleteClick(product));


    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        ManageProductItemBinding binding;

        public Viewholder(ManageProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


