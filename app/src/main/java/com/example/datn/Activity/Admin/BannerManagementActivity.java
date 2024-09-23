package com.example.datn.Activity.Admin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.datn.Adapter.BannerSliderAdapter;
import com.example.datn.Entity.Banner;
import com.example.datn.databinding.ActivityBannerManagementBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BannerManagementActivity extends AppCompatActivity {
    ActivityBannerManagementBinding binding;

    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBannerManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initBanner();
        binding.btnAddBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1); // Mã yêu cầu là 1
            }
        });

    }

    private void initBanner() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Banner");
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            // Tạo tham chiếu đến Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("banners/" + System.currentTimeMillis() + ".jpg");

            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Lấy URL của ảnh đã upload và lưu vào Firebase Realtime Database
                    String imageUrl = uri.toString();
                    saveBannerToDatabase(imageUrl);
                });
            }).addOnFailureListener(e -> {
                // Xử lý lỗi khi upload ảnh
            });
        }
    }

    private void saveBannerToDatabase(String imageUrl) {
        DatabaseReference bannerRef = database.getReference("Banner");
        String bannerId = bannerRef.push().getKey(); // Tạo một id duy nhất cho banner mới
        Banner newBanner = new Banner(imageUrl); // Tạo đối tượng Banner với URL ảnh
        if (bannerId != null) {
            bannerRef.child(bannerId).setValue(newBanner).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(BannerManagementActivity.this, "Thêm banner thành công", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý lỗi khi lưu banner
                }
            });
        }
    }



}
