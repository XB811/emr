package top.xblog1.emr.services.user.services.strategy.doctor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.xblog1.emr.framework.starter.cache.DistributedCache;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.database.toolkit.PageUtil;
import top.xblog1.emr.framework.starter.user.core.UserContext;
import top.xblog1.emr.services.user.common.constant.UserExecuteStrategyContant;
import top.xblog1.emr.services.user.dao.entity.AdminDO;
import top.xblog1.emr.services.user.dao.entity.DoctorDO;
import top.xblog1.emr.services.user.dao.mapper.DoctorMapper;
import top.xblog1.emr.services.user.dto.req.UpdatePasswordReqDTO;
import top.xblog1.emr.services.user.dto.req.UserPageQueryReqDTO;
import top.xblog1.emr.services.user.dto.req.UserResetPasswordReqDTO;
import top.xblog1.emr.services.user.dto.req.UserUpdateReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryRespDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.strategy.AbstractUserExecuteStrategy;
import top.xblog1.emr.services.user.toolkit.PasswordEncryptUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.USER_LOGIN_PHONE_VERIFY_CODE_PREFIX;
import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.USER_REGISTER_PHONE_DOCTOR;
import static top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum.HAS_PHONE;

/**
 * 医生信息策略
 */
@Component
@RequiredArgsConstructor
public class DoctorInfoStrategy extends AbstractUserExecuteStrategy {
    private final DoctorMapper doctorMapper;
    private final DistributedCache distributedCache;
    public String mark(){
        return UserTypeEnum.DOCTOR.code()+ UserExecuteStrategyContant.USER_INFO_STRATEGY_SUFFIX;
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
    /*
    更新用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(BaseUserDTO baseUserDTO) {
        //拆包
        UserUpdateReqDTO requestParam = baseUserDTO.getUserUpdateReqDTO();
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        //查询用户
        baseUserDTO.setId(Long.valueOf(requestParam.getId()));
        UserQueryActualRespDTO userQueryActualRespDTO = queryActualUserByID(baseUserDTO)
                .getUserQueryActualRespDTO();
        String phone = userQueryActualRespDTO.getPhone();
        //更新redis中的手机号
        if(requestParam.getPhone()!=null&&!Objects.equals(phone, requestParam.getPhone())){
            instance.opsForSet().remove(USER_REGISTER_PHONE_DOCTOR, phone);
            Long add = instance.opsForSet().add(USER_REGISTER_PHONE_DOCTOR, requestParam.getPhone());
            if(add==null||add==0){
                throw new ClientException(HAS_PHONE);
            }
        }
        DoctorDO doctorDO = BeanUtil.convert(requestParam, DoctorDO.class);
        //密码加密
        // 这里只有admin可以更改密码
        if(UserContext.getUserType().equals(UserTypeEnum.ADMIN.code()))
            if(!Objects.equals(requestParam.getPassword(), "")&&requestParam.getPassword() !=null)
                doctorDO.setPassword(PasswordEncryptUtil.encryptPassword(requestParam.getPassword()));
        LambdaUpdateWrapper<DoctorDO> doctorUpdateWrapper = Wrappers.lambdaUpdate(DoctorDO.class)
                .eq(DoctorDO::getId, doctorDO.getId());
        doctorMapper.update(doctorDO, doctorUpdateWrapper);
    }
    /*
    根据id查询用户未脱敏信息
     */
    public BaseUserDTO queryActualUserByID(BaseUserDTO baseUserDTO) {
        //拆包
        Long userId = baseUserDTO.getId();
        LambdaQueryWrapper<DoctorDO> queryWrapper = Wrappers.lambdaQuery(DoctorDO.class)
                .eq(DoctorDO::getId, userId);
        DoctorDO doctorDO = doctorMapper.selectOne(queryWrapper);
        if (doctorDO == null) {
            throw new ClientException("用户ID不存在或已注销，请检查用户名是否正确");
        }
        UserQueryActualRespDTO convert = BeanUtil.convert(doctorDO, UserQueryActualRespDTO.class);
        //封包返回
        return BaseUserDTO.builder()
                .userQueryActualRespDTO(convert)
                .build();
    }

    public void updatePassword(BaseUserDTO request){
        UpdatePasswordReqDTO requestParam = request.getUpdatePasswordReqDTO();
        if(requestParam.getOldPassword() ==null || requestParam.getOldPassword().isEmpty()){
            throw new ClientException("旧密码不能为空");
        }else if(requestParam.getNewPassword() ==null || requestParam.getNewPassword().isEmpty()){
            throw new ClientException("新密码不能为空");
        }else if(requestParam.getConfirmPassword() ==null || requestParam.getConfirmPassword().isEmpty()) {
            throw new ClientException("重复密码不能为空");
        }else if(requestParam.getNewPassword().length()<6|| requestParam.getNewPassword().length()>16){
            throw new ClientException("新密码的长度为6-16位之间");
        }else if(!requestParam.getNewPassword().equals(requestParam.getConfirmPassword())){
            throw new ClientException("重复密码和新密码输入不一致");
        }
        LambdaQueryWrapper<DoctorDO> queryWrapper = Wrappers.lambdaQuery(DoctorDO.class)
                .eq(DoctorDO::getId, UserContext.getUserId());
        DoctorDO doctorDO = null;
        try {
            doctorDO = doctorMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            throw new ClientException("当前登录用户信息丢失");
        }
        if(doctorDO==null){
            throw new ClientException("当前登录用户信息丢失");
        }
        if(!PasswordEncryptUtil.verifyPassword(requestParam.getOldPassword(),doctorDO.getPassword())){
            throw new ClientException("旧密码错误");
        }
        doctorDO.setPassword(PasswordEncryptUtil.encryptPassword(requestParam.getNewPassword()));
        doctorMapper.updateById(doctorDO);
    }

    public BaseUserDTO pageQuery(BaseUserDTO request){
        //拆包
        UserPageQueryReqDTO requestParam = request.getUserPageQueryReqDTO();
        //分页查询
        LambdaQueryWrapper<DoctorDO> queryWrapper = Wrappers.lambdaQuery(DoctorDO.class);
        if(requestParam.getPhone() !=null&& !requestParam.getPhone().isEmpty())
            queryWrapper.like(DoctorDO::getPhone,requestParam.getPhone());
        if(requestParam.getUsername()!=null&& !requestParam.getUsername().isEmpty())
                queryWrapper.like(DoctorDO::getUsername,requestParam.getUsername());
        if(requestParam.getRealName()!=null&& !requestParam.getRealName().isEmpty())
                queryWrapper.like(DoctorDO::getRealName,requestParam.getRealName());
        if(requestParam.getDepartmentId()!=null)
            queryWrapper.eq(DoctorDO::getDepartmentId,requestParam.getDepartmentId());
        queryWrapper.orderByDesc(DoctorDO::getUpdateTime);
        IPage<DoctorDO> doctorDOIPage =doctorMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        PageResponse<UserQueryRespDTO> response = PageUtil.convert(doctorDOIPage, each -> {

            return BeanUtil.convert(each, UserQueryRespDTO.class);
        });
        return BaseUserDTO.builder()
                .userPageQueryRespDTO(response)
                .build();

    }
    // TODO 修改为redis存储
    public BaseUserDTO queryAll(BaseUserDTO request){
        List<DoctorDO> doctorDOS = doctorMapper.selectList(Wrappers.lambdaQuery(DoctorDO.class));
        List<UserQueryRespDTO> convert = BeanUtil.convert(doctorDOS, UserQueryRespDTO.class);
        return BaseUserDTO.builder().userQueryRespDTOList(convert).build();
    }

    public void resetPassword(BaseUserDTO request){
        UserResetPasswordReqDTO requestParam = request.getUserResetPasswordReqDTO();
        //先查数据库拿到用户信息
        LambdaQueryWrapper<DoctorDO> queryWrapper = Wrappers.lambdaQuery(DoctorDO.class)
                .eq(DoctorDO::getPhone,requestParam.getPhone());
        DoctorDO doctorDO = doctorMapper.selectOne(queryWrapper);
        if(doctorDO==null){
            throw new ClientException("该用户不存在");
        }
        //再查缓存拿到验证码
        String cacheCode = distributedCache.get(USER_LOGIN_PHONE_VERIFY_CODE_PREFIX + UserTypeEnum.DOCTOR.code(), String.class);
        //如果验证码不同
        if(!cacheCode.equals(requestParam.getCode()))
            throw new ClientException("验证码错误");
        //如果相同，更新密码
        doctorDO.setPassword(PasswordEncryptUtil.encryptPassword(requestParam.getPassword()));
        doctorMapper.updateById(doctorDO);
    }
}
