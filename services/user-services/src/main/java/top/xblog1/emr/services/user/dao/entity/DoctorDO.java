package top.xblog1.emr.services.user.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.xblog1.emr.framework.starter.database.base.BaseDO;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("doctor")
public class DoctorDO extends BaseDO {
    /**
     * id
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 手机号
     */
    private String phone;
    /**
    * 性别
    */
    private Boolean gender;
    /**
     * 科室id
     */
    private Long departmentId;
    /**
     * 职称
     */
    private String title;
    /**
    * 特长
    */
    private String specialty;
}
