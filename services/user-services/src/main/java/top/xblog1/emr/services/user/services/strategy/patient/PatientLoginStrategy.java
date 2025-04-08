package top.xblog1.emr.services.user.services.strategy.patient;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;

import top.xblog1.emr.services.user.common.constant.UserExecuteStrategyContant;
import top.xblog1.emr.services.user.common.enums.UserOperationTypeEnum;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.strategy.AbstractUserExecuteStrategy;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 患者登录策略
 */
@Component
public class PatientLoginStrategy extends AbstractUserExecuteStrategy {
    public String mark(){
        return UserTypeEnum.PATIENT.code()+ UserExecuteStrategyContant.USER_LOGIN_STRATEGY_SUFFIX;
    }
    @PostConstruct
    protected void init() {
        // 获取当前类的所有方法
        Method[] methods = this.getClass().getDeclaredMethods();

        // 过滤并缓存方法
        Arrays.stream(methods)
                .filter(method -> method.getParameterCount() == 1 && method.getParameterTypes()[0] == BaseUserDTO.class)
                .forEach(method -> methodCache.put(method.getName(), method));
    }
}
