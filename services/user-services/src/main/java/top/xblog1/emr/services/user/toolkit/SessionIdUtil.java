package top.xblog1.emr.services.user.toolkit;

import lombok.extern.slf4j.Slf4j;
import top.xblog1.emr.framework.starter.user.core.UserInfoDTO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 *
 */
@Slf4j
public final class SessionIdUtil {
    private static final String SECRET = "SecretKey039245678901232039487623456783092349288901402967890140939827";
    public static String generateAccessToken(UserInfoDTO userInfo){
        try{
            String raw = userInfo.toString()+"_"+System.currentTimeMillis()+"_"+SECRET;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成会话ID失败", e);
        }
    }
}
