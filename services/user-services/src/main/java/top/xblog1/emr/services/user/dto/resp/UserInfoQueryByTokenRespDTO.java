package top.xblog1.emr.services.user.dto.resp;

import lombok.Builder;
import lombok.Data;

/**
 *
 */
@Data
@Builder
public class UserInfoQueryByTokenRespDTO {
    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * Token
     */
    private String accessToken;
    /**
     * 用户类型
     */
    private String userType;
}
