package com.example.datn.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.datn.Adapter.BannerSliderAdapter;
import com.example.datn.Adapter.CategoryAdapter;
import com.example.datn.Adapter.PopularProductsAdapter;
import com.example.datn.Entity.Banner;
import com.example.datn.Entity.Category;
import com.example.datn.Entity.Product;
import com.example.datn.Helper.OnCategoryClickListener;
import com.example.datn.databinding.ActivityCustomerMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerMainActivity extends BaseActivity implements OnCategoryClickListener {

    ActivityCustomerMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBanner();
        initCategory();
        initPopular();
        findProduct();
        bottomNavigation();
    }

    private void findProduct() {
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.vgroup1.setVisibility(View.GONE);
                binding.vgroup2.setVisibility(View.GONE);
                binding.textView2.setVisibility(View.GONE);
                String name = binding.edtSearch.getText().toString();
                DatabaseReference myRef = database.getReference("Products");
                binding.progressBarPopular.setVisibility(View.VISIBLE);
                ArrayList<Product> items = new ArrayList<>();
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot issue : snapshot.getChildren()) {
                                Product product = issue.getValue(Product.class);
                                if (product != null && (product.getProductName().contains(name)))
                                    items.add(product);
                            }
                            if (!items.isEmpty()) {
                                binding.recyclerViewPopular.setLayoutManager(new GridLayoutManager(CustomerMainActivity.this, 2));
                                binding.recyclerViewPopular.setAdapter(new PopularProductsAdapter(items));

                            }
                            binding.progressBarPopular.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                binding.edtSearch.clearFocus();
            }
        });
    }

    private void bottomNavigation() {
        binding.btnExplore.setOnClickListener(view -> recreate());
        binding.btnCart.setOnClickListener(v -> startActivity(new Intent(CustomerMainActivity.this, CartActivity.class)));
        binding.btnOrderHistory.setOnClickListener(v -> startActivity(new Intent(CustomerMainActivity.this, OrderHistoryActivity.class)));
        binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerMainActivity.this,SettingsActivity.class));
            }
        });
    }


    private void initPopular() {
        DatabaseReference myRef = database.getReference("Products");
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        ArrayList<Product> items = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();  // Xóa danh sách cũ trước khi thêm dữ liệu mới
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Product product = issue.getValue(Product.class);
                        if (product != null) {
                            items.add(product);
                        }
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewPopular.setLayoutManager(new GridLayoutManager(CustomerMainActivity.this, 2));
                        // Nếu adapter đã được khởi tạo, chỉ cần cập nhật dữ liệu
                        PopularProductsAdapter adapter = (PopularProductsAdapter) binding.recyclerViewPopular.getAdapter();
                        if (adapter == null) {
                            adapter = new PopularProductsAdapter(items);
                            binding.recyclerViewPopular.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                binding.progressBarPopular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
                binding.progressBarPopular.setVisibility(View.GONE);
            }
        });
    }



    private void initCategory() {
        DatabaseReference myRef = database.getReference("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> items = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();  // Xóa danh sách cũ trước khi thêm dữ liệu mới
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(Category.class));
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewCategory.setLayoutManager(new LinearLayoutManager(CustomerMainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewCategory.setAdapter(new CategoryAdapter(items, CustomerMainActivity.this));
                    }
                    binding.progressBarCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }


    private void initBanner() {
        DatabaseReference myRef = database.getReference("Banner");
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<Banner> items = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();  // Xóa danh sách cũ trước khi thêm dữ liệu mới
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(Banner.class));
                    }
                }
                banners(items);  // Cập nhật giao diện với dữ liệu mới
                binding.progressBarBanner.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
                binding.progressBarBanner.setVisibility(View.GONE);
            }
        });
    }


    private void banners(ArrayList<Banner> items) {
        binding.viewPagerSlider.setAdapter(new BannerSliderAdapter(items, binding.viewPagerSlider));
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPagerSlider.setPageTransformer(compositePageTransformer);
    }


    @Override
    public void onCategoryClick(Category category) {

        binding.vgroup1.setVisibility(View.GONE);
        binding.vgroup2.setVisibility(View.GONE);
        binding.textView3.setVisibility(View.GONE);
        binding.textView2.setText("Danh Mục: " + category.getTitle());
        DatabaseReference myRef = database.getReference("Products");
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        ArrayList<Product> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Product product = issue.getValue(Product.class);
                        if (product != null && (category.getId()).equals(product.getCategoryId()))
                            items.add(product);
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewPopular.setLayoutManager(new GridLayoutManager(CustomerMainActivity.this, 2));
                        binding.recyclerViewPopular.setAdapter(new PopularProductsAdapter(items));

                    }
                    binding.progressBarPopular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
