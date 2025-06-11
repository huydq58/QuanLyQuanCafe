package QuanLyQuanCafe;

import org.mindrot.jbcrypt.BCrypt;

public class TempHashGenerator {

    public static void main(String[] args) {
        // Mật khẩu chúng ta muốn mã hóa
        String passwordToHash = "123";

        // Dùng thư viện của dự án để tạo chuỗi mã hóa
        String generatedHash = BCrypt.hashpw(passwordToHash, BCrypt.gensalt(12));

        // In chuỗi đó ra màn hình Console
        System.out.println("=====================================================================");
        System.out.println("VUI LÒNG SAO CHÉP TOÀN BỘ CHUỖI MÃ HÓA BÊN DƯỚI:");
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        
        System.out.println(generatedHash);
        
        System.out.println("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        System.out.println("=====================================================================");
    }
}