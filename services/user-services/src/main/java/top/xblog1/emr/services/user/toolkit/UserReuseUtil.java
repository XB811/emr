package top.xblog1.emr.services.user.toolkit;

import static top.xblog1.emr.services.user.common.constant.EmrConstant.PATIENT_REGISTER_PHONE_REUSE_SHARDING_COUNT;

/**
 * 用户名可复用工具类
 */
public final class UserReuseUtil {

    /**
     * 计算分片位置
     */
    public static int hashShardingIdx(String phone) {
        return Math.abs(phone.hashCode() % PATIENT_REGISTER_PHONE_REUSE_SHARDING_COUNT);
    }
}
