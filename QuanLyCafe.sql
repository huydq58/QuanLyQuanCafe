/************************************************************************************************
 * SCRIPT CÀI ĐẶT CƠ SỞ DỮ LIỆU CHO DỰ ÁN QUẢN LÝ QUÁN CAFE
 *
 * Phiên bản: 2.0 (Bản hoàn thiện sau khi nâng cấp)
 * Ngày cập nhật: 2025-06-12
 *
 * Mô tả:
 * Script này sẽ tự động xóa (nếu có) và tạo mới cơ sở dữ liệu QuanLyQuanCafe,
 * bao gồm tất cả các bảng, trigger và dữ liệu mẫu cần thiết.
 ************************************************************************************************/

-- Cảnh báo: Script sẽ xóa CSDL QuanLyQuanCafe cũ nếu tồn tại để cài đặt mới.
USE master;
GO

IF DB_ID('QuanLyQuanCafe') IS NOT NULL
BEGIN
    ALTER DATABASE QuanLyQuanCafe SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLyQuanCafe;
    PRINT 'Da xoa CSDL QuanLyQuanCafe cu de chuan bi cai dat.';
END
GO

CREATE DATABASE QuanLyQuanCafe;
PRINT 'Da tao CSDL QuanLyQuanCafe.';
GO

USE QuanLyQuanCafe;
GO

-------------------------------------------------------------------------------------------------
-- PHẦN 1: TẠO BẢNG
-------------------------------------------------------------------------------------------------
PRINT 'Dang tao cac bang...';

CREATE TABLE TaiKhoan (
    TenDangNhap NVARCHAR(50) PRIMARY KEY,
    MatKhauHash NVARCHAR(255) NOT NULL,
    MaNV INT UNIQUE,
    Role NVARCHAR(20) NOT NULL DEFAULT 'NHANVIEN'
);
PRINT '- Bang TaiKhoan ... OK';

CREATE TABLE Category (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(255),
    isActive BIT DEFAULT 1
);
PRINT '- Bang Category ... OK';

CREATE TABLE Food (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    categoryId INT NOT NULL,
    price BIGINT NOT NULL,
    unit NVARCHAR(20),
    description NVARCHAR(255),
    isAvailable BIT DEFAULT 1,
    imagePath NVARCHAR(255),
    CONSTRAINT FK_Food_Category FOREIGN KEY (categoryId) REFERENCES Category(id)
);
PRINT '- Bang Food ... OK';

CREATE TABLE TableFood (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    isAvailable BIT NOT NULL DEFAULT 1
);
PRINT '- Bang TableFood ... OK';

CREATE TABLE Bill (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tableID INT NOT NULL,
    disCount INT DEFAULT 0,
    paid BIT DEFAULT 0,
    orderDate DATETIME DEFAULT GETDATE(),
    paidDate DATETIME,
    totalPrice DECIMAL(18, 2) DEFAULT 0,
    CONSTRAINT FK_Bill_Table FOREIGN KEY (tableID) REFERENCES TableFood(id)
);
PRINT '- Bang Bill ... OK';

CREATE TABLE [Order] (
    id INT IDENTITY(1,1) PRIMARY KEY,
    billID INT NOT NULL,
    foodID INT NOT NULL,
    count BIGINT NOT NULL,
    CONSTRAINT FK_Order_Bill FOREIGN KEY (billID) REFERENCES Bill(id) ON DELETE CASCADE,
    CONSTRAINT FK_Order_Food FOREIGN KEY (foodID) REFERENCES Food(id) ON DELETE CASCADE
);
PRINT '- Bang [Order] ... OK';

CREATE TABLE NguyenLieu (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten NVARCHAR(255) NOT NULL UNIQUE,
    donViTinh NVARCHAR(50) NOT NULL,
    soLuongTon DECIMAL(18, 2) NOT NULL DEFAULT 0,
    nguongCanhBao DECIMAL(18, 2) NOT NULL DEFAULT 0
);
PRINT '- Bang NguyenLieu ... OK';

CREATE TABLE DinhLuongMonAn (
    idMonAn INT NOT NULL,
    idNguyenLieu INT NOT NULL,
    soLuongCan DECIMAL(18, 2) NOT NULL,
    PRIMARY KEY (idMonAn, idNguyenLieu),
    CONSTRAINT FK_DinhLuong_Food FOREIGN KEY (idMonAn) REFERENCES Food(id) ON DELETE CASCADE,
    CONSTRAINT FK_DinhLuong_NguyenLieu FOREIGN KEY (idNguyenLieu) REFERENCES NguyenLieu(id) ON DELETE CASCADE
);
PRINT '- Bang DinhLuongMonAn ... OK';
GO

-------------------------------------------------------------------------------------------------
-- PHẦN 2: TẠO TRIGGER
-------------------------------------------------------------------------------------------------
PRINT 'Dang tao cac trigger...';
GO
CREATE TRIGGER trg_Bill_Paid
ON Bill
AFTER UPDATE
AS
BEGIN
    IF UPDATE(paid)
    BEGIN
        UPDATE TableFood
        SET isAvailable = 1
        FROM TableFood tf
        INNER JOIN inserted i ON tf.id = i.tableID
        WHERE i.paid = 1;
    END
END
GO
PRINT '- Trigger trg_Bill_Paid ... OK';
GO
-------------------------------------------------------------------------------------------------
-- PHẦN 3: CHÈN DỮ LIỆU MẪU
-------------------------------------------------------------------------------------------------
PRINT 'Dang chen du lieu mau...';

-- Tài khoản
-- Admin: abc / 123
-- Staff: nhanvien01 / 123
INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, MaNV, Role) VALUES
('abc', '$2a$12$jRDyDdHhTjI1QuS735m8cu6yE8sTcrC4t5La3hH49GSam2ItXMgDa', 1, 'QUANLY'),
('nhanvien01', '$2a$12$J9gL.lF.yO4y5.R7e8s9t.uVwXyZaBcDeFgHiJkLmNoPqRsTuVwX', 2, 'NHANVIEN');
PRINT '- Du lieu TaiKhoan ... OK';

-- Danh mục món ăn
INSERT INTO Category (name) VALUES (N'Cà Phê'), (N'Trà & Thức uống khác'), (N'Đồ Ăn Vặt');
PRINT '- Du lieu Category ... OK';

-- Bàn ăn
INSERT INTO TableFood (name, isAvailable) VALUES
(N'Bàn 1', 1), (N'Bàn 2', 1), (N'Bàn 3', 1), (N'Bàn 4', 1), (N'Bàn 5', 1),
(N'Bàn 6', 1), (N'Bàn 7', 1), (N'Bàn 8', 1), (N'Bàn 9', 1), (N'Bàn 10', 1);
PRINT '- Du lieu TableFood ... OK';

-- Món ăn
INSERT INTO Food (name, categoryId, price, imagePath) VALUES
(N'Cà Phê Sữa', 1, 25000, 'caphe_sua.png'),
(N'Cà Phê Đen', 1, 20000, 'cafe_den.png'),
(N'Trà Chanh', 2, 22000, 'tra_chanh.png'),
(N'Trà Sữa Trân Châu', 2, 30000, 'tra_sua_tran_chau.png'),
(N'Hướng Dương', 3, 15000, 'huong_duong.png');
PRINT '- Du lieu Food ... OK';

-- Nguyên liệu
INSERT INTO NguyenLieu (ten, donViTinh, soLuongTon, nguongCanhBao) VALUES
(N'Hạt cà phê Robusta', 'gram', 5000, 500),  -- ID: 1
(N'Sữa đặc Ông Thọ', 'ml', 10000, 1000), -- ID: 2
(N'Lá trà đen', 'gram', 2000, 200),     -- ID: 3
(N'Chanh tươi', 'quả', 100, 10),        -- ID: 4
(N'Đường cát', 'gram', 20000, 2000),   -- ID: 5
(N'Bột sữa', 'gram', 5000, 500),      -- ID: 6
(N'Trân châu đen', 'gram', 3000, 300);   -- ID: 7
PRINT '- Du lieu NguyenLieu ... OK';

-- Định lượng/Công thức
-- Lưu ý: ID món ăn và nguyên liệu dựa trên thứ tự chèn ở trên
INSERT INTO DinhLuongMonAn (idMonAn, idNguyenLieu, soLuongCan) VALUES
(1, 1, 25), (1, 2, 40), -- Cà Phê Sữa: 25g cafe, 40ml sữa
(2, 1, 20), (2, 5, 10), -- Cà Phê Đen: 20g cafe, 10g đường
(3, 3, 10), (3, 4, 1), (3, 5, 20), -- Trà Chanh: 10g trà, 1 quả chanh, 20g đường
(4, 3, 15), (4, 6, 30), (4, 7, 50); -- Trà Sữa: 15g trà, 30g bột sữa, 50g trân châu
PRINT '- Du lieu DinhLuongMonAn ... OK';
GO

PRINT '***************************************************';
PRINT '*** CAI DAT CO SO DU LIEU THANH CONG!           ***';
PRINT '***************************************************';
GO