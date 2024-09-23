package com.example.datn.Activity.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.datn.Activity.CartActivity;
import com.example.datn.Activity.CustomerMainActivity;
import com.example.datn.Adapter.PopularProductsAdapter;
import com.example.datn.Adapter.ProductAdapter;
import com.example.datn.Entity.Product;
import com.example.datn.databinding.ActivityProductManagementBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductManagementActivity extends AppCompatActivity {

    ActivityProductManagementBinding binding;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        binding = ActivityProductManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initProduct();
        findProduct();
        addProduct();

    }

    private void addProduct() {
        binding.btnAddProduct.setOnClickListener(v -> startActivity(new Intent(ProductManagementActivity.this, AddProductActivity.class)));
    }

    private void initProduct() {
        DatabaseReference myRef = database.getReference("Products");
        binding.progressProduct.setVisibility(View.VISIBLE);
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
                        binding.rvProduct.setLayoutManager(new LinearLayoutManager(ProductManagementActivity.this, LinearLayoutManager.VERTICAL, false));
                        // Nếu adapter đã được khởi tạo, chỉ cần cập nhật dữ liệu
                        ProductAdapter adapter = (ProductAdapter) binding.rvProduct.getAdapter();
                        if (adapter == null) {
                            adapter = new ProductAdapter(items, new ProductAdapter.OnItemClickListener() {
                                @Override
                                public void onEditClick(Product product) {
                                    updateProduct(product);
                                }

                                @Override
                                public void onDeleteClick(Product product) {
                                    deleteProduct(product);
                                }
                            });
                            binding.rvProduct.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                binding.progressProduct.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
                binding.progressProduct.setVisibility(View.GONE);
            }
        });
    }

    private void updateProduct(Product product) {
        Intent intent = new Intent(ProductManagementActivity.this, EditProductActivity.class);
        intent.putExtra("product", product); // Truyền sản phẩm sang UpdateProductActivity
        startActivity(intent);
    }

    private void deleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Sản Phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference myRef = database.getReference("Products");
                        myRef.child(product.getProductId()).removeValue();
                        Toast.makeText(ProductManagementActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void findProduct() {
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.edtSearch.getText().toString();
                DatabaseReference myRef = database.getReference("Products");
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
                                binding.rvProduct.setLayoutManager(new LinearLayoutManager(ProductManagementActivity.this, LinearLayoutManager.VERTICAL, false));
                                binding.rvProduct.setAdapter(new ProductAdapter(items, new ProductAdapter.OnItemClickListener() {
                                    @Override
                                    public void onEditClick(Product product) {
                                        updateProduct(product);
                                    }

                                    @Override
                                    public void onDeleteClick(Product product) {
                                        deleteProduct(product);
                                    }
                                }));
                            }
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
}