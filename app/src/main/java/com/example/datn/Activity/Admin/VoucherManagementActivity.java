package com.example.datn.Activity.Admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.datn.Activity.SettingsActivity;
import com.example.datn.Adapter.VoucherAdapter;
import com.example.datn.Entity.Voucher;
import com.example.datn.R;
import com.example.datn.databinding.ActivityVoucherManagementBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class VoucherManagementActivity extends AppCompatActivity {

    ActivityVoucherManagementBinding binding;
    private VoucherAdapter adapter;
    private ArrayList<Voucher> voucherList;
    private FirebaseDatabase database;
    private DatabaseReference vouchersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoucherManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        voucherList = new ArrayList<>();
        adapter = new VoucherAdapter(voucherList, new VoucherAdapter.OnVoucherClickListener() {
            @Override
            public void onEditClick(Voucher voucher) {
                showEditVoucherDialog(voucher);
            }

            @Override
            public void onDeleteClick(Voucher voucher) {
                deleteVoucher(voucher);
            }
        });

        binding.rvVoucher.setLayoutManager(new LinearLayoutManager(this));
        binding.rvVoucher.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        vouchersRef = database.getReference("Vouchers");

        loadVouchers();

        binding.btnAddVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở Activity thêm voucher mới
                showAddVoucherDialog();
            }
        });
    }

    private void showAddVoucherDialog() {
        // Create a dialog for adding a voucher
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_voucher, null);
        builder.setView(dialogView);

        // Find the input fields in the dialog
        TextInputEditText edtCode = dialogView.findViewById(R.id.edtNewVoucherCode);
        TextInputEditText edtDiscount = dialogView.findViewById(R.id.edtNewVoucherDiscount);
        TextInputEditText edtValidFrom = dialogView.findViewById(R.id.edtNewVoucherValidFrom);
        TextInputEditText edtValidUntil = dialogView.findViewById(R.id.edtNewVoucherValidUntil);

        // Set date pickers for the "Valid From" and "Valid Until" fields
        edtValidFrom.setOnClickListener(v -> showDatePickerDialog(edtValidFrom));
        edtValidUntil.setOnClickListener(v -> showDatePickerDialog(edtValidUntil));

        // Set up the "Add" button
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            // Retrieve user input
            String code = edtCode.getText().toString().trim();
            int discount = Integer.parseInt(edtDiscount.getText().toString().trim());
            String validFrom = edtValidFrom.getText().toString().trim();
            String validUntil = edtValidUntil.getText().toString().trim();
            if (discount < 0 || discount > 100) {
                Toast.makeText(this, "Discount không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            // Create a new Voucher object
            String voucherId = vouchersRef.push().getKey(); // Generate unique ID using Firebase push key
            Voucher newVoucher = new Voucher(voucherId, code, discount, validFrom, validUntil);

            // Save the new voucher to Firebase
            vouchersRef.child(voucherId).setValue(newVoucher)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            adapter.notifyDataSetChanged(); // Update the list after adding
                        }
                    });
        });

        // Set up the "Cancel" button
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showEditVoucherDialog(Voucher voucher) {
        // Tạo một dialog mới
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_voucher, null);
        builder.setView(dialogView);

        // Ánh xạ các trường trong hộp thoại với layout
        TextInputEditText edtCode = dialogView.findViewById(R.id.edtVoucherCode);
        TextInputEditText edtDiscount = dialogView.findViewById(R.id.edtVoucherDiscount);
        TextInputEditText edtValidFrom = dialogView.findViewById(R.id.edtVoucherValidFrom);
        TextInputEditText edtValidUntil = dialogView.findViewById(R.id.edtVoucherValidUntil);


        // Gán giá trị hiện tại của voucher vào các trường
        edtCode.setText(voucher.getCode());
        edtDiscount.setText(String.valueOf(voucher.getDiscount()));
        edtValidFrom.setText(voucher.getValidFrom());
        edtValidUntil.setText(voucher.getValidUntil());

        // Set onClickListener to open DatePickerDialog for 'Valid From'
        edtValidFrom.setOnClickListener(v -> showDatePickerDialog(edtValidFrom));

        // Set onClickListener to open DatePickerDialog for 'Valid Until'
        edtValidUntil.setOnClickListener(v -> showDatePickerDialog(edtValidUntil));

        // Thiết lập nút "Lưu"
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            // Lấy giá trị mới từ các trường
            String updatedCode = edtCode.getText().toString().trim();
            Integer updatedDiscount = Integer.parseInt(edtDiscount.getText().toString().trim());
            String updatedValidFrom = edtValidFrom.getText().toString().trim();
            String updatedValidUntil = edtValidUntil.getText().toString().trim();
            if (updatedDiscount < 0 || updatedDiscount > 100) {
                Toast.makeText(this, "Discount không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            // Cập nhật giá trị vào voucher
            voucher.setCode(updatedCode);
            voucher.setDiscount(updatedDiscount);
            voucher.setValidFrom(updatedValidFrom);
            voucher.setValidUntil(updatedValidUntil);

            // Cập nhật Firebase Database với thông tin mới
            vouchersRef.child(voucher.getVoucherId()).setValue(voucher)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            adapter.notifyDataSetChanged(); // Cập nhật lại giao diện
                        }
                    });
        });

        // Thiết lập nút "Hủy"
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        // Hiển thị dialog
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to show DatePickerDialog
    private void showDatePickerDialog(TextInputEditText editText) {
        // Lấy ngày hiện tại
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Hiển thị DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            // Cập nhật giá trị ngày được chọn vào TextInputEditText
            String selectedDate = String.format("%02d-%02d-%d", year1, (month1 + 1), dayOfMonth);
            editText.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }


    private void loadVouchers() {
        vouchersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                voucherList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Voucher voucher = snapshot.getValue(Voucher.class);
                    if (voucher != null) {
                        voucherList.add(voucher);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }

    private void deleteVoucher(Voucher voucher) {
        vouchersRef.child(voucher.getVoucherId()).removeValue();
    }
}
