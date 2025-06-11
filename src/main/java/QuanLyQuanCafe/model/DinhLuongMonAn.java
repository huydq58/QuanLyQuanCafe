package QuanLyQuanCafe.model;

public class DinhLuongMonAn {
    private int idMonAn;
    private int idNguyenLieu;
    private double soLuongCan;

    // Constructors
    public DinhLuongMonAn() {}

    public DinhLuongMonAn(int idMonAn, int idNguyenLieu, double soLuongCan) {
        this.idMonAn = idMonAn;
        this.idNguyenLieu = idNguyenLieu;
        this.soLuongCan = soLuongCan;
    }

    // Getters and Setters
    public int getIdMonAn() { return idMonAn; }
    public void setIdMonAn(int idMonAn) { this.idMonAn = idMonAn; }

    public int getIdNguyenLieu() { return idNguyenLieu; }
    public void setIdNguyenLieu(int idNguyenLieu) { this.idNguyenLieu = idNguyenLieu; }

    public double getSoLuongCan() { return soLuongCan; }
    public void setSoLuongCan(double soLuongCan) { this.soLuongCan = soLuongCan; }
}