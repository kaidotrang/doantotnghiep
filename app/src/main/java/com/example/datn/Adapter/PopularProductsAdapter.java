package com.example.datn.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.datn.Activity.ProductDetailActivity;
import com.example.datn.Entity.Product;
import com.example.datn.databinding.PopularItemBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PopularProductsAdapter extends RecyclerView.Adapter<PopularProductsAdapter.Viewholder> {

    ArrayList<Product> items;
    Context context;
    DecimalFormat decimalFormat = new DecimalFormat("#,###,###");


    public PopularProductsAdapter(ArrayList<Product> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PopularProductsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        PopularItemBinding binding = PopularItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularProductsAdapter.Viewholder holder, int position) {
        holder.binding.popularProductName.setText(items.get(position).getProductName());
        holder.binding.numberReview.setText("" + items.get(position).getReviews());
        holder.binding.popularProductPrice.setText(decimalFormat.format(items.get(position).getPrice()) + " VNĐ");
        holder.binding.numberReview.setText(String.valueOf(items.get(position).getReviewCount()));
        holder.binding.numberRating.setText("(" + (items.get(position).getReviewCount())+ ")" );
        holder.binding.ratingBar.setRating((float) items.get(position).getAverageRating());


        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop());

        if (items.get(position).getPicUrls() != null && !items.get(position).getPicUrls().isEmpty()) {
            Glide.with(context)
                    .load(items.get(position).getPicUrls().get(0))
                    .apply(requestOptions)
                    .into(holder.binding.imagePopularItem);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("object", items.get(currentPosition));
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        PopularItemBinding binding;

        public Viewholder(PopularItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

