package top.xblog1.emr.services.user.dto.req;

import lombok.Data;
import top.xblog1.emr.framework.starter.convention.page.PageRequest;

/**
 *
 */
@Data
public class UserPageQueryReqDTO extends PageRequest {
    /**
     * 用户名
     */
    private String username;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 科室id
     */
    private Long departmentId;

}
