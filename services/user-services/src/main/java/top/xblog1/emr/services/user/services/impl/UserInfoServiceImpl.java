package top.xblog1.emr.services.user.services.impl;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.designpattern.chain.AbstractChainContext;
import top.xblog1.emr.framework.starter.designpattern.strategy.AbstractStrategyChoose;
import top.xblog1.emr.framework.starter.user.core.UserContext;
import top.xblog1.emr.services.user.common.enums.UserOperationTypeEnum;
import top.xblog1.emr.services.user.dto.req.UpdatePasswordReqDTO;
import top.xblog1.emr.services.user.dto.req.UserUpdateReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryRespDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.UserService;

import static top.xblog1.emr.services.user.common.constant.UserExecuteStrategyContant.USER_INFO_STRATEGY_SUFFIX;

/**
 * 用户信息管理实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoServiceImpl implements UserService {
    private final AbstractStrategyChoose strategyChoose;
    private final AbstractChainContext abstractChainContext;
    /**
    * 根据username修改用户
    * @param requestParam
    * @return
    */
    @Override
    public void update(UserUpdateReqDTO requestParam) {
        /**
        * 对更新参数做参数校验
         * 和注册校验判断要求相同
        */
//        abstractChainContext.handler(UserChainMarkEnum.USER_REGISTER_FILTER.name(),
//                BeanUtil.convert(requestParam, UserRegisterReqDTO.class));
        BaseUserDTO request = BaseUserDTO.builder()
                .userUpdateReqDTO(requestParam)
                .operationType(UserOperationTypeEnum.USER_UPDATE)
                .build();
        strategyChoose.chooseAndExecute(requestParam.getUserType()+ USER_INFO_STRATEGY_SUFFIX
                , request);
    }

    /**
    * 根据用户id和用户类型查询用户信息
    * @param id 
     * @param userType 
    * @return UserQueryRespDTO 
    */
    @Override
    public UserQueryRespDTO queryUserByIDAndUserType(Long id, String userType) {
        return BeanUtil.convert(queryActualUserByIDAndUserType(id, userType), UserQueryRespDTO.class);
    }

    /**
    * 根据用户id和用户类型查询用户无脱敏信息
    * @param id 
     * @param userType 
    * @return UserQueryActualRespDTO 
    */
    @Override
    public UserQueryActualRespDTO queryActualUserByIDAndUserType(Long id, String userType) {
        BaseUserDTO request = BaseUserDTO.builder()
                .id(id)
                .operationType(UserOperationTypeEnum.USER_QUERY_ACTUAL_USER_BY_ID)
                .build();
        BaseUserDTO response = strategyChoose.chooseAndExecuteResp(userType+ USER_INFO_STRATEGY_SUFFIX
                , request);
        return response.getUserQueryActualRespDTO();
    }

    @Override
    public void updatePassword(UpdatePasswordReqDTO requestParam, @NotEmpty String userType) {
        BaseUserDTO request =BaseUserDTO.builder()
                .updatePasswordReqDTO(requestParam)
                .operationType(UserOperationTypeEnum.USER_PASSWORD_UPDATE)
                .build();
        strategyChoose.chooseAndExecute(userType+USER_INFO_STRATEGY_SUFFIX,
                request);
    }
}
