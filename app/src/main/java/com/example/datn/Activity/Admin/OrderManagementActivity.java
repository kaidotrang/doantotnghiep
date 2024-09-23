package com.example.datn.Activity.Admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.datn.Adapter.OrderManageAdapter;
import com.example.datn.Entity.Order;
import com.example.datn.databinding.ActivityOrderManagementBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderManagementActivity extends AppCompatActivity {

    ActivityOrderManagementBinding binding;
    private OrderManageAdapter orderAdapter;
    private ArrayList<Order> orderList = new ArrayList<>();
    private DatabaseReference ordersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this));
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        orderAdapter = new OrderManageAdapter(orderList);
        binding.rvOrders.setAdapter(orderAdapter);
        loadOrders();
    }

    private void loadOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

}