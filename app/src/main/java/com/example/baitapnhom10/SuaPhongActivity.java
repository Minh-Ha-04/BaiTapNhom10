package com.example.nhatro;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nhatro.model.Phong;
import java.text.NumberFormat;
import java.util.Locale;

public class SuaPhongActivity extends AppCompatActivity {

    private EditText etMaPhong, etTenPhong, etGiaThue, etTenNguoiThue, etSoDienThoai;
    private CheckBox cbDaThue;
    private LinearLayout layoutNguoiThue;
    private Button btnLuu, btnHuy;
    private int position;

    private boolean dangFormat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_phong); // dùng chung layout
        setTitle("Sửa Thông Tin Phòng");

        position = getIntent().getIntExtra("position", -1);
        if (position == -1) { finish(); return; }

        etMaPhong       = findViewById(R.id.etMaPhong);
        etTenPhong      = findViewById(R.id.etTenPhong);
        etGiaThue       = findViewById(R.id.etGiaThue);
        cbDaThue        = findViewById(R.id.cbDaThue);
        layoutNguoiThue = findViewById(R.id.layoutNguoiThue);
        etTenNguoiThue  = findViewById(R.id.etTenNguoiThue);
        etSoDienThoai   = findViewById(R.id.etSoDienThoai);
        btnLuu          = findViewById(R.id.btnThem);
        btnHuy          = findViewById(R.id.btnHuy);

        btnLuu.setText("💾 Lưu thay đổi");

        // Điền dữ liệu cũ
        Phong phong = MainActivity.sharedController.getPhongTheoViTri(position);
        etMaPhong.setText(phong.getMaPhong());
        etMaPhong.setEnabled(false); // không cho sửa mã phòng
        etTenPhong.setText(phong.getTenPhong());

        // Hiển thị giá có format
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        etGiaThue.setText(formatter.format((long) phong.getGiaThue()));

        cbDaThue.setChecked(phong.isDaThue());
        etTenNguoiThue.setText(phong.getTenNguoiThue());
        etSoDienThoai.setText(phong.getSoDienThoai());
        layoutNguoiThue.setVisibility(phong.isDaThue() ? View.VISIBLE : View.GONE);

        // Format giá tiền real-time
        etGiaThue.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (dangFormat) return;
                dangFormat = true;

                String raw = s.toString().replace(".", "");
                if (!raw.isEmpty()) {
                    try {
                        long soThuần = Long.parseLong(raw);
                        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
                        String formatted = fmt.format(soThuần);
                        etGiaThue.setText(formatted);
                        etGiaThue.setSelection(formatted.length());
                    } catch (NumberFormatException e) {
                        // bỏ qua
                    }
                }

                dangFormat = false;
            }
        });

        cbDaThue.setOnCheckedChangeListener((buttonView, isChecked) ->
                layoutNguoiThue.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        btnLuu.setOnClickListener(v -> suaPhong());
        btnHuy.setOnClickListener(v -> finish());
    }

    private void suaPhong() {
        String tenPhong    = etTenPhong.getText().toString().trim();
        // Bỏ dấu chấm trước khi parse
        String giaThueStr  = etGiaThue.getText().toString().replace(".", "").trim();
        boolean daThue     = cbDaThue.isChecked();
        String tenNguoiThue = etTenNguoiThue.getText().toString().trim();
        String soDienThoai = etSoDienThoai.getText().toString().trim();

        if (tenPhong.isEmpty()) {
            etTenPhong.setError("Vui lòng nhập tên phòng");
            etTenPhong.requestFocus(); return;
        }
        if (giaThueStr.isEmpty()) {
            etGiaThue.setError("Vui lòng nhập giá thuê");
            etGiaThue.requestFocus(); return;
        }
        double giaThue;
        try {
            giaThue = Double.parseDouble(giaThueStr);
            if (giaThue <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            etGiaThue.setError("Giá thuê không hợp lệ");
            etGiaThue.requestFocus(); return;
        }
        if (daThue && tenNguoiThue.isEmpty()) {
            etTenNguoiThue.setError("Vui lòng nhập tên người thuê");
            etTenNguoiThue.requestFocus(); return;
        }
        if (daThue && soDienThoai.isEmpty()) {
            etSoDienThoai.setError("Vui lòng nhập số điện thoại");
            etSoDienThoai.requestFocus(); return;
        }
        if (daThue && !soDienThoai.matches("^(0|\\+84)[0-9]{9}$")) {
            etSoDienThoai.setError("Số điện thoại không hợp lệ");
            etSoDienThoai.requestFocus(); return;
        }

        String maPhong = etMaPhong.getText().toString().trim();
        Phong phongMoi = new Phong(maPhong, tenPhong, giaThue, daThue,
                daThue ? tenNguoiThue : "",
                daThue ? soDienThoai : "");

        MainActivity.sharedController.capNhatPhong(position, phongMoi);
        Toast.makeText(this, "✅ Cập nhật thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}