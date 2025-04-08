package top.xblog1.emr.services.user.toolkit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 密码加密工具
 */
public final class PasswordEncryptUtil {

    private static final String SALT = "nlefoliabnfawuirheawbfkjasdfkladfsjnwlaefdsafsjdfk";

    // 使用 SHA-256 加密密码（带固定盐）
    public static String encryptPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + SALT;
            byte[] hash = messageDigest.digest(saltedPassword.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("加密算法不可用", e);
        }
    }

    // 验证密码
    public static boolean verifyPassword(String password, String encryptedPassword) {
        String hashedPassword = encryptPassword(password);
        return hashedPassword.equals(encryptedPassword);
    }
}