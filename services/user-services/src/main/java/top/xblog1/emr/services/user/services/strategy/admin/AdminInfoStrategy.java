package top.xblog1.emr.services.user.services.strategy.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.xblog1.emr.framework.starter.cache.DistributedCache;
import org.springframework.stereotype.Component;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.framework.starter.designpattern.chain.AbstractChainContext;
import top.xblog1.emr.services.user.common.constant.UserExecuteStrategyContant;
import top.xblog1.emr.services.user.common.enums.UserChainMarkEnum;
import top.xblog1.emr.services.user.dao.entity.AdminDO;
import top.xblog1.emr.services.user.dao.mapper.AdminMapper;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;
import top.xblog1.emr.services.user.dto.req.UserUpdateReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryRespDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.strategy.AbstractUserExecuteStrategy;
import top.xblog1.emr.services.user.toolkit.PasswordEncryptUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.*;
import static top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum.HAS_PHONE;
import static top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum.HAS_USERNAME_NOTNULL;


/**
 * 管理员信息策略
 */
@Component
@RequiredArgsConstructor
public class AdminInfoStrategy extends AbstractUserExecuteStrategy {
    private final AdminMapper adminMapper;
    private final DistributedCache distributedCache;

    public String mark(){
        return UserTypeEnum.ADMIN.code() + UserExecuteStrategyContant.USER_INFO_STRATEGY_SUFFIX;
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
    public void update(BaseUserDTO baseUserDTO) {
        //拆包
        UserUpdateReqDTO requestParam = baseUserDTO.getUserUpdateReqDTO();
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        //查询用户
        baseUserDTO.setId(Long.valueOf(requestParam.getId()));
        String phone = queryActualUserByID(baseUserDTO)
                .getUserQueryActualRespDTO()
                .getPhone();
        //更新redis中的手机号
        if(requestParam.getPhone()!=null&&!Objects.equals(phone, requestParam.getPhone())){
            instance.opsForSet().remove(USER_REGISTER_PHONE_ADMIN, phone);
            Long add = instance.opsForSet().add(USER_REGISTER_PHONE_ADMIN, requestParam.getPhone());
            if(add==null||add==0){
                throw new ClientException(HAS_PHONE);
            }
        }
        AdminDO adminDO = BeanUtil.convert(requestParam, AdminDO.class);
        //密码加密
        adminDO.setPassword(PasswordEncryptUtil.encryptPassword(requestParam.getPassword()));
        LambdaUpdateWrapper<AdminDO> adminUpdateWrapper = Wrappers.lambdaUpdate(AdminDO.class)
                .eq(AdminDO::getId, adminDO.getId());
        adminMapper.update(adminDO, adminUpdateWrapper);
    }

    public BaseUserDTO queryActualUserByID(BaseUserDTO baseUserDTO) {
        //拆包
        Long userId = baseUserDTO.getId();
        LambdaQueryWrapper<AdminDO> queryWrapper = Wrappers.lambdaQuery(AdminDO.class)
                .eq(AdminDO::getId, userId);
        AdminDO adminDO = adminMapper.selectOne(queryWrapper);
        if (adminDO == null) {
            throw new ClientException("用户ID不存在或已注销，请检查用户名是否正确");
        }
        //封包返回
        return BaseUserDTO.builder()
                .userQueryActualRespDTO(BeanUtil.convert(adminDO, UserQueryActualRespDTO.class))
                .build();
    }
}
