package top.xblog1.emr.services.user.services.impl;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.designpattern.chain.AbstractChainContext;
import top.xblog1.emr.framework.starter.designpattern.strategy.AbstractStrategyChoose;
import top.xblog1.emr.services.user.common.enums.UserOperationTypeEnum;
import top.xblog1.emr.services.user.dto.req.UpdatePasswordReqDTO;
import top.xblog1.emr.services.user.dto.req.UserPageQueryReqDTO;
import top.xblog1.emr.services.user.dto.req.UserUpdateReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryRespDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.UserService;

import java.util.List;

import static top.xblog1.emr.services.user.common.constant.UserExecuteStrategyContant.USER_INFO_STRATEGY_SUFFIX;
import static top.xblog1.emr.services.user.common.enums.UserOperationTypeEnum.USER_PAGE_QUERY;
import static top.xblog1.emr.services.user.common.enums.UserOperationTypeEnum.USER_QUERY_ALL;

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
        //TODO 用户更新，手机号更新应该单独抽离出来新的接口，并使用短信验证码服务单独更新
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

    @Override
    public PageResponse<UserQueryRespDTO> pageQuery(UserPageQueryReqDTO requestParam, String userType) {
        BaseUserDTO request =BaseUserDTO.builder()
                .userPageQueryReqDTO(requestParam)
                .operationType(USER_PAGE_QUERY)
                .build();
        BaseUserDTO response = strategyChoose.chooseAndExecuteResp(userType+USER_INFO_STRATEGY_SUFFIX,request);
        return response.getUserPageQueryRespDTO();
    }

    @Override
    public List<UserQueryRespDTO> queryAll(String userType) {
        BaseUserDTO request =BaseUserDTO.builder()
                .operationType(USER_QUERY_ALL)
                .build();
        BaseUserDTO response = strategyChoose.chooseAndExecuteResp(userType+USER_INFO_STRATEGY_SUFFIX,request);
        return response.getUserQueryRespDTOList();
    }
}
