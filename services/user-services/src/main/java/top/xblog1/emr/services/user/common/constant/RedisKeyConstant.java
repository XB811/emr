package top.xblog1.emr.services.user.common.constant;

/**
 * Redis Key 定义常量类
 */
public final class RedisKeyConstant {

    /**
     * 三种用户注册锁，Key Prefix + 用户名
     */
    public static final String LOCK_ADMIN_REGISTER = "emr-user-service:lock:admin-register:";
    public static final String LOCK_DOCTOR_REGISTER = "emr-user-service:lock:doctor-register:";
    public static final String LOCK_PATIENT_REGISTER = "emr-user-service:lock:patient-register:";

    /**
     * 用户注销锁，Key Prefix + 用户名
     */
    public static final String USER_DELETION = "emr-user-service:user-deletion:";
    public static final String PATIENT_DELETION = "emr-user-service:patient-deletion:";
    /**
     * 用户注册可复用用户名分片，Key Prefix + Idx
     */
    public static final String PATIENT_REGISTER_PHONE_REUSE_SHARDING = "emr-user-service:patient-phone-reuse:";
    /**
     * 医生和管理员用户名字自增
     */
    public static final String USER_REGISTER_USERNAME_ADMIN ="emr-user-service:user_register:username:admin";
    public static final String USER_REGISTER_USERNAME_DOCTOR ="emr-user-service:user_register:username:doctor";
    /**
    * 医生和管理员手机号存储
    */
    public static final String USER_REGISTER_PHONE_ADMIN="emr-user-service:user_register:phone:admin";
    public static final String USER_REGISTER_PHONE_DOCTOR="emr-user-service:user_register:phone:doctor";
    /**
    * 用户id到token的映射 Key Prefix +Idx
    */
    public static final String USER_LOGIN_TOKEN_PREFIX = "emr-user-service:user-login:token:";
    public static final String USER_LOGIN_ADMIN_TOKEN_PREFIX ="emr-user-service:user_login:token:admin:";
    public static final String USER_LOGIN_DOCTOR_TOKEN_PREFIX="emr-user-service:user_login:token:doctor:";
    public static final String USER_LOGIN_PATIEN_TOKEN_PREFIX ="emr-user-service:user_login:token:patient:";
}
