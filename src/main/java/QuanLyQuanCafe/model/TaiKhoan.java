package QuanLyQuanCafe.model;

public class TaiKhoan {
    private String tenDangNhap;
    private String matKhauHash;
    private Integer maNV;
    private String role; // <-- THÊM DÒNG NÀY

    // Constructor (nếu cần)
    public TaiKhoan() {
        tenDangNhap = "";
        matKhauHash = "";
        maNV = 0;
        role = "NHANVIEN";
    }

    // Getter & Setter
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhauHash() {
        return matKhauHash;
    }

    public void setMatKhauHash(String matKhauHash) {
        this.matKhauHash = matKhauHash;
    }

    public Integer getMaNV() {
        return maNV;
    }

    public void setMaNV(Integer maNV) {
        this.maNV = maNV;
    }
    
    // <-- THÊM CÁC HÀM NÀY
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}