package com.example.datn.Activity.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.datn.Adapter.AdminCategoryAdapter;
import com.example.datn.Entity.Category;
import com.example.datn.databinding.ActivityCategoryManagementBinding;
import com.example.datn.databinding.DialogCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryManagementActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private DialogCategoryBinding currentDialogBinding;

    private RecyclerView recyclerView;
    private AdminCategoryAdapter categoryAdapter;
    private ArrayList<Category> categoryList;
    private DatabaseReference categoryRef;
    private ActivityCategoryManagementBinding binding;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCategoryManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.recyclerViewCategory;
        progressBar = binding.progressBar;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        categoryRef = database.getReference("Category");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();
        categoryAdapter = new AdminCategoryAdapter(categoryList, new AdminCategoryAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Category category) {
                showEditCategoryDialog(category);
            }

            @Override
            public void onDeleteClick(Category category) {
                deleteCategory(category);
            }
        });
        recyclerView.setAdapter(categoryAdapter);

        binding.btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCategoryDialog();
            }
        });

        loadCategories();
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    categoryList.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Update ImageView in the current dialog
            if (currentDialogBinding != null) {
                currentDialogBinding.imageViewCategory.setImageURI(imageUri);
            }
        }
    }

    private void uploadImageToFirebaseStorage(final String categoryId) {
        if (imageUri != null) {
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference("category_images/" + categoryId);
            UploadTask uploadTask = storageReference.putFile(imageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            updateCategoryImageUrl(categoryId, uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle unsuccessful uploads
                }
            });
        }
    }

    private void updateCategoryImageUrl(String categoryId, String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("picUrl", imageUrl);
        categoryRef.child(categoryId).updateChildren(updates);
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Danh Mục");

        currentDialogBinding = DialogCategoryBinding.inflate(LayoutInflater.from(this));
        builder.setView(currentDialogBinding.getRoot());

        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = currentDialogBinding.editTextTitle.getText().toString();

                if (!title.isEmpty() && imageUri != null) {
                    String id = categoryRef.push().getKey();
                    uploadImageToFirebaseStorage(id);
                    Category category = new Category(id, "", title);
                    categoryRef.child(id).setValue(category);
                } else {
                    Toast.makeText(CategoryManagementActivity.this, "Mời nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", null);
        currentDialogBinding.btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
        builder.show();
    }

    private void showEditCategoryDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Danh Mục");

        currentDialogBinding = DialogCategoryBinding.inflate(LayoutInflater.from(this));
        currentDialogBinding.editTextTitle.setText(category.getTitle());
        Glide.with(this).load(category.getPicUrl()).into(currentDialogBinding.imageViewCategory);
        builder.setView(currentDialogBinding.getRoot());

        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = currentDialogBinding.editTextTitle.getText().toString();

                if (!title.isEmpty()) {
                    if (imageUri != null) {
                        uploadImageToFirebaseStorage(category.getId());
                    } else {
                        updateCategoryImageUrl(category.getId(), category.getPicUrl());
                    }
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("title", title);
                    categoryRef.child(category.getId()).updateChildren(updates);
                }
            }
        });

        builder.setNegativeButton("Hủy", null);
        currentDialogBinding.btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
        builder.show();
    }

    private void deleteCategory(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Danh Mục")
                .setMessage("Bạn có chắc chắn muốn xóa danh mục này?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryRef.child(category.getId()).removeValue();
                        Toast.makeText(CategoryManagementActivity.this, "Xóa danh mục thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
