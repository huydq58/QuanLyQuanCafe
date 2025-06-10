package QuanLyQuanCafe.model;
public class Food {
    private int id;
    private String name;
    private int categoryId;
    private long price;
    private String unit;
    private String description;
    private boolean isAvailable;
    private String imagePath;

    public Food(String name, long price, int categoryId) {
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
    }

    public Food() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    // Đây chỉ trả về id danh mục (int)
    public int getCategory() {
        return categoryId;
    }

    // Không thể trực tiếp gọi getCategory().getName() vì getCategory() trả về int,
    // cần lấy tên danh mục từ DAL hoặc Service theo categoryId này.
    public String getCategoryName() {
        // Ví dụ: gọi service hoặc DAL để lấy tên danh mục theo categoryId
        // return CategoryService.getNameById(categoryId);
        return ""; // tạm thời trả về chuỗi rỗng, cần implement bên ngoài
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public String getImgName() {
        return imagePath;
    }

    public void setImageName(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryId=" + categoryId +
                ", price=" + price +
                ", unit='" + unit + '\'' +
                ", description='" + description + '\'' +
                ", isAvailable=" + isAvailable +
                ", imageName='" + imagePath + '\'' +
                '}';
    }
}
