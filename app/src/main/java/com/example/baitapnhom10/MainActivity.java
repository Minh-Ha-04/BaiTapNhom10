package com.example.nhatro;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nhatro.controller.PhongController;
import com.example.nhatro.model.Phong;
import com.example.nhatro.view.PhongAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhongAdapter adapter;
    private PhongController controller;

    private EditText etTimKiem;
    private TextView tvThongKe, tvSoPhongTrong, tvSoPhongThue;
    private TextView tvFilterTinhTrang, tvFilterGia;
    private LinearLayout btnFilterTinhTrang, btnFilterGia;
    private ExtendedFloatingActionButton fabThem;

    private String currentTinhTrang = "tat_ca";
    private String currentSort      = "mac_dinh";
    private String currentKeyword   = "";

    public static PhongController sharedController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (sharedController == null) {
            sharedController = new PhongController();
        }
        controller = sharedController;

        // Ánh xạ
        recyclerView        = findViewById(R.id.recyclerView);
        etTimKiem           = findViewById(R.id.etTimKiem);
        tvThongKe           = findViewById(R.id.tvThongKe);
        tvSoPhongTrong      = findViewById(R.id.tvSoPhongTrong);
        tvSoPhongThue       = findViewById(R.id.tvSoPhongThue);
        fabThem             = findViewById(R.id.fabThem);
        tvFilterTinhTrang   = findViewById(R.id.tvFilterTinhTrang);
        tvFilterGia         = findViewById(R.id.tvFilterGia);
        btnFilterTinhTrang  = findViewById(R.id.btnFilterTinhTrang);
        btnFilterGia        = findViewById(R.id.btnFilterGia);

        // RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PhongAdapter(this, controller.getDanhSachPhong(),
                new PhongAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        List<Phong> ds = controller.locVaSapXep(
                                currentKeyword, currentTinhTrang, currentSort);
                        int realIndex = controller.getDanhSachPhong().indexOf(ds.get(position));
                        Intent intent = new Intent(MainActivity.this, SuaPhongActivity.class);
                        intent.putExtra("position", realIndex);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(int position) {
                        hienThiDialogXoa(position);
                    }

                    @Override
                    public void onDeleteClick(int position) {
                        hienThiDialogXoa(position);
                    }
                });
        recyclerView.setAdapter(adapter);

        // FAB
        fabThem.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ThemPhongActivity.class)));

        // Tìm kiếm
        etTimKiem.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s.toString();
                apDungLocVaSapXep();
            }
        });

        // Dropdown tình trạng
        btnFilterTinhTrang.setOnClickListener(v -> showPopupTinhTrang(v));

        // Dropdown giá
        btnFilterGia.setOnClickListener(v -> showPopupGia(v));

        apDungLocVaSapXep();
        capNhatThongKe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        apDungLocVaSapXep();
        capNhatThongKe();
    }

    // ===== POPUP TÌNH TRẠNG =====
    private void showPopupTinhTrang(View anchor) {
        String[] labels = {"🏠  Tất cả", "🟢  Còn trống", "🔴  Đã thuê"};
        String[] values = {"tat_ca", "con_trong", "da_thue"};
        showPopup(anchor, labels, values, currentTinhTrang, (value, label) -> {
            currentTinhTrang = value;
            // Bỏ icon prefix khi hiển thị trên nút
            tvFilterTinhTrang.setText(label.replace("🏠  ", "")
                    .replace("🟢  ", "").replace("🔴  ", ""));
            apDungLocVaSapXep();
        });
    }

    // ===== POPUP GIÁ =====
    private void showPopupGia(View anchor) {
        String[] labels = {"💰  Mặc định", "↑  Thấp → Cao", "↓  Cao → Thấp"};
        String[] values = {"mac_dinh", "tang_dan", "giam_dan"};
        showPopup(anchor, labels, values, currentSort, (value, label) -> {
            currentSort = value;
            tvFilterGia.setText(label.replace("💰  ", "")
                    .replace("↑  ", "↑ ").replace("↓  ", "↓ "));
            apDungLocVaSapXep();
        });
    }

    // ===== HÀM CHUNG TẠO POPUP =====
    interface OnPopupItemSelected {
        void onSelected(String value, String label);
    }

    private void showPopup(View anchor, String[] labels, String[] values,
                           String currentValue, OnPopupItemSelected callback) {

        // Tạo layout popup
        LinearLayout popupLayout = new LinearLayout(this);
        popupLayout.setOrientation(LinearLayout.VERTICAL);
        popupLayout.setBackgroundResource(R.drawable.bg_popup);
        popupLayout.setPadding(0, 8, 0, 8);

        // Tạo PopupWindow
        PopupWindow popup = new PopupWindow(popupLayout,
                anchor.getWidth() + 32,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        popup.setElevation(16f);
        popup.setOutsideTouchable(true);

        // Thêm từng item vào popup
        for (int i = 0; i < labels.length; i++) {
            final String val = values[i];
            final String lbl = labels[i];

            View itemView = LayoutInflater.from(this)
                    .inflate(R.layout.item_popup, popupLayout, false);
            TextView tvItem = itemView.findViewById(R.id.tvPopupItem);
            tvItem.setText(lbl);

            // Đánh dấu item đang được chọn
            if (val.equals(currentValue)) {
                tvItem.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvItem.setTextSize(14f);
                // Thêm dấu tick
                tvItem.setText("✓  " + lbl);
                tvItem.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                tvItem.setTextColor(getResources().getColor(android.R.color.black));
            }

            // Thêm divider giữa các item (trừ item cuối)
            if (i < labels.length - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 1);
                divParams.setMargins(20, 0, 20, 0);
                divider.setLayoutParams(divParams);
                divider.setBackgroundColor(0xFFEEEEEE);

                popupLayout.addView(itemView);
                popupLayout.addView(divider);
            } else {
                popupLayout.addView(itemView);
            }

            tvItem.setOnClickListener(v -> {
                callback.onSelected(val, lbl);
                popup.dismiss();
            });
        }

        // Hiển thị popup bên dưới nút
        popup.showAsDropDown(anchor, -16, 8, Gravity.START);
    }

    private void apDungLocVaSapXep() {
        List<Phong> ketQua = controller.locVaSapXep(
                currentKeyword, currentTinhTrang, currentSort);
        adapter.capNhatDanhSach(ketQua);
        tvThongKe.setText("Hiển thị: " + ketQua.size() + " phòng");
    }

    private void hienThiDialogXoa(int position) {
        List<Phong> ds = controller.locVaSapXep(
                currentKeyword, currentTinhTrang, currentSort);
        Phong phong = ds.get(position);
        int realIndex = controller.getDanhSachPhong().indexOf(phong);

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa \"" + phong.getTenPhong() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    controller.xoaPhong(realIndex);
                    apDungLocVaSapXep();
                    capNhatThongKe();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void capNhatThongKe() {
        tvSoPhongTrong.setText(controller.soPhongTrong() + " phòng");
        tvSoPhongThue.setText(controller.soPhongDaThue() + " phòng");
    }
}