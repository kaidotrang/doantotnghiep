package com.example.datn.Activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn.Adapter.BannerSliderAdapter;
import com.example.datn.Entity.CartItem;
import com.example.datn.Entity.Product;
import com.example.datn.Entity.Banner;
import com.example.datn.Fragment.DescriptionFragment;
import com.example.datn.Fragment.ReviewFragment;
import com.example.datn.databinding.ActivityProductDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    ActivityProductDetailBinding binding;

    private Product object;

    DecimalFormat decimalFormat = new DecimalFormat("#,###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getBundle();

        initBanners();

        setupViewPager();

    }

    private void initBanners() {
        ArrayList<Banner> sliderItems = new ArrayList<>();
        for (int i = 0; i < object.getPicUrls().size(); i++) {
            sliderItems.add(new Banner(object.getPicUrls().get(i)));
        }

        binding.viewPageSlider.setAdapter(new BannerSliderAdapter(sliderItems, binding.viewPageSlider));
        binding.viewPageSlider.setClipToPadding(false);
        binding.viewPageSlider.setClipChildren(false);
        binding.viewPageSlider.setOffscreenPageLimit(3);
        binding.viewPageSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }

    private void getBundle() {
        object = (Product) getIntent().getSerializableExtra("object");
        binding.titleTxt.setText(object.getProductName());
        binding.priceTxt.setText(decimalFormat.format(object.getPrice()) + " VNĐ");
        binding.ratingBar.setRating((float) object.getAverageRating());
        binding.numberRating.setText(object.getReviewCount() + " Đánh giá");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products").child(object.getProductId());

        // Lấy danh sách size từ Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Xóa tất cả các RadioButton trước khi thêm mới
                binding.radioGroupSizes.removeAllViews();
                binding.radioGroupColors.removeAllViews();

                // Lấy danh sách size từ Firebase
                ArrayList<String> sizes = (ArrayList<String>) dataSnapshot.child("sizes").getValue();
                ArrayList<String> colors = (ArrayList<String>) dataSnapshot.child("colors").getValue();

                // Tạo RadioButton cho từng size
                for (String size : sizes) {
                    RadioButton radioButton = new RadioButton(ProductDetailActivity.this);
                    binding.radioGroupColors.setGravity(Gravity.LEFT);
                    binding.radioGroupSizes.setWeightSum(sizes.size());
                    radioButton.setText(size);
                    radioButton.setId(View.generateViewId()); // Tạo ID cho mỗi RadioButton
                    // Thêm RadioButton vào RadioGroup
                    binding.radioGroupSizes.addView(radioButton);
                }

                for (String color : colors) {
                    RadioButton radioButton = new RadioButton(ProductDetailActivity.this);
                    binding.radioGroupColors.setGravity(Gravity.LEFT);
                    binding.radioGroupColors.setWeightSum(colors.size());
                    radioButton.setText(color);
                    radioButton.setId(View.generateViewId()); // Tạo ID cho mỗi RadioButton
                    // Thêm RadioButton vào RadioGroup
                    binding.radioGroupColors.addView(radioButton);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });

        binding.addToCarBtn.setOnClickListener(view -> {
            int selectedColorId = binding.radioGroupColors.getCheckedRadioButtonId();
            int selectedSizeId = binding.radioGroupSizes.getCheckedRadioButtonId();
            if (selectedSizeId == -1 || selectedColorId == -1) {
                // Nếu không có kích thước nào được chọn
                Toast.makeText(ProductDetailActivity.this, "Mời chọn size và màu sắc", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy kích thước sản phẩm đã chọn
            String selectedSize = ((RadioButton) findViewById(selectedSizeId)).getText().toString();
            String selectedColor = ((RadioButton) findViewById(selectedColorId)).getText().toString();


            // Cập nhật giỏ hàng lên Firebase
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Users");
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userCartRef = cartRef.child(userId).child("cart");
            String cartId = userCartRef.push().getKey();

            // Tạo đối tượng CartItem
            CartItem cartItem = new CartItem(
                    cartId,
                    object.getProductId(),
                    object.getProductName(),
                    object.getPrice(),
                    selectedSize,
                    selectedColor,
                    1 // Số lượng mặc định là 1
            );

            // Kiểm tra nếu sản phẩm đã có trong giỏ hàng

            // Tạo một truy vấn để tìm các sản phẩm trong giỏ hàng có cùng productId, size, và color
            userCartRef.orderByChild("productId").equalTo(cartItem.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean updated = false;
                    if (dataSnapshot.exists()) {
                        // Nếu có các mục trong giỏ hàng
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CartItem existingItem = snapshot.getValue(CartItem.class);
                            if (existingItem != null) {
                                // Kiểm tra xem item có cùng size và color không
                                if (existingItem.getSize().equals(cartItem.getSize()) && existingItem.getColor().equals(cartItem.getColor())) {
                                    // Nếu cùng size và color, cập nhật số lượng
                                    int newQuantity = existingItem.getQuantity() + 1;
                                    snapshot.getRef().child("quantity").setValue(newQuantity);
                                    Toast.makeText(ProductDetailActivity.this, "Đã cập nhật giỏ hàng", Toast.LENGTH_SHORT).show();
                                    updated = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!updated) {
                        // Nếu không tìm thấy sản phẩm cùng size và color, thêm mới vào giỏ hàng
                        userCartRef.child(cartId).setValue(cartItem).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProductDetailActivity.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProductDetailActivity.this, "Thêm vào giỏ hàng không thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ProductDetailActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        binding.backBtn.setOnClickListener(view -> finish());
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        DescriptionFragment tab1 = new DescriptionFragment();
        ReviewFragment tab2 = new ReviewFragment();

        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();

        bundle1.putString("description", object.getDescription());
        bundle2.putSerializable("reviews", object.getReviews());
        bundle2.putString("productId", object.getProductId()); // Đảm bảo productId không null
        tab1.setArguments(bundle1);
        tab2.setArguments(bundle2);

        adapter.addFrag(tab1, "Mô tả");
        adapter.addFrag(tab2, "Đánh giá");

        binding.viewpager.setAdapter(adapter);
        binding.tableLayout.setupWithViewPager(binding.viewpager);
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}