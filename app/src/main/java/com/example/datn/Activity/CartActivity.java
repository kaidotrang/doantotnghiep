package com.example.datn.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.datn.Adapter.CartAdapter;
import com.example.datn.Entity.CartItem;
import com.example.datn.Entity.Order;
import com.example.datn.Entity.Voucher;
import com.example.datn.R;
import com.example.datn.databinding.ActivityCartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    ArrayList<CartItem> cartItems = new ArrayList<>();
    DatabaseReference cartRef;

    Voucher voucher;
    Order newOrder;
    private double totalAmount = 0;
    private double discountAmount = 0;

    DecimalFormat decimalFormat = new DecimalFormat("#,###,###");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadCart();

        cartEvent();
    }

    private void cartEvent() {
        binding.backBtn.setOnClickListener(view -> finish());
        binding.btnApply.setOnClickListener(view -> {
            String voucherCode = binding.edtVoucher.getText().toString().trim();
            if (TextUtils.isEmpty(voucherCode)) {
                Toast.makeText(CartActivity.this, "Vui lòng nhập mã voucher", Toast.LENGTH_SHORT).show();
            } else {
                applyVoucher(voucherCode);
            }
        });
        binding.checkOutBtn.setOnClickListener(view -> showCheckoutDialog());
    }

    // Function to apply a voucher code
    private void applyVoucher(String voucherCode) {
        DatabaseReference voucherRef = FirebaseDatabase.getInstance().getReference("Vouchers");

        voucherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean voucherFound = false;
                for (DataSnapshot voucherSnapshot : dataSnapshot.getChildren()) {
                    String id = voucherSnapshot.child("voucherId").getValue(String.class);
                    String code = voucherSnapshot.child("code").getValue(String.class);
                    int discount = voucherSnapshot.child("discount").getValue(Integer.class);
                    String validFrom = voucherSnapshot.child("validFrom").getValue(String.class);
                    String validUntil = voucherSnapshot.child("validUntil").getValue(String.class);
                    voucher = new Voucher(id,code, discount, validFrom, validUntil);

                    // Kiểm tra mã voucher và trạng thái
                    if (code != null && code.equals(voucherCode)) {
                        try {
                            // Kiểm tra xem voucher có hợp lệ trong khoảng thời gian không
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date currentDate = new Date();
                            Date fromDate = sdf.parse(validFrom);
                            Date untilDate = sdf.parse(validUntil);

                            if (currentDate.after(fromDate) && currentDate.before(untilDate)) {
                                discountAmount = totalAmount * discount / 100;
                                updateTotalAmount();
                                voucherFound = true;
                                break;
                            } else {
                                Toast.makeText(CartActivity.this, "Voucher không khả dụng", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (!voucherFound) {
                    Toast.makeText(CartActivity.this, "Mã voucher không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CartActivity.this, "Đã xảy ra lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalAmount() {
        binding.discountTxt.setText(decimalFormat.format(discountAmount)+" VNĐ");
        totalAmount = totalAmount - discountAmount;
        binding.totalTxt.setText(decimalFormat.format(totalAmount)+ "VNĐ");
    }

    private void loadCart() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("cart");

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        CartItem cartItem = issue.getValue(CartItem.class);
                        if (cartItem != null) {
                            cartItems.add(cartItem);
                        }
                    }
                    if (!cartItems.isEmpty()) {
                        setupCartAdapter();
                    }
                }
                else {
                    binding.emptyTxt.setVisibility(View.VISIBLE);
                    binding.scrollViewCart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Không thể tải giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCartAdapter() {
        CartAdapter.OnCartItemChangeListener listener = item -> {
            if (item.getProductId() != null) {
                cartRef.child(item.getProductId()).setValue(item)
                        .addOnSuccessListener(aVoid -> Toast.makeText(CartActivity.this, "Cập nhật giỏ hàng thành công", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(CartActivity.this, "Cập nhật giỏ hàng thất bại", Toast.LENGTH_SHORT).show());
            }
        };

        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(cartItems, this, listener));

        float totalFee = 0;
        for (CartItem item : cartItems) {
            totalFee += item.getPrice() * item.getQuantity();
        }
        totalAmount = totalFee;
        binding.totalFeeTxt.setText(decimalFormat.format(totalFee) + " VNĐ");
        binding.discountTxt.setText(String.valueOf(0) + " VNĐ"); // Discount có thể được tính thêm
        binding.totalTxt.setText(decimalFormat.format(totalFee) + " VNĐ");
    }

    private void showCheckoutDialog() {
        // Inflate layout dialog_checkout.xml
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_checkout, null);

        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        EditText phoneEditText = dialogView.findViewById(R.id.phoneEditText);
        EditText addressEditText = dialogView.findViewById(R.id.addressEditText);

        new AlertDialog.Builder(this)
                .setTitle("Thông tin nhận hàng")
                .setView(dialogView)
                .setPositiveButton("Đặt hàng", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    String phone = phoneEditText.getText().toString().trim();
                    String address = addressEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
                        Toast.makeText(CartActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    } else {
                        createNewOrder(name, phone, address);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void createNewOrder(String name, String phone, String address) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        String orderId = ordersRef.push().getKey(); // Tạo orderId từ Firebase
        String orderDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        if (voucher != null){
            newOrder = new Order(orderId, userId, name, phone, address, orderDate, cartItems,totalAmount, "Đang xử lý", voucher.getVoucherId());
        }
        else {
            newOrder = new Order(orderId, userId, name, phone, address, orderDate, cartItems, "Đang xử lý");
        }

        if (orderId != null) {
            ordersRef.child(orderId).setValue(newOrder)
                    .addOnSuccessListener(aVoid -> {
                        // Xóa giỏ hàng sau khi đặt hàng thành công
                        cartRef.removeValue()
                                .addOnSuccessListener(aVoid1 -> Toast.makeText(CartActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(CartActivity.this, "Không thể xóa giỏ hàng", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(CartActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show());
        }
    }
}
