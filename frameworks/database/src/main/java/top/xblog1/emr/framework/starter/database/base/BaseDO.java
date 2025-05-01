package top.xblog1.emr.framework.starter.database.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

/**
 * 数据持久层基础属性
 */
@Data
public class BaseDO {

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志
     */
    @TableField(fill = FieldFill.INSERT)
    @TableLogic(value = "0", delval = "id")
    private Long delFlag;
}
