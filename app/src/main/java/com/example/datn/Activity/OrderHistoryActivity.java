package com.example.datn.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.datn.Adapter.OrderHistoryAdapter;
import com.example.datn.Entity.CartItem;
import com.example.datn.Entity.Order;
import com.example.datn.Entity.Product;
import com.example.datn.Entity.Review;
import com.example.datn.R;
import com.example.datn.databinding.ActivityCartBinding;
import com.example.datn.databinding.ActivityOrderHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderHistoryActivity extends AppCompatActivity {

    ActivityOrderHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadOrderHistory();
        orderHistoryEvent();
    }

    private void orderHistoryEvent() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadOrderHistory() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        ArrayList<Order> orderHistory = new ArrayList<>();

// Lắng nghe dữ liệu từ Firebase Realtime Database
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    orderHistory.clear();  // Xóa dữ liệu cũ
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        try {
                            // Lấy thông tin đơn hàng
                            String orderDate = orderSnapshot.child("orderDate").getValue(String.class);
                            String status = orderSnapshot.child("status").getValue(String.class);
                            String voucherAppliedId = orderSnapshot.child("voucherApplied").getValue(String.class);
                            String orderId = String.valueOf(orderSnapshot.child("orderId").getValue(String.class)); // Đổi kiểu thành String
                            String userIdFromOrder = orderSnapshot.child("userId").getValue(String.class);
                            String name = orderSnapshot.child("name").getValue(String.class);
                            String phone = orderSnapshot.child("phone").getValue(String.class);
                            String address = orderSnapshot.child("address").getValue(String.class);
                            Double totalAmount = Double.valueOf(orderSnapshot.child("totalAmount").getValue(Double.class));

                            // Kiểm tra xem đơn hàng có thuộc về người dùng hiện tại không
                            if (userIdFromOrder != null && userIdFromOrder.equals(userId)) {
                                // Xử lý các sản phẩm
                                ArrayList<CartItem> products = new ArrayList<>();
                                DataSnapshot productsSnapshot = orderSnapshot.child("products");
                                for (DataSnapshot productSnapshot : productsSnapshot.getChildren()) {
                                    String cartId = productSnapshot.child("cartId").getValue(String.class);
                                    String productId = productSnapshot.child("productId").getValue(String.class);
                                    Double price = Double.valueOf(productSnapshot.child("price").getValue(Float.class));
                                    String productName = productSnapshot.child("productName").getValue(String.class);
                                    Integer quantity = productSnapshot.child("quantity").getValue(Integer.class);
                                    String size = productSnapshot.child("size").getValue(String.class);
                                    String color = productSnapshot.child("color").getValue(String.class);

                                    CartItem product = new CartItem(cartId, productId, productName, price, size, color, quantity);
                                    products.add(product);
                                }

                                // Tạo đối tượng Order
                                Order order = new Order(orderId, userIdFromOrder, name, phone, address, orderDate, products, totalAmount, status , voucherAppliedId);
                                orderHistory.add(order);
                            }
                        } catch (Exception e) {
                            Log.e("OrderHistoryActivity", "Lỗi khi phân tích dữ liệu đơn hàng", e);
                        }
                    }

                    if (!orderHistory.isEmpty()) {
                        // Thiết lập RecyclerView với OrderHistoryAdapter
                        binding.orderHistoryView.setLayoutManager(new LinearLayoutManager(OrderHistoryActivity.this, LinearLayoutManager.VERTICAL, false));
                        binding.orderHistoryView.setAdapter(new OrderHistoryAdapter(orderHistory, OrderHistoryActivity.this));
                    } else {
                        Toast.makeText(OrderHistoryActivity.this, "Không có đơn hàng nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "Không có đơn hàng nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderHistoryActivity", "Lỗi cơ sở dữ liệu", error.toException());
                Toast.makeText(OrderHistoryActivity.this, "Lỗi khi tải đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}