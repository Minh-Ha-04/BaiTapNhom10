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

public class ThemPhongActivity extends AppCompatActivity {

    private EditText etMaPhong, etTenPhong, etGiaThue, etTenNguoiThue, etSoDienThoai;
    private CheckBox cbDaThue;
    private LinearLayout layoutNguoiThue;
    private Button btnThem, btnHuy;

    private boolean dangFormat = false; // tránh vòng lặp vô hạn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_phong);
        setTitle("Thêm Phòng Mới");

        etMaPhong      = findViewById(R.id.etMaPhong);
        etTenPhong     = findViewById(R.id.etTenPhong);
        etGiaThue      = findViewById(R.id.etGiaThue);
        cbDaThue       = findViewById(R.id.cbDaThue);
        layoutNguoiThue = findViewById(R.id.layoutNguoiThue);
        etTenNguoiThue = findViewById(R.id.etTenNguoiThue);
        etSoDienThoai  = findViewById(R.id.etSoDienThoai);
        btnThem        = findViewById(R.id.btnThem);
        btnHuy         = findViewById(R.id.btnHuy);

        // Format giá tiền real-time
        etGiaThue.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (dangFormat) return;
                dangFormat = true;

                // Lấy số thuần (bỏ dấu chấm)
                String raw = s.toString().replace(".", "");
                if (!raw.isEmpty()) {
                    try {
                        long soThuần = Long.parseLong(raw);
                        // Format có dấu chấm phân cách hàng nghìn
                        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                        String formatted = formatter.format(soThuần);
                        etGiaThue.setText(formatted);
                        etGiaThue.setSelection(formatted.length()); // đặt con trỏ cuối
                    } catch (NumberFormatException e) {
                        // bỏ qua nếu không parse được
                    }
                }

                dangFormat = false;
            }
        });

        cbDaThue.setOnCheckedChangeListener((buttonView, isChecked) ->
                layoutNguoiThue.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        btnThem.setOnClickListener(v -> themPhong());
        btnHuy.setOnClickListener(v -> finish());
    }

    private void themPhong() {
        String maPhong     = etMaPhong.getText().toString().trim();
        String tenPhong    = etTenPhong.getText().toString().trim();
        // Bỏ dấu chấm trước khi parse thành số
        String giaThueStr  = etGiaThue.getText().toString().replace(".", "").trim();
        boolean daThue     = cbDaThue.isChecked();
        String tenNguoiThue = etTenNguoiThue.getText().toString().trim();
        String soDienThoai = etSoDienThoai.getText().toString().trim();

        // Validate
        if (maPhong.isEmpty()) {
            etMaPhong.setError("Vui lòng nhập mã phòng");
            etMaPhong.requestFocus(); return;
        }
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

        Phong phong = new Phong(maPhong, tenPhong, giaThue, daThue,
                daThue ? tenNguoiThue : "",
                daThue ? soDienThoai : "");

        boolean success = MainActivity.sharedController.themPhong(phong);
        if (success) {
            Toast.makeText(this, "✅ Thêm phòng thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            etMaPhong.setError("Mã phòng đã tồn tại");
            etMaPhong.requestFocus();
        }
    }
}