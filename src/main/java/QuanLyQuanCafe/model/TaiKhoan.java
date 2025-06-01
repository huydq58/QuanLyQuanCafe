package QuanLyQuanCafe.model;

public class TaiKhoan {
    private String tenDangNhap;
    private String matKhauHash;
    private Integer maNV; // Integer thay cho int? (nullable)

    // Constructor (nếu cần)
    public TaiKhoan() {}

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
}
