package top.xblog1.emr.services.user.services.strategy;

import jakarta.annotation.PostConstruct;
import top.xblog1.emr.framework.starter.convention.exception.AbstractException;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.framework.starter.designpattern.strategy.AbstractExecuteStrategy;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户模块策略控制通用方法抽取
 */

public class AbstractUserExecuteStrategy implements AbstractExecuteStrategy<BaseUserDTO,BaseUserDTO> {

    // 缓存 Method 对象
    protected final ConcurrentHashMap<String, Method> methodCache = new ConcurrentHashMap<>();

    // 有返回值
    public BaseUserDTO executeResp(BaseUserDTO requestParam) {
        try {
            Method method = getCachedMethod(requestParam.getOperationType().code());
            return (BaseUserDTO) method.invoke(this, requestParam);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if(cause instanceof AbstractException) {
                throw (AbstractException) cause;
            }else{
                throw new ServiceException("方法"+ite.getStackTrace()[0].getMethodName()+"调用失败"); // 处理其他异常
            }
        } catch(Exception e) {
            throw new ServiceException("方法调用失败"+e.getMessage());
        }
    }

    // 无返回值
    public void execute(BaseUserDTO requestParam) {
        try {
            Method method = getCachedMethod(requestParam.getOperationType().code());
            method.invoke(this, requestParam);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if(cause instanceof AbstractException) {
                throw (AbstractException) cause;
            }else{
                throw new ServiceException("方法"+ite.getStackTrace()[0].getMethodName()+"调用失败"+cause); // 处理其他异常
            }
        } catch (Exception e) {
            throw new ServiceException("方法调用失败"+e.getMessage());
        }
    }

    // 获取缓存的 Method 对象
    protected Method getCachedMethod(String methodName) {
        return methodCache.computeIfAbsent(methodName, key -> {
            try {
                return this.getClass().getMethod(key, BaseUserDTO.class);
            } catch (NoSuchMethodException e) {
                throw new ServiceException("方法未找到: " + key);
            }
        });
    }
}
