package top.xblog1.emr.services.department.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("department")
public class DepartmentDO  extends BaseDO {
    Long id;
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    String code;
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    String name;
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    String detail;
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    String address;
}
