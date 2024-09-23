package com.example.datn.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datn.Entity.CartItem;
import com.example.datn.Entity.Product;
import com.example.datn.Entity.Review;
import com.example.datn.R;
import com.example.datn.databinding.ViewholderOrderProductBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderProductsAdapter extends RecyclerView.Adapter<OrderProductsAdapter.ViewHolder> {

    private ArrayList<CartItem> productList;
    private Context context;
    DecimalFormat decimalFormat = new DecimalFormat("#,###,###");

    public OrderProductsAdapter(ArrayList<CartItem> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderOrderProductBinding binding = ViewholderOrderProductBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductsAdapter.ViewHolder holder, int position) {
        CartItem product = productList.get(position);
        holder.binding.titleTxt.setText(product.getProductName());
        holder.binding.feeEachItem.setText(decimalFormat.format(product.getPrice()) + " VNĐ");
        holder.binding.numberItemTxt.setText("Số lượng: " + String.valueOf(product.getQuantity()));
        holder.binding.total.setText(decimalFormat.format(product.getQuantity() * product.getPrice()) + " VNĐ");
        holder.binding.size.setText("Size: " + product.getSize());
        holder.binding.color.setText("Màu: " + product.getColor());

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Products").child(product.getProductId());
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product productx = snapshot.getValue(Product.class);
                    if (productx != null && !productx.getPicUrls().isEmpty()) {
                        String imageUrl = productx.getPicUrls().get(0); // Lấy URL hình ảnh đầu tiên
                        Glide.with(context)
                                .load(imageUrl) // Đặt URL hình ảnh từ sản phẩm
                                .into(holder.binding.pic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });

        holder.itemView.setOnClickListener(view -> {
            DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("Products").child(product.getProductId()).child("reviews");
            reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean hasReviewed = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Review review = snapshot.getValue(Review.class);
                        if (review != null && review.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            hasReviewed = true;
                            break;
                        }
                    }
                    if (hasReviewed) {
                        Toast.makeText(context, "Bạn đã đánh giá sản phẩm này rồi!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Inflate the dialog layout
                        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_review, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setView(dialogView)
                                .setTitle("Đánh giá và Nhận xét")
                                .setPositiveButton("Gửi", (dialog, which) -> {
                                    RatingBar ratingInput = dialogView.findViewById(R.id.ratingBar);
                                    EditText commentInput = dialogView.findViewById(R.id.commentEditText);
                                    Float rating = ratingInput.getRating();
                                    String comment = commentInput.getText().toString().trim();

                                    if (!comment.isEmpty() && rating >= 1 && rating <= 5) {
                                        // Tạo một đối tượng Review mới
                                        Review newReview = new Review(FirebaseAuth.getInstance().getCurrentUser().getUid(), rating, comment);

                                        // Thêm review mới vào danh sách
                                        ArrayList<Review> reviewsList = new ArrayList<>();
                                        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    Review review = snapshot.getValue(Review.class);
                                                    reviewsList.add(review);
                                                }
                                                reviewsList.add(newReview);
                                                reviewsRef.setValue(reviewsList);
                                                Toast.makeText(context, "Đánh giá thành công!", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                // Xử lý lỗi nếu cần
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context, "Vui lòng nhập cả điểm đánh giá và nhận xét", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Hủy", null)
                                .show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu có
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderOrderProductBinding binding;

        public ViewHolder(ViewholderOrderProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
