package QuanLyQuanCafe.model;

import QuanLyQuanCafe.database.UserDB;
import QuanLyQuanCafe.enums.UserRole;

public class User implements Comparable<User> {

    private String name;
    private Integer userId; // UNIQUE //optimize search nhanh hơn nếu dùng với binary tree...
    private String username; // UNIQUE
    private String passwordHash;
    private UserRole role = UserRole.NHANVIEN;

    public User() {
    }

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;

        userId = UserDB.count + 1;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public int compareTo(User other) {

        Integer result = Integer.compare(this.userId, other.userId);

        System.out.println("Result " + result);

        return result;
    }

    public boolean checkPassword(String password) {

        try {
            String rawPassword = "";

            System.out.println("RAW: " + rawPassword);

            if (rawPassword.equals(password)) {
                return true;
            }

        } catch (Exception e) {
        }

        return false;
    }

    @Override
    public String toString() {
        return "User{" + "username='" + username + '\'' + ", password=" + passwordHash + ", name=" + name + '}';
    }
}
