package com.example.nhatro.model;

public class Phong {
    private String maPhong;
    private String tenPhong;
    private double giaThue;
    private boolean daThue; // false = Còn trống, true = Đã thuê
    private String tenNguoiThue;
    private String soDienThoai;

    public Phong(String maPhong, String tenPhong, double giaThue,
                 boolean daThue, String tenNguoiThue, String soDienThoai) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.giaThue = giaThue;
        this.daThue = daThue;
        this.tenNguoiThue = tenNguoiThue;
        this.soDienThoai = soDienThoai;
    }

    // Getters
    public String getMaPhong() { return maPhong; }
    public String getTenPhong() { return tenPhong; }
    public double getGiaThue() { return giaThue; }
    public boolean isDaThue() { return daThue; }
    public String getTenNguoiThue() { return tenNguoiThue; }
    public String getSoDienThoai() { return soDienThoai; }

    // Setters
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }
    public void setGiaThue(double giaThue) { this.giaThue = giaThue; }
    public void setDaThue(boolean daThue) { this.daThue = daThue; }
    public void setTenNguoiThue(String tenNguoiThue) { this.tenNguoiThue = tenNguoiThue; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
}