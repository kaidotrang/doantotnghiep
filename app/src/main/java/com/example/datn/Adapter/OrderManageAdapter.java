package com.example.datn.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn.Entity.Order;
import com.example.datn.databinding.OrderManageItemBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderManageAdapter extends RecyclerView.Adapter<OrderManageAdapter.OrderManageViewHolder> {

    private ArrayList<Order> orderList;
    private DatabaseReference ordersRef;
    private static final String[] STATUS_OPTIONS = {"Đang xử lý", "Đang vận chuyển", "Hoàn tất", "Đã hủy"};
    private DecimalFormat decimalFormat = new DecimalFormat("#,###,###");

    public OrderManageAdapter(ArrayList<Order> orderList) {
        this.orderList = orderList;
        // Khởi tạo Firebase Database Reference
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
    }

    @NonNull
    @Override
    public OrderManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        OrderManageItemBinding binding = OrderManageItemBinding.inflate(inflater, parent, false);
        return new OrderManageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderManageViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.binding.orderId.setText(order.getOrderId());
        holder.binding.orderDate.setText(order.getOrderDate());
        holder.binding.name.setText(order.getName());
        holder.binding.phone.setText(order.getPhone());
        holder.binding.address.setText(order.getAddress());
        holder.binding.numProducts.setText(String.valueOf(order.getProducts().size()));
        holder.binding.totalAmount.setText(decimalFormat.format(order.getTotalAmount()) + " VNĐ");

        // Tạo ArrayAdapter cho Spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                holder.itemView.getContext(),
                android.R.layout.simple_spinner_item,
                STATUS_OPTIONS
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gán ArrayAdapter cho Spinner
        holder.binding.spinnerStatus.setAdapter(statusAdapter);

        // Thiết lập giá trị hiện tại của Spinner dựa trên trạng thái của đơn hàng
        int statusIndex = getStatusIndex(order.getStatus());
        holder.binding.spinnerStatus.setSelection(statusIndex);

        // Kiểm tra trạng thái của đơn hàng và kích hoạt/khóa Spinner tương ứng
        boolean isDisabled = "Đã hủy".equals(order.getStatus()) || "Hoàn tất".equals(order.getStatus());
        holder.binding.spinnerStatus.setEnabled(!isDisabled);
        // Xử lý sự kiện chọn item trong Spinner
        holder.binding.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isDisabled) {
                    String selectedStatus = STATUS_OPTIONS[position];
                    updateOrderStatus(order.getOrderId(), selectedStatus);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý khi không có mục nào được chọn
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private int getStatusIndex(String status) {
        switch (status) {
            case "Đang xử lý":
                return 0;
            case "Đang vận chuyển":
                return 1;
            case "Hoàn tất":
                return 2;
            case "Đã hủy":
                return 3;
            default:
                return -1; // Hoặc giá trị mặc định khác nếu trạng thái không hợp lệ
        }
    }

    private void updateOrderStatus(String orderId, String newStatus) {
        ordersRef.child(orderId).child("status").setValue(newStatus)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Cập nhật thành công
                    } else {
                        // Xử lý lỗi
                    }
                });
    }

    public static class OrderManageViewHolder extends RecyclerView.ViewHolder {
        OrderManageItemBinding binding;

        public OrderManageViewHolder(@NonNull OrderManageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


