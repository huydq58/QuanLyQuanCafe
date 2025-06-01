package QuanLyQuanCafe.crypto;

import org.mindrot.jbcrypt.BCrypt;

public class Hashing {
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }
}
