package com.example.datn.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.datn.Entity.Order;
import com.example.datn.R;
import com.example.datn.databinding.ViewholderOrderBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private ArrayList<Order> orderList;
    private Context context;

    private DatabaseReference databaseReference;
    DecimalFormat decimalFormat = new DecimalFormat("#,###,###");



    public OrderHistoryAdapter(ArrayList<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
    }

    @NonNull
    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderOrderBinding binding = ViewholderOrderBinding.inflate(LayoutInflater.from(context),parent,false);
        return new OrderHistoryAdapter.ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull OrderHistoryAdapter.ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.binding.orderId.setText(order.getOrderId());
        holder.binding.orderDate.setText(order.getOrderDate());
        holder.binding.numProducts.setText(String.valueOf(order.getProducts().size()));
        holder.binding.totalAmount.setText(decimalFormat.format(order.getTotalAmount())+" VNĐ");
        holder.binding.status.setText(order.getStatus());
        if (order.getStatus().equals("Đang xử lý") || order.getStatus().equals("Đang vận chuyển")){
            holder.binding.btnCancelOrder.setVisibility(View.VISIBLE);
        }
        holder.binding.btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Change status to "Đã hủy"
                order.setStatus("Đã hủy");
                // Update the status in Firebase Realtime Database
                databaseReference.child(order.getOrderId()).child("status").setValue("Đã hủy")
                        .addOnSuccessListener(aVoid -> {
                            // Update UI
                            holder.binding.status.setText("Đã hủy");
                            holder.binding.btnCancelOrder.setVisibility(View.GONE);
                            Toast.makeText(context, "Đơn hàng đã được hủy", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Toast.makeText(context, "Không thể hủy đơn hàng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });
        OrderProductsAdapter productsAdapter = new OrderProductsAdapter(order.getProducts(), context);
        holder.binding.productInOrderView.setLayoutManager(new LinearLayoutManager(context));
        holder.binding.productInOrderView.setAdapter(productsAdapter);
        holder.binding.btnArrowDown.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @Override
            public void onClick(View view) {
                i++;
                if (i % 2 == 1) {
                    holder.binding.productInOrderView.setVisibility(View.VISIBLE);
                    holder.binding.btnArrowDown.setImageResource(R.drawable.baseline_keyboard_double_arrow_up_24);
                } else {
                    holder.binding.productInOrderView.setVisibility(View.GONE);
                    holder.binding.btnArrowDown.setImageResource(R.drawable.baseline_keyboard_double_arrow_down_24);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderOrderBinding binding;
        public ViewHolder(ViewholderOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
