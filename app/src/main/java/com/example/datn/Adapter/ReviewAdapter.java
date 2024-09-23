package com.example.datn.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.datn.Entity.Review;
import com.example.datn.databinding.ViewholderReviewBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    ArrayList<Review> items;
    Context context;

    public ReviewAdapter(ArrayList<Review> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderReviewBinding binding = ViewholderReviewBinding.inflate(LayoutInflater.from(context),parent,false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        holder.binding.descTxt.setText(items.get(position).getComment());
        holder.binding.numberRating.setText("" + items.get(position).getRating());
        // Lấy thông tin tên người dùng dựa vào userId
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(items.get(position).getUserId());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("name").getValue(String.class);
                    String imageProfile = snapshot.child("imgProfile").getValue(String.class);
                    // Hiển thị tên người đánh giá
                    holder.binding.nameTxt.setText(userName);
                    Glide.with(context)
                            .load(imageProfile)
                            .apply(RequestOptions.circleCropTransform())
                            .into(holder.binding.pic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ViewholderReviewBinding binding;
        public ViewHolder(ViewholderReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
