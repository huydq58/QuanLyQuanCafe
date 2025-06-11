/************************************************************************************************
 * SQL SCRIPT FOR QUAN LY QUAN CAFE PROJECT
 *
 * Description:
 * This script will create the database, all required tables, and insert sample data
 * for the application to run correctly.
 *
 * Author: Nguyễn Tấn Phúc và Đoàn Quang Huy
 * Version: 1.0
 * Date: 2025-06-12
 ************************************************************************************************/

-- Use master database to check for existence of QuanLyQuanCafe DB
USE master;
GO

-- WARNING: The following lines will drop the existing database to ensure a clean setup.
-- Comment this section out if you want to preserve your existing data.
IF DB_ID('QuanLyQuanCafe') IS NOT NULL
BEGIN
    ALTER DATABASE QuanLyQuanCafe SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLyQuanCafe;
    PRINT 'Database QuanLyQuanCafe dropped for clean setup.';
END
GO

-- Create the database
CREATE DATABASE QuanLyQuanCafe;
PRINT 'Database QuanLyQuanCafe created.';
GO

-- Switch to the newly created database
USE QuanLyQuanCafe;
GO

-------------------------------------------------------------------------------------------------
-- PART 1: TABLE CREATION
-------------------------------------------------------------------------------------------------

PRINT 'Creating tables...';

-- Table: TaiKhoan (Accounts)
-- Stores user accounts with roles
CREATE TABLE TaiKhoan (
    TenDangNhap NVARCHAR(50) PRIMARY KEY,
    MatKhauHash NVARCHAR(255) NOT NULL,
    MaNV INT UNIQUE,
    Role NVARCHAR(20) NOT NULL DEFAULT 'NHANVIEN'
);
PRINT 'Table TaiKhoan created.';

-- Table: Category
-- Stores food and drink categories
CREATE TABLE Category (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(255),
    isActive BIT DEFAULT 1
);
PRINT 'Table Category created.';

-- Table: Food
-- Stores food and drink items
CREATE TABLE Food (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    categoryId INT NOT NULL,
    price DECIMAL(18, 2) NOT NULL,
    unit NVARCHAR(20),
    description NVARCHAR(255),
    isAvailable BIT DEFAULT 1,
    imagePath NVARCHAR(255),
    CONSTRAINT FK_Food_Category FOREIGN KEY (categoryId) REFERENCES Category(id)
);
PRINT 'Table Food created.';

-- Table: TableFood
-- Stores restaurant tables
CREATE TABLE TableFood (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    isAvailable BIT NOT NULL DEFAULT 1
);
PRINT 'Table TableFood created.';

-- Table: Bill
-- Stores bills/invoices
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
PRINT 'Table Bill created.';

-- Table: Order
-- Stores items within a bill (junction table)
CREATE TABLE [Order] (
    id INT IDENTITY(1,1) PRIMARY KEY,
    billID INT NOT NULL,
    foodID INT NOT NULL,
    count BIGINT NOT NULL,
    CONSTRAINT FK_Order_Bill FOREIGN KEY (billID) REFERENCES Bill(id) ON DELETE CASCADE,
    CONSTRAINT FK_Order_Food FOREIGN KEY (foodID) REFERENCES Food(id) ON DELETE CASCADE
);
PRINT 'Table [Order] created.';

-- Table: NguyenLieu (Ingredients)
-- Stores inventory items
CREATE TABLE NguyenLieu (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten NVARCHAR(255) NOT NULL UNIQUE,
    donViTinh NVARCHAR(50) NOT NULL,
    soLuongTon DECIMAL(18, 2) NOT NULL DEFAULT 0,
    nguongCanhBao DECIMAL(18, 2) NOT NULL DEFAULT 0
);
PRINT 'Table NguyenLieu created.';

-- Table: DinhLuongMonAn (Recipes)
-- Stores recipes for each food item
CREATE TABLE DinhLuongMonAn (
    idMonAn INT NOT NULL,
    idNguyenLieu INT NOT NULL,
    soLuongCan DECIMAL(18, 2) NOT NULL,
    PRIMARY KEY (idMonAn, idNguyenLieu),
    CONSTRAINT FK_DinhLuong_Food FOREIGN KEY (idMonAn) REFERENCES Food(id) ON DELETE CASCADE,
    CONSTRAINT FK_DinhLuong_NguyenLieu FOREIGN KEY (idNguyenLieu) REFERENCES NguyenLieu(id) ON DELETE CASCADE
);
PRINT 'Table DinhLuongMonAn created.';

-------------------------------------------------------------------------------------------------
-- PART 2: TRIGGERS
-------------------------------------------------------------------------------------------------
PRINT 'Creating triggers...';

-- Trigger to free up a table when a bill is paid
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
PRINT 'Trigger trg_Bill_Paid created.';

-------------------------------------------------------------------------------------------------
-- PART 3: SAMPLE DATA INSERTION
-------------------------------------------------------------------------------------------------
PRINT 'Inserting sample data...';

-- Insert Accounts
-- Default Admin: abc / 123
-- Default Staff: nhanvien01 / 123
INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, MaNV, Role) VALUES
('abc', '$2a$12$jRDyDdHhTjI1QuS735m8cu6yE8sTcrC4t5La3hH49GSam2ItXMgDa', 1, 'QUANLY'),
('nhanvien01', '$2a$12$8.A5S.A3v5D5qC7F6s0pQ.i2/a/O/o.N/k.p/M.q/r/s/t.u/v', 2, 'NHANVIEN');
PRINT 'Sample accounts inserted.';

-- Insert Categories
INSERT INTO Category (name, description) VALUES
(N'Cà Phê', N'Các loại cà phê pha máy và pha phin'),
(N'Trà & Thức uống khác', N'Các loại trà và nước giải khát'),
(N'Đồ Ăn Vặt', N'Các món ăn nhẹ dùng kèm');
PRINT 'Sample categories inserted.';

-- Insert Food items
-- IMPORTANT: Image paths are relative. The 'images' folder should be in the app's root directory.
INSERT INTO Food (name, categoryId, price, unit, description, imagePath) VALUES
(N'Cà Phê Đen', 1, 20000, N'Ly', N'Cà phê rang xay nguyên chất', 'cafe_den.png'),
(N'Cà Phê Sữa', 1, 25000, N'Ly', N'Cà phê pha phin với sữa đặc', 'caphe_sua.png'),
(N'Trà Chanh', 2, 22000, N'Ly', N'Trà chanh tươi mát giải nhiệt', 'tra_chanh.png'),
(N'Trà Sữa Trân Châu', 2, 30000, N'Ly', N'Trà sữa béo ngậy cùng trân châu dai', 'tra_sua_tran_chau.png'),
(N'Hướng Dương', 3, 15000, N'Đĩa', N'Hạt hướng dương rang thơm', 'huong_duong.png');
PRINT 'Sample food items inserted.';

-- Insert Tables
INSERT INTO TableFood (name, isAvailable) VALUES
(N'Bàn 1', 1), (N'Bàn 2', 1), (N'Bàn 3', 1), (N'Bàn 4', 1), (N'Bàn 5', 1),
(N'Bàn 6', 1), (N'Bàn 7', 1), (N'Bàn 8', 1), (N'Bàn 9', 1), (N'Bàn 10', 1);
PRINT 'Sample tables inserted.';

-- Insert Ingredients
INSERT INTO NguyenLieu (ten, donViTinh, soLuongTon, nguongCanhBao) VALUES
(N'Hạt cà phê Robusta', 'gram', 5000, 500),  -- ID: 1
(N'Sữa đặc Ông Thọ', 'ml', 10000, 1000), -- ID: 2
(N'Lá trà đen', 'gram', 2000, 200),     -- ID: 3
(N'Chanh tươi', 'quả', 100, 10),        -- ID: 4
(N'Đường cát', 'gram', 20000, 2000),   -- ID: 5
(N'Bột sữa', 'gram', 5000, 500),      -- ID: 6
(N'Trân châu đen', 'gram', 3000, 300);   -- ID: 7
PRINT 'Sample ingredients inserted.';

-- Insert Recipes
-- Assumes Food IDs are 1, 2, 3, 4 based on insertion order
-- Recipe for 'Cà Phê Đen' (Food ID = 1)
INSERT INTO DinhLuongMonAn (idMonAn, idNguyenLieu, soLuongCan) VALUES (1, 1, 25), (1, 5, 10);
-- Recipe for 'Cà Phê Sữa' (Food ID = 2)
INSERT INTO DinhLuongMonAn (idMonAn, idNguyenLieu, soLuongCan) VALUES (2, 1, 25), (2, 2, 40);
-- Recipe for 'Trà Chanh' (Food ID = 3)
INSERT INTO DinhLuongMonAn (idMonAn, idNguyenLieu, soLuongCan) VALUES (3, 3, 10), (3, 4, 1), (3, 5, 20);
-- Recipe for 'Trà Sữa Trân Châu' (Food ID = 4)
INSERT INTO DinhLuongMonAn (idMonAn, idNguyenLieu, soLuongCan) VALUES (4, 3, 15), (4, 6, 30), (4, 7, 50);
PRINT 'Sample recipes inserted.';

-------------------------------------------------------------------------------------------------
-- SETUP COMPLETE
-------------------------------------------------------------------------------------------------
PRINT '****************************************************************';
PRINT '*** DATABASE SETUP COMPLETED SUCCESSFULLY!            ***';
PRINT '****************************************************************';
GO