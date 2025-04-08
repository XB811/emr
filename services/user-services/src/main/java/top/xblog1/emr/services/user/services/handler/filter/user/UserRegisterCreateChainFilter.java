package top.xblog1.emr.services.user.services.handler.filter.user;

import top.xblog1.emr.framework.starter.designpattern.chain.AbstractChainHandler;
import top.xblog1.emr.services.user.common.enums.UserChainMarkEnum;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;

/**
 * 用户注册责任链过滤器
 */
public interface UserRegisterCreateChainFilter<T extends UserRegisterReqDTO> extends AbstractChainHandler<UserRegisterReqDTO> {

    @Override
    default String mark() {
        return UserChainMarkEnum.USER_REGISTER_FILTER.name();
    }
}