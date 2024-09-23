package com.example.datn.Activity.Admin;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.datn.Activity.LoginActivity;
import com.example.datn.databinding.ActivityAdminMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class AdminMainActivity extends AppCompatActivity {

    private ActivityAdminMainBinding binding;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sử dụng View Binding
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập các sự kiện click cho từng CardView
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Quản lý banner
        binding.adminGrid.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, BannerManagementActivity.class);
                startActivity(intent);
            }
        });

        // Quản lý danh mục
        binding.adminGrid.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, CategoryManagementActivity.class);
                startActivity(intent);
            }
        });

        // Quản lý sản phẩm
        binding.adminGrid.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, ProductManagementActivity.class);
                startActivity(intent);
            }
        });

        // Quản lý đơn hàng
        binding.adminGrid.getChildAt(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, OrderManagementActivity.class);
                startActivity(intent);
            }
        });

        // Quản lý voucher
        binding.adminGrid.getChildAt(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, VoucherManagementActivity.class);
                startActivity(intent);
            }
        });

        // Thống kê doanh thu
        binding.adminGrid.getChildAt(5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, RevenueStatisticsActivity.class);
                startActivity(intent);
            }
        });

        // Nút đăng xuất
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(AdminMainActivity.this, LoginActivity.class));
                finish();
            }
        });

        // Nút Home
        binding.btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
