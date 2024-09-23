package com.example.datn.Activity.Admin;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.datn.databinding.ActivityRevenueStatisticsBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RevenueStatisticsActivity extends AppCompatActivity {

    private ActivityRevenueStatisticsBinding binding;
    private DatabaseReference ordersRef;
    private SimpleDateFormat dateFormat;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sử dụng View Binding
        binding = ActivityRevenueStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        barChart = binding.barChart;

        binding.etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(binding.etStartDate);
            }
        });

        binding.etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(binding.etEndDate);
            }
        });

        binding.btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateRevenue();
            }
        });
    }

    private void showDatePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RevenueStatisticsActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        editText.setText(dateFormat.format(selectedDate.getTime()).split(" ")[0]); // Chỉ chọn ngày, không bao gồm giờ
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void calculateRevenue() {
        String startDateStr = binding.etStartDate.getText().toString();
        String endDateStr = binding.etEndDate.getText().toString();
        binding.barChart.setVisibility(View.VISIBLE);

        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            // Thông báo lỗi nếu ngày bắt đầu hoặc ngày kết thúc không được nhập
            binding.tvOrderCount.setText("Số đơn hàng: 0");
            binding.tvRevenue.setText("Doanh thu: 0 VND");
            return;
        }

        try {
            // Thêm giờ phút giây vào ngày kết thúc để tính hết ngày
            String endDateStrWithTime = endDateStr + " 23:59:59";
            Date startDate = dateFormat.parse(startDateStr + " 00:00:00");
            Date endDate = dateFormat.parse(endDateStrWithTime);

            if (startDate == null || endDate == null || startDate.after(endDate)) {
                // Thông báo lỗi nếu ngày bắt đầu lớn hơn ngày kết thúc hoặc không hợp lệ
                binding.tvOrderCount.setText("Số đơn hàng: 0");
                binding.tvRevenue.setText("Doanh thu: 0 VND");
                return;
            }

            // Truy vấn dữ liệu từ Firebase Realtime Database
            ordersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int[] statusCounts = new int[4]; // 0: Đang xử lý, 1: Đang vận chuyển, 2: Hoàn tất, 3: Đã hủy
                    double totalRevenue = 0.0;

                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        String orderDateStr = orderSnapshot.child("orderDate").getValue(String.class);
                        String status = orderSnapshot.child("status").getValue(String.class);
                        Double totalAmount = orderSnapshot.child("totalAmount").getValue(Double.class);

                        if (orderDateStr != null) {
                            try {
                                Date orderDate = dateFormat.parse(orderDateStr);

                                if (orderDate != null && !orderDate.before(startDate) && !orderDate.after(endDate)) {
                                    if ("Hoàn tất".equals(status)) {
                                        totalRevenue += totalAmount != null ? totalAmount : 0;
                                    }

                                    // Tăng số lượng đơn hàng theo trạng thái
                                    switch (status) {
                                        case "Đang xử lý":
                                            statusCounts[0]++;
                                            break;
                                        case "Đang vận chuyển":
                                            statusCounts[1]++;
                                            break;
                                        case "Hoàn tất":
                                            statusCounts[2]++;
                                            break;
                                        case "Đã hủy":
                                            statusCounts[3]++;
                                            break;
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // Cập nhật số lượng đơn hàng và doanh thu
                    binding.tvOrderCount.setText("Số đơn hàng hoàn tất: " + statusCounts[2]); // Chỉ hiển thị số đơn hàng hoàn tất
                    binding.tvRevenue.setText("Doanh thu: " + decimalFormat.format(totalRevenue) + " VNĐ");

                    // Hiển thị biểu đồ cột
                    showBarChart(statusCounts);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý lỗi khi truy vấn dữ liệu
                    binding.tvOrderCount.setText("Số đơn hàng: 0");
                    binding.tvRevenue.setText("Doanh thu: 0 VND");
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
            binding.tvOrderCount.setText("Số đơn hàng: 0");
            binding.tvRevenue.setText("Doanh thu: 0 VND");
        }
    }

    private void showBarChart(int[] statusCounts) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, statusCounts[0]));
        entries.add(new BarEntry(1f, statusCounts[1]));
        entries.add(new BarEntry(2f, statusCounts[2]));
        entries.add(new BarEntry(3f, statusCounts[3]));

        BarDataSet dataSet = new BarDataSet(entries, "Số lượng đơn hàng");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(16f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(4);

        // Thiết lập các nhãn cho trục X
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                switch ((int) value) {
                    case 0:
                        return "Đang xử lý";
                    case 1:
                        return "Đang vận chuyển";
                    case 2:
                        return "Hoàn tất";
                    case 3:
                        return "Đã hủy";
                    default:
                        return "";
                }
            }
        });

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);

        barChart.getAxisRight().setEnabled(false); // Vô hiệu hóa trục Y phải
        barChart.getDescription().setEnabled(false); // Tắt mô tả biểu đồ

        barChart.invalidate(); // Refresh biểu đồ
    }
}




