package top.xblog1.emr.framework.starter.database.handler;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import top.xblog1.emr.framework.starter.distributedid.toolkit.SnowflakeIdUtil;

/**
 * 自定义雪花算法生成器
 */
public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        return SnowflakeIdUtil.nextId();
    }
}
