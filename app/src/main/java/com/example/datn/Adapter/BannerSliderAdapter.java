package com.example.datn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.datn.Entity.Banner;
import com.example.datn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BannerSliderAdapter extends RecyclerView.Adapter<BannerSliderAdapter.SliderViewholder> {
    private ArrayList<Banner> sliderItems;
    private ViewPager2 viewPager2;
    private Context context;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };

    public BannerSliderAdapter(ArrayList<Banner> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public BannerSliderAdapter.SliderViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SliderViewholder(LayoutInflater.from(context).inflate(R.layout.banner_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BannerSliderAdapter.SliderViewholder holder, int position) {
        holder.setImage(sliderItems.get(position));

        if (position == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }

        // Lấy role của user từ Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.child("role").getValue(String.class);

                    // Chỉ cho phép admin thực hiện long click
                    if ("admin".equals(role)) {
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                // Thực hiện hành động long click cho admin
                                int position = holder.getAdapterPosition();
                                if (position == RecyclerView.NO_POSITION || sliderItems.isEmpty()) {
                                    return false;
                                }

                                String bannerUrl = sliderItems.get(position).getUrl();
                                DatabaseReference bannerRef = FirebaseDatabase.getInstance().getReference("Banner");
                                bannerRef.orderByChild("url").equalTo(bannerUrl).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        if (position != RecyclerView.NO_POSITION && position < sliderItems.size() && !sliderItems.isEmpty()) {
                                                            sliderItems.remove(position);
                                                            notifyItemRemoved(position);
                                                            notifyItemRangeChanged(position, sliderItems.size());
                                                        }
                                                        Toast.makeText(context, "Banner đã được xóa!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(context, "Xóa banner thất bại.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(context, "Có lỗi xảy ra: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                return true;
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Có lỗi xảy ra: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    public class SliderViewholder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public SliderViewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImage);

        }

        void setImage(Banner sliderItems) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop());
            Glide.with(context)
                    .load(sliderItems.getUrl())
                    .apply(requestOptions)
                    .into(imageView);
        }
    }
}
