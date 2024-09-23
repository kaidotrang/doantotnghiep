package com.example.datn.Activity.Admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datn.Entity.Category;
import com.example.datn.Entity.Product;
import com.example.datn.databinding.ActivityAddProductBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding binding;
    private List<String> categoryTitles = new ArrayList<>();
    private List<String> categoryIds = new ArrayList<>();
    private DatabaseReference productRef, categoryRef;
    private FirebaseStorage storage;
    private Uri imageUri;
    private FirebaseDatabase database;
    private String productImageUrl = "";
    private String selectedCategoryId = "";

    // Launcher để chọn ảnh từ thư viện

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeFirebase();
        fetchCategories();

        // Nút chọn ảnh từ thư viện
        binding.btnSelectImage.setOnClickListener(v -> selectImageFromGallery());

        // Nút thêm sản phẩm
        binding.btnAddProduct.setOnClickListener(v -> addProduct());
    }
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    binding.imageProduct.setImageURI(imageUri);
                }
            });

    private void initializeFirebase() {
        database = FirebaseDatabase.getInstance();
        productRef = database.getReference("Products");
        categoryRef = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
    }

    private void fetchCategories() {
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitles.clear();
                categoryIds.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        categoryTitles.add(category.getTitle());
                        categoryIds.add(category.getId());  // Save category ID
                    }
                }

                // Cập nhật danh sách danh mục trong Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddProductActivity.this,
                        android.R.layout.simple_spinner_item, categoryTitles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerCategory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Lỗi: " + error.getMessage());
            }
        });

        // Lấy danh mục được chọn từ Spinner
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryId = categoryIds.get(position);  // Get selected category ID
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategoryId = "";  // Default value if nothing selected
            }
        });
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void addProduct() {
        String productName = binding.edtProductName.getText().toString().trim();
        String priceStr = binding.edtPrice.getText().toString().trim();
        String description = binding.edtDescription.getText().toString().trim();
        String stockStr = binding.edtStock.getText().toString().trim();
        String sizesStr = binding.edtSizes.getText().toString().trim();  // Lấy chuỗi kích cỡ
        String colorsStr = binding.edtColors.getText().toString().trim();

        if (isInputValid(productName, priceStr, description, stockStr, sizesStr, colorsStr)) {
            int price = Integer.parseInt(priceStr);
            int stock = Integer.parseInt(stockStr);

            // Chuyển đổi chuỗi kích cỡ và màu sắc thành danh sách các chuỗi
            ArrayList<String> sizes = new ArrayList<>(Arrays.asList(sizesStr.split(",")));
            ArrayList<String> colors = new ArrayList<>(Arrays.asList(colorsStr.split(",")));

            String productId = productRef.push().getKey();
            if (imageUri != null) {
                // Tải ảnh lên Firebase Storage
                uploadImageToFirebase(productId, productName, price, stock, description, sizes, colors);
            } else {
                showToast("Mời chọn ảnh sản phẩm");
            }
        }
    }

    private boolean isInputValid(String productName, String priceStr, String description, String stockStr, String sizesStr, String colorsStr) {
        if (productName.isEmpty() || priceStr.isEmpty() || description.isEmpty() || stockStr.isEmpty() || sizesStr.isEmpty() || colorsStr.isEmpty()) {
            showToast("Mời điền đầy đủ thông tin sản phẩm");
            return false;
        }
        if (selectedCategoryId.isEmpty()) {
            showToast("Mời chọn danh mục sản phẩm");
            return false;
        }
        return true;
    }

    private void uploadImageToFirebase(String productId, String productName, int price, int stock, String description, ArrayList<String> sizes, ArrayList<String> colors) {
        StorageReference storageRef = storage.getReference().child("product_images/" + productId + ".jpg");
        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    productImageUrl = uri.toString();
                    // Sau khi tải ảnh thành công, lưu thông tin sản phẩm lên Firebase
                    saveProductToDatabase(productId, productName, price, stock, description, sizes, colors);
                })
        ).addOnFailureListener(e -> showToast("Tải ảnh thất bại: " + e.getMessage()));
    }

    private void saveProductToDatabase(String productId, String productName, int price, int stock, String description, ArrayList<String> sizes, ArrayList<String> colors) {
        Product product = new Product(productId, productName, selectedCategoryId, price, stock, description, new ArrayList<>());
        product.getPicUrls().add(productImageUrl);
        product.setSizes(sizes);  // Lưu kích cỡ
        product.setColors(colors);  // Lưu màu sắc

        productRef.child(productId).setValue(product).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showToast("Thêm sản phẩm thành công.");
                startActivity(new Intent(AddProductActivity.this, ProductManagementActivity.class));
                finish();
            } else {
                showToast("Thêm sản phẩm thất bại.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
