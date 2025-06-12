package QuanLyQuanCafe.model;

public class NguyenLieu {
    private int id;
    private String ten;
    private String donViTinh;
    private double soLuongTon;
    private double nguongCanhBao;

    // Constructors
    public NguyenLieu() {}

    public NguyenLieu(int id, String ten, String donViTinh, double soLuongTon, double nguongCanhBao) {
        this.id = id;
        this.ten = ten;
        this.donViTinh = donViTinh;
        this.soLuongTon = soLuongTon;
        this.nguongCanhBao = nguongCanhBao;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public double getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(double soLuongTon) { this.soLuongTon = soLuongTon; }

    public double getNguongCanhBao() { return nguongCanhBao; }
    public void setNguongCanhBao(double nguongCanhBao) { this.nguongCanhBao = nguongCanhBao; }

    @Override
    public String toString() {
        // Điều này giúp ChoiceBox hiển thị tên thay vì object reference
        return this.ten; 
    }
}