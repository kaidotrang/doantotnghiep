package com.example.datn.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.datn.Entity.User;
import com.example.datn.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SettingsActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    ActivitySettingsBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference storageRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        loadUserData();
        writeData();

        binding.imgProfile.setOnClickListener(view -> openGallery());

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
                updatePassword();

            }
        });

        binding.btnLogout.setOnClickListener(v -> logout());
    }

    private void updateUserInfo() {
        String name = binding.edtName.getText().toString();
        String phone = binding.edtPhone.getText().toString();
        String address = binding.edtAddress.getText().toString();

        if (!name.isEmpty()) {
            userRef.child("name").setValue(name);
        }
        if (!phone.isEmpty()) {
            userRef.child("phone").setValue(phone);
        }
        if (!address.isEmpty()) {
            userRef.child("address").setValue(address);
        }

        if(binding.edtName.isEnabled() || binding.edtPhone.isEnabled() || binding.edtAddress.isEnabled()) {
            Toast.makeText(SettingsActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
        }

        if (imageUri != null) {
            uploadProfileImage();
        }

        binding.edtName.setEnabled(false);
        binding.edtPhone.setEnabled(false);
        binding.edtAddress.setEnabled(false);
    }

    private void updatePassword() {
        String password = binding.edtPassword.getText().toString();
        if (!password.isEmpty() && binding.edtPassword.isEnabled()) {
            mAuth.getCurrentUser().updatePassword(password).addOnCompleteListener(passTask -> {
                if (passTask.isSuccessful()) {
                    userRef.child("password").setValue(password);
                    Toast.makeText(SettingsActivity.this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    binding.edtPassword.setEnabled(false);
                } else {
                    Toast.makeText(SettingsActivity.this, "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.imgProfile.setImageURI(imageUri);
        }
    }

    private void uploadProfileImage() {
        if (imageUri != null) {
            String fileName = UUID.randomUUID().toString();
            StorageReference fileRef = storageRef.child(fileName);

            fileRef.putFile(imageUri).addOnCompleteListener(uploadTask -> {
                if (uploadTask.isSuccessful()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        userRef.child("imgProfile").setValue(imageUrl);
                        Toast.makeText(SettingsActivity.this, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Toast.makeText(SettingsActivity.this, "Tải ảnh lên thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        Glide.with(SettingsActivity.this)
                                .load(user.getImgProfile())
                                .apply(RequestOptions.circleCropTransform())
                                .into(binding.imgProfile);
                        binding.edtName.setText(user.getName());
                        binding.edtEmail.setText(user.getEmail());
                        binding.edtPassword.setText(user.getPassword());
                        binding.edtPhone.setText(user.getPhone());
                        binding.edtAddress.setText(user.getAddress());

                        binding.edtName.setEnabled(false);
                        binding.edtEmail.setEnabled(false);
                        binding.edtPassword.setEnabled(false);
                        binding.edtPhone.setEnabled(false);
                        binding.edtAddress.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, "Tải dữ liệu thất bại.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void writeData() {
        binding.btnChangeName.setOnClickListener(view -> binding.edtName.setEnabled(true));
        binding.btnChangePassword.setOnClickListener(view -> binding.edtPassword.setEnabled(true));
        binding.btnChangePhone.setOnClickListener(view -> binding.edtPhone.setEnabled(true));
        binding.btnChangeAddress.setOnClickListener(view -> binding.edtAddress.setEnabled(true));
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
        finish();
    }
}
