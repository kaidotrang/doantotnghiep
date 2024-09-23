package com.example.datn.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn.Entity.Voucher;
import com.example.datn.databinding.ItemVoucherBinding;

import java.util.ArrayList;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    private ArrayList<Voucher> voucherList;
    private OnVoucherClickListener listener;

    public interface OnVoucherClickListener {
        void onEditClick(Voucher voucher);

        void onDeleteClick(Voucher voucher);
    }

    public VoucherAdapter(ArrayList<Voucher> voucherList, OnVoucherClickListener listener) {
        this.voucherList = voucherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVoucherBinding binding = ItemVoucherBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VoucherViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        holder.binding.code.setText(voucher.getCode());
        holder.binding.discount.setText(String.valueOf(voucher.getDiscount()) + "%");
        holder.binding.validFrom.setText(voucher.getValidFrom());
        holder.binding.validUntil.setText(voucher.getValidUntil());
        holder.binding.btnEditVoucher.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(voucher);
            }
        });

        // Xử lý sự kiện click cho nút "Delete"
        holder.binding.btnDeleteVoucher.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(voucher);
            }
        });

    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public static class VoucherViewHolder extends RecyclerView.ViewHolder {

        ItemVoucherBinding binding;

        public VoucherViewHolder(@NonNull ItemVoucherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}

