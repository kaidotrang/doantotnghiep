package com.example.datn.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn.Adapter.ReviewAdapter;
import com.example.datn.Entity.Review;
import com.example.datn.Entity.Banner;
import com.example.datn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Queue;

//public class ReviewFragment extends Fragment {
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_review, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initList(view);
//    }
//
////    private void initList(View view) {
////        FirebaseDatabase database = FirebaseDatabase.getInstance();
////        DatabaseReference myRef = database.getReference("Review");
////        ArrayList<Review> list = new ArrayList<>();
////        Query query = myRef.orderByChild("ItemId").equalTo(4);
////
////        query.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                if (snapshot.exists()) {
////                    for (DataSnapshot issue : snapshot.getChildren()) {
////                        list.add(issue.getValue(Review.class));
////                    }
////                }
////                RecyclerView descTxt = view.findViewById(R.id.reviewView);
////                if(list.size()>0) {
////                    descTxt.setAdapter(new ReviewAdapter(list));
////                    descTxt.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
////
////                }
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////
////            }
////        });
////
////    }
//private void initList(View view) {
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    // Truy cập vào node "Products"
//    DatabaseReference myRef = database.getReference("Products");
//    ArrayList<Review> list = new ArrayList<>();
//    // Truy vấn để lấy sản phẩm có productId là 4
//    Query query = myRef.orderByChild("productName").equalTo("Product A"); // Giả sử "Product A" có id = 4
//
//    query.addListenerForSingleValueEvent(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot snapshot) {
//            if (snapshot.exists()) {
//                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
//                    // Lấy danh sách reviews từ sản phẩm
//                    DataSnapshot reviewsSnapshot = productSnapshot.child("reviews");
//                    for (DataSnapshot reviewSnapshot : reviewsSnapshot.getChildren()) {
//                        // Lấy từng review và add vào list
//                        Review review = new Review();
//                        review.setComment(reviewSnapshot.child("comment").getValue(String.class));
//                        review.setRating(reviewSnapshot.child("rating").getValue(Float.class));
//                        list.add(review);
//                    }
//                }
//            }
//
//            // Set adapter cho RecyclerView
//            RecyclerView descTxt = view.findViewById(R.id.reviewView);
//            if (list.size() > 0) {
//                descTxt.setAdapter(new ReviewAdapter(list));
//                descTxt.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//            }
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//            // Xử lý lỗi
//        }
//    });
//}
//
//}

public class ReviewFragment extends Fragment {
    private String productId; // Thay đổi ở đây

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    //    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Bundle bundle = getArguments();
//        ArrayList<Review> reviews = new ArrayList<>();
//        reviews = (ArrayList<Review>) bundle.getSerializable("reviews");
//        RecyclerView descTxt = view.findViewById(R.id.reviewView);
//        if (reviews.size() > 0) {
//            descTxt.setAdapter(new ReviewAdapter(reviews));
//            descTxt.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        }
//
//    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<Review> reviews = (ArrayList<Review>) bundle.getSerializable("reviews");
            if (reviews != null) {
                if (reviews.isEmpty()) {
                    // Handle the case where reviews list is empty
                    Log.d("ReviewFragment", "Reviews list is empty");
                    // Example: show a message to the user or a placeholder view
                    showEmptyState(view);
                } else {
                    // Proceed with initialization
                    initList(view, reviews);
                }
            } else {
                // Handle the case where reviews list is null
                Log.e("ReviewFragment", "Reviews list is null");
                showEmptyState(view);
            }
        } else {
            // Handle the case where Bundle is null
            Log.e("ReviewFragment", "Bundle is null");
        }
    }

    private void showEmptyState(View view) {
        // Example implementation to show a message or placeholder
        TextView emptyMessage = view.findViewById(R.id.empty_message);
        emptyMessage.setVisibility(View.VISIBLE);
        emptyMessage.setText("Sản pẩm chưa có đánh giá");
    }

    private void initList(View view, ArrayList<Review> reviews) {
        RecyclerView descTxt = view.findViewById(R.id.reviewView);
        if (reviews.size() > 0) {
            descTxt.setAdapter(new ReviewAdapter(reviews));
            descTxt.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        }
    }


}
