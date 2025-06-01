package QuanLyQuanCafe.model;
import QuanLyQuanCafe.enums.TableStatus;

import java.util.List;

public class TableFood {
    private int id;
    private String name;
    private boolean status;

    public TableFood() {
    }

    public TableFood(int id, String name, boolean status) {
        this.id = id;
        this.name = name;
        this.status = status;
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


    public boolean isAvailable() { return status; }
    public void setAvailable(boolean available) { status = available; }

    @Override
    public String toString() {
        return "Table{" + "name='" + name + '\'' + ", status=" + status + '}';
    }


}
