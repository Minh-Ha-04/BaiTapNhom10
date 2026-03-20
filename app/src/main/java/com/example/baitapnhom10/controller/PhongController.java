package com.example.nhatro.controller;

import com.example.nhatro.model.Phong;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
public class PhongController {
    private List<Phong> danhSachPhong;

    public PhongController() {
        danhSachPhong = new ArrayList<>();
        themDuLieuMau();
    }

    // Thêm dữ liệu mẫu ban đầu
    private void themDuLieuMau() {
        danhSachPhong.add(new Phong("P01", "Phòng 101", 2500000, false, "", ""));
        danhSachPhong.add(new Phong("P02", "Phòng 102", 3000000, true, "Nguyễn Văn A", "0901234567"));
        danhSachPhong.add(new Phong("P03", "Phòng 201", 2800000, false, "", ""));
        danhSachPhong.add(new Phong("P04", "Phòng 202", 3500000, true, "Trần Thị B", "0912345678"));
    }

    // CREATE
    public boolean themPhong(Phong phong) {
        // Kiểm tra mã phòng trùng
        for (Phong p : danhSachPhong) {
            if (p.getMaPhong().equalsIgnoreCase(phong.getMaPhong())) {
                return false; // mã đã tồn tại
            }
        }
        danhSachPhong.add(phong);
        return true;
    }

    // READ
    public List<Phong> getDanhSachPhong() {
        return danhSachPhong;
    }

    public Phong getPhongTheoViTri(int position) {
        return danhSachPhong.get(position);
    }

    // UPDATE
    public void capNhatPhong(int position, Phong phongMoi) {
        danhSachPhong.set(position, phongMoi);
    }

    // DELETE
    public void xoaPhong(int position) {
        danhSachPhong.remove(position);
    }

    // Tìm kiếm theo tên
    public List<Phong> timKiem(String keyword) {
        List<Phong> ketQua = new ArrayList<>();
        for (Phong p : danhSachPhong) {
            if (p.getTenPhong().toLowerCase().contains(keyword.toLowerCase())
                    || p.getMaPhong().toLowerCase().contains(keyword.toLowerCase())) {
                ketQua.add(p);
            }
        }
        return ketQua;
    }

    // Thống kê
    public int soPhongTrong() {
        int count = 0;
        for (Phong p : danhSachPhong) if (!p.isDaThue()) count++;
        return count;
    }

    public int soPhongDaThue() {
        int count = 0;
        for (Phong p : danhSachPhong) if (p.isDaThue()) count++;
        return count;
    }
    // ===== LỌC THEO TÌNH TRẠNG =====
    public List<Phong> locTheoTinhTrang(String tinhTrang) {
        List<Phong> ketQua = new ArrayList<>();
        for (Phong p : danhSachPhong) {
            switch (tinhTrang) {
                case "con_trong":
                    if (!p.isDaThue()) ketQua.add(p);
                    break;
                case "da_thue":
                    if (p.isDaThue()) ketQua.add(p);
                    break;
                default: // "tat_ca"
                    ketQua.add(p);
                    break;
            }
        }
        return ketQua;
    }

    // ===== SẮP XẾP THEO GIÁ =====
    public List<Phong> sapXepTheoGia(List<Phong> ds, String kieuSap) {
        List<Phong> ketQua = new ArrayList<>(ds);
        switch (kieuSap) {
            case "tang_dan":
                Collections.sort(ketQua, (a, b) -> Double.compare(a.getGiaThue(), b.getGiaThue()));
                break;
            case "giam_dan":
                Collections.sort(ketQua, (a, b) -> Double.compare(b.getGiaThue(), a.getGiaThue()));
                break;
            default: // mac_dinh - giữ nguyên
                break;
        }
        return ketQua;
    }

    // ===== LỌC + TÌM KIẾM + SẮP XẾP KẾT HỢP =====
    public List<Phong> locVaSapXep(String keyword, String tinhTrang, String kieuSap) {
        // Bước 1: lọc theo tình trạng
        List<Phong> ds = locTheoTinhTrang(tinhTrang);

        // Bước 2: lọc theo từ khóa tìm kiếm
        List<Phong> dsTimKiem = new ArrayList<>();
        for (Phong p : ds) {
            if (keyword.isEmpty()
                    || p.getTenPhong().toLowerCase().contains(keyword.toLowerCase())
                    || p.getMaPhong().toLowerCase().contains(keyword.toLowerCase())) {
                dsTimKiem.add(p);
            }
        }

        // Bước 3: sắp xếp theo giá
        return sapXepTheoGia(dsTimKiem, kieuSap);
    }
}