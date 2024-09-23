package com.example.datn.Activity.Admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.datn.Entity.Product;
import com.example.datn.databinding.ActivityEditProductBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

public class EditProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Code yêu cầu chọn ảnh
    private ActivityEditProductBinding binding;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private Product product; // Sản phẩm cần cập nhật
    private String categoryName; // Tên danh mục
    private Uri imageUri; // URI của ảnh đã chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Nhận thông tin sản phẩm từ intent
        product = (Product) getIntent().getSerializableExtra("product");

        // Hiển thị thông tin hiện tại của sản phẩm
        binding.edtProductName.setText(product.getProductName());
        binding.edtDescription.setText(product.getDescription());
        binding.edtStock.setText(String.valueOf(product.getStock()));
        binding.edtPrice.setText(String.valueOf(product.getPrice()));
        binding.edtSizes.setText(String.join(",", product.getSizes()));
        binding.edtColors.setText(String.join(",", product.getColors()));

        Glide.with(EditProductActivity.this)
                .load(product.getPicUrls().get(0)) // Đặt URL hình ảnh từ sản phẩm
                .into(binding.imageProduct);

        // Truy xuất tên danh mục dựa trên categoryId của sản phẩm
        fetchCategoryName(product.getCategoryId());

        // Khi người dùng nhấn vào hình ảnh
        binding.btnSelectImage.setOnClickListener(v -> openImageChooser());

        // Khi người dùng nhấn nút Cập Nhật
        binding.btnUpdateProduct.setOnClickListener(v -> updateProductInFirebase());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.imageProduct.setImageURI(imageUri);
        }
    }

    private void fetchCategoryName(String categoryId) {
        DatabaseReference categoryRef = database.getReference("Category").child(categoryId);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    categoryName = snapshot.child("title").getValue(String.class);
                    binding.txtCategory.setText(categoryName); // Hiển thị tên danh mục
                } else {
                    Toast.makeText(EditProductActivity.this, "Không tìm thấy danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProductActivity.this, "Lỗi khi lấy tên danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductInFirebase() {
        String updatedName = binding.edtProductName.getText().toString();
        String updatedDescription = binding.edtDescription.getText().toString();
        Integer updatedPrice = Integer.parseInt(binding.edtPrice.getText().toString());
        Integer updatedStock = Integer.parseInt(binding.edtStock.getText().toString());
        ArrayList<String> updatedSizes = new ArrayList<>(Arrays.asList(binding.edtSizes.getText().toString().split(",")));
        ArrayList<String> updatedColors = new ArrayList<>(Arrays.asList(binding.edtColors.getText().toString().split(",")));

        // Cập nhật thông tin sản phẩm
        product.setProductName(updatedName);
        product.setPrice(updatedPrice);
        product.setDescription(updatedDescription);
        product.setStock(updatedStock);
        product.setSizes(updatedSizes);
        product.setColors(updatedColors);

        // Nếu có ảnh mới được chọn, tải lên Firebase Storage và cập nhật URL
        if (imageUri != null) {
            uploadImageToFirebase();
        } else {
            // Nếu không có ảnh mới, chỉ cập nhật thông tin sản phẩm
            updateProductInDatabase(product);
        }
    }

    private void uploadImageToFirebase() {
        StorageReference imageRef = storage.getReference("product_images/" + product.getProductId() + ".jpg");

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                ArrayList<String> picUrls = new ArrayList<>();
                picUrls.add(uri.toString()); // Cập nhật URL của ảnh

                product.setPicUrls(picUrls);
                updateProductInDatabase(product);
            }).addOnFailureListener(exception -> {
                Toast.makeText(EditProductActivity.this, "Lấy URL ảnh thất bại", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(exception -> {
            Toast.makeText(EditProductActivity.this, "Tải ảnh lên thất bại", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProductInDatabase(Product product) {
        DatabaseReference productRef = database.getReference("Products").child(product.getProductId());
        productRef.setValue(product).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditProductActivity.this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                finish(); // Đóng activity sau khi cập nhật thành công
            } else {
                Toast.makeText(EditProductActivity.this, "Cập nhật sản phẩm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
