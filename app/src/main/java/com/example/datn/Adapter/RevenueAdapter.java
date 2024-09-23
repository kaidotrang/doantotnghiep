package com.example.datn.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn.Entity.Order;
import com.example.datn.databinding.ItemRevenueBinding;
import java.util.ArrayList;

public class RevenueAdapter extends RecyclerView.Adapter<RevenueAdapter.RevenueViewHolder> {

    private ArrayList<Order> orderList;

    public RevenueAdapter(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public RevenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRevenueBinding binding = ItemRevenueBinding.inflate(inflater, parent, false);
        return new RevenueViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RevenueViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.binding.orderId.setText("Order ID: " + order.getOrderId());
        holder.binding.totalAmount.setText("Total Amount: " + order.getTotalAmount());
        holder.binding.orderDate.setText("Order Date: " + order.getOrderDate());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class RevenueViewHolder extends RecyclerView.ViewHolder {

        private ItemRevenueBinding binding;

        public RevenueViewHolder(@NonNull ItemRevenueBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
