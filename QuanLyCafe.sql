Create Database QuanLyQuanCafe
Use QuanLyQuanCafe
CREATE TABLE TaiKhoan (
    TenDangNhap NVARCHAR(50) PRIMARY KEY,
    MatKhauHash NVARCHAR(255) NOT NULL, -- Lưu trữ mật khẩu đã mã hóa (nên dùng thuật toán băm an toàn)
    MaNV INT UNIQUE, -- Mỗi nhân viên chỉ có 1 tài khoản duy nhất
    -- Khóa ngoại sẽ được thêm sau
);
Go
CREATE TABLE Category (
    id INT IDENTITY(1,1) PRIMARY KEY,       -- Mã danh mục, tự tăng
    name NVARCHAR(100) NOT NULL UNIQUE,     -- Tên danh mục (duy nhất)
    description NVARCHAR(255),              -- Mô tả danh mục (tùy chọn)
    isActive BIT DEFAULT 1                  -- Có đang dùng hay không
);
Go
CREATE TABLE Food (
    id INT IDENTITY(1,1) PRIMARY KEY,           -- Tự tăng
    name NVARCHAR(100) NOT NULL,                -- Tên món ăn
    categoryId INT NOT NULL,                    -- Mã danh mục (FK)
    price DECIMAL(10, 2) NOT NULL,              -- Giá món
    unit NVARCHAR(20),                          -- Đơn vị tính (phần, ly...)
    description NVARCHAR(255),                  -- Mô tả ngắn
    isAvailable BIT DEFAULT 1,                  -- Còn bán hay không
    imagePath NVARCHAR(255),                    -- Đường dẫn ảnh (tuỳ chọn)
    CONSTRAINT FK_Food_Category FOREIGN KEY (categoryId) REFERENCES Category(id)--Khóa ngoại
);
Go

CREATE TABLE TableFood (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    isAvailable BIT NOT NULL DEFAULT 1
);
GO
CREATE TABLE Bill (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tableID INT NOT NULL,
    disCount INT DEFAULT 0,
    paid BIT DEFAULT 0,
    orderDate DATETIME DEFAULT GETDATE(),
    paidDate DATETIME,
    totalPrice DECIMAL(10,2) DEFAULT 0,
    CONSTRAINT FK_Bill_Table FOREIGN KEY (tableID) REFERENCES TableFood(id)
);
Go
CREATE TABLE [Order] (
    id INT IDENTITY(1,1) PRIMARY KEY,
    billID INT NOT NULL,
    foodID INT NOT NULL,
    count BIGINT NOT NULL,
    CONSTRAINT FK_Order_Bill FOREIGN KEY (billID) REFERENCES Bill(id),
    CONSTRAINT FK_Order_Food FOREIGN KEY (foodID) REFERENCES Food(id)
);


Select * from Bill

Delete from TableFood
INSERT INTO TableFood (name, isAvailable)
VALUES 
(N'Bàn 1', 1),
(N'Bàn 2', 1),
(N'Bàn 3', 1),
(N'Bàn 4', 1),
(N'Bàn 5', 0);

INSERT INTO Category (name, description, isActive) VALUES
(N'Nước uống', N'Danh mục các loại nước uống như cà phê, trà, nước ngọt', 1),
(N'Món ăn nhẹ', N'Các món ăn vặt như khoai tây chiên, bánh ngọt, snack', 1),
(N'Món chính', N'Món ăn chính như cơm, mì, bún...', 1),
(N'Tráng miệng', N'Các món tráng miệng như kem, chè, rau câu', 1),
(N'Ngừng kinh doanh', N'Danh mục đã dừng bán', 0);

INSERT INTO Food (name, categoryId, price, unit, description, isAvailable, imagePath) VALUES
(N'Cà phê sữa đá', 1, 25000.00, N'Ly', N'Cà phê pha phin truyền thống với sữa đặc', 1, N'C:\Users\Administrator\Desktop\Java\QuanLyQuanCafe\src\main\resources\images\caphe_sua.png'),
(N'Cà phê đá', 1, 20000.00, N'Ly', N'Cà phê pha phin truyền thống', 1, N'C:\Users\Administrator\Desktop\Java\QuanLyQuanCafe\src\main\resources\images\cafe_den.png'),
(N'Trà chanh', 1, 20000.00, N'Ly', N'Trà chanh thơm ngon giải khát', 1, N'C:\Users\Administrator\Desktop\Java\QuanLyQuanCafe\src\main\resources\images\tra_chanh.png'),
(N'Trà sữa trân châu', 1, 20000.00, N'Ly', N'Trà sữa trân châu ngọt ngào', 1, N'C:\Users\Administrator\Desktop\Java\QuanLyQuanCafe\src\main\resources\images\tra_sua_tran_chau.png');

Select * from Bill

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
        WHERE i.paid = 1
    END
END
