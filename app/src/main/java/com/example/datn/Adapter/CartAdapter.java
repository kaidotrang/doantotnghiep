package com.example.datn.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datn.Entity.CartItem;
import com.example.datn.Entity.Product;
import com.example.datn.R;
import com.example.datn.databinding.ViewholderCartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private ArrayList<CartItem> items;
    private Context context;
    private OnCartItemChangeListener listener;

    DecimalFormat decimalFormat = new DecimalFormat("#,###,###");


    public interface OnCartItemChangeListener {
        void onQuantityChanged(CartItem item);
    }

    public CartAdapter(ArrayList<CartItem> items, Context context, OnCartItemChangeListener listener) {
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CartAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartItem item = items.get(position);

        // Cài đặt thông tin sản phẩm
        holder.binding.titleTxt.setText(item.getProductName());
        holder.binding.feeEachItem.setText(decimalFormat.format(item.getPrice())+" VNĐ");
        holder.binding.totalEachItem.setText(decimalFormat.format(item.getPrice() * item.getQuantity())+ " VNĐ");
        holder.binding.numberItemTxt.setText(String.valueOf(item.getQuantity()));
        holder.binding.size.setText("Kích cỡ: " + item.getSize());
        holder.binding.color.setText("Màu: " + item.getColor());

        // Lấy thông tin sản phẩm từ Firebase dựa trên productId
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Products").child(item.getProductId());
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null && !product.getPicUrls().isEmpty()) {
                        String imageUrl = product.getPicUrls().get(0); // Lấy URL hình ảnh đầu tiên
                        Glide.with(context)
                                .load(imageUrl) // Đặt URL hình ảnh từ sản phẩm
                                .into(holder.binding.pic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
                Log.e("FirebaseError", "Error getting product data", error.toException());
            }
        });


        // Xử lý sự kiện khi bấm nút tăng số lượng
        // Xử lý sự kiện khi bấm nút tăng số lượng
        holder.binding.plusCartBtn.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            int stock = product.getStock(); // Lấy số lượng tồn kho từ sản phẩm
                            int currentQuantity = item.getQuantity();
                            int newQuantity = currentQuantity + 1;

                            if (newQuantity > stock) {
                                // Hiển thị thông báo khi số lượng vượt quá số lượng tồn kho
                                Toast.makeText(context, "Số lượng sản phẩm vượt quá tồn kho", Toast.LENGTH_SHORT).show();
                            } else {
                                // Cập nhật số lượng nếu chưa vượt quá tồn kho
                                DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Users")
                                        .child(userId)
                                        .child("cart")
                                        .child(item.getCartId());

                                cartItemRef.child("quantity").setValue(newQuantity)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Thay đổi số lượng thành công", Toast.LENGTH_SHORT).show();
                                                holder.binding.numberItemTxt.setText(String.valueOf(newQuantity));
                                                holder.binding.totalEachItem.setText(decimalFormat.format(item.getPrice() * newQuantity) + " VNĐ");
                                            } else {
                                                Toast.makeText(context, "Thay đổi số lượng không thành công", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error getting product data", error.toException());
                }
            });
        });


        holder.binding.minusCartBtn.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity() - 1;
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(userId)
                    .child("cart")
                    .child(item.getCartId());

            if (currentQuantity > 0) {
                cartItemRef.child("quantity").setValue(currentQuantity)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Thay đổi số lượng thành công", Toast.LENGTH_SHORT).show();
                                holder.binding.numberItemTxt.setText(String.valueOf(currentQuantity));
                                holder.binding.totalEachItem.setText(String.valueOf(item.getPrice() * currentQuantity));
                            } else {
                                Toast.makeText(context, "Thay đổi số lượng không thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                cartItemRef.removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                int i = holder.getAdapterPosition();

                                if (i != RecyclerView.NO_POSITION && i < items.size()) {
                                    // Kiểm tra xem danh sách có phần tử tại vị trí này không
                                    items.remove(i);
                                    notifyItemRemoved(i);
                                } else {
                                    // Cập nhật toàn bộ danh sách nếu không thể xác định được vị trí chính xác
                                    notifyDataSetChanged();
                                }

                                Toast.makeText(context, "Sản phẩm đã được xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Không thể xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public ViewHolder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
