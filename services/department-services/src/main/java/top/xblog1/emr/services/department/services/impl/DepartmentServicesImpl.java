package top.xblog1.emr.services.department.services.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xblog1.emr.framework.starter.cache.DistributedCache;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.database.toolkit.PageUtil;
import top.xblog1.emr.framework.starter.designpattern.chain.AbstractChainContext;
import top.xblog1.emr.services.department.common.enums.DepartmentChainMarkEnum;
import top.xblog1.emr.services.department.dao.entity.DepartmentDO;
import top.xblog1.emr.services.department.dao.mapper.DepartmentMapper;
import top.xblog1.emr.services.department.dto.req.DepartmentInsertReqDTO;
import top.xblog1.emr.services.department.dto.req.DepartmentPageQueryReqDTO;
import top.xblog1.emr.services.department.dto.req.DepartmentUpdateReqDTO;
import top.xblog1.emr.services.department.dto.resp.DepartmentQueryRespDTO;
import top.xblog1.emr.services.department.dto.resp.DepartmentUpdateRespDTO;
import top.xblog1.emr.services.department.services.DepartmentServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static top.xblog1.emr.services.department.common.constant.RedisKeyConstant.*;
import static top.xblog1.emr.services.department.common.enums.DepartmentCreateErrorCodeEnum.*;

/**
 * 这张表很少更新，但是经常会被查询，尤其是全表查询
 * 对于数据要永久存储到redis中，
 * 采用版本号机制控制数据一致性，并设置版本号过期时间作为兜底
 * 数据库更新只有管理员有权限，不需要上锁
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServicesImpl implements DepartmentServices {
    // todo 由于涉及缓存，需要加锁

    private final DepartmentMapper departmentMapper;
    private final AbstractChainContext abstractChainContext;
    private final DistributedCache distributedCache;
    private final RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(DepartmentInsertReqDTO requestParam) {
        // 数据校验
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        abstractChainContext.handler(DepartmentChainMarkEnum.DEPARTMENT_CREATE_FILTER.name(),requestParam);
        DepartmentDO departmentDO = BeanUtil.convert(requestParam, DepartmentDO.class);
        //log.info(departmentDO.toString());

        RLock lock = redissonClient.getLock(LOCK_DEPARTMENT_INFO_KEY);
        boolean tryLock = lock.tryLock();
        if (!tryLock) {
            throw new ServiceException("系统错误");
        }
        try {
            //插入数据
            departmentMapper.insert(departmentDO);
            //删除版本号
            instance.delete(DEPARTMENT_INFO_VERSION_CONTROLLER);
        }catch (DuplicateKeyException ex){
            throw new ClientException(HAS_NAME);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException(DEPARTMENT_CREATE_FAIL);
        }finally{
            lock.unlock();
        }
        //log.info("部门{}创建完成",departmentDO);
        return departmentDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        if(id==null){
            throw new ClientException(ID_NOTNULL);
        }
        RLock lock = redissonClient.getLock(LOCK_DEPARTMENT_INFO_KEY);
        boolean tryLock = lock.tryLock();
        if (!tryLock) {
            throw new ServiceException("系统错误");
        }
        try {
            //先数据库，再删缓存，因为数据库操作时间长，缓存操作时间短，这样可以尽量保证数据一致性
            instance.delete(DEPARTMENT_INFO_KEY_PREFIX+id);
            //再删除数据库 手动将更新时间置为null
            DepartmentDO departmentDO = departmentMapper.selectById(id);
            departmentDO.setUpdateTime(null);
            departmentMapper.updateById(departmentDO);
            departmentMapper.deleteById(id);
            //再删除版本号
            instance.delete(DEPARTMENT_INFO_VERSION_CONTROLLER);
        } catch (Exception e) {
            throw new ServiceException(DEPARTMENT_DELETE_FAIL);
        }finally {
            lock.unlock();
        }

    }

    @Override
    public DepartmentUpdateRespDTO update(DepartmentUpdateReqDTO requestParam) {
        //abstractChainContext.handler(DepartmentChainMarkEnum.DEPARTMENT_UPDATE_FILTER.name(),requestParam);
        if(requestParam.getId()==null){
            throw new ClientException(ID_NOTNULL);
        } else if (Objects.equals(requestParam.getCode(), "")) {
            requestParam.setId(null);
        } else if (Objects.equals(requestParam.getName(), "")) {
            requestParam.setName(null);
        }else if(Objects.equals(requestParam.getDetail(), "")){
            requestParam.setDetail(null);
        } else if (Objects.equals(requestParam.getAddress(), "")) {
            requestParam.setAddress(null);
        }
        //先更新数据库
        DepartmentDO departmentDO = BeanUtil.convert(requestParam, DepartmentDO.class);
        RLock lock = redissonClient.getLock(LOCK_DEPARTMENT_INFO_KEY);
        boolean tryLock = lock.tryLock();
        if (!tryLock) {
            throw new ServiceException("系统错误");
        }
        try{

            int update = departmentMapper.updateById(departmentDO);
            if(update == 0){
                throw new ClientException(ID_NOTNULL);
            }
            //再删除缓存
            StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
            instance.delete(DEPARTMENT_INFO_KEY_PREFIX+departmentDO.getId());
            // 删除版本号
            instance.delete(DEPARTMENT_INFO_VERSION_CONTROLLER);
        }catch (DuplicateKeyException ex){
            throw new ServiceException("更新失败");
        }finally {
            lock.unlock();
        }
        return BeanUtil.convert(departmentDO, DepartmentUpdateRespDTO.class);
    }

    @Override
    public DepartmentQueryRespDTO queryById(Long id) {
        if(id==null){
            throw new ClientException(ID_NOTNULL);
        }
        //先查询缓存
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        String cacheString = instance.opsForValue().get(DEPARTMENT_INFO_KEY_PREFIX + id);
        DepartmentDO bean = JSONUtil.toBean(cacheString, DepartmentDO.class);
        if (cacheString != null && !cacheString.isEmpty()) {
            return BeanUtil.convert(bean, DepartmentQueryRespDTO.class);
        }
        //如何缓存没有再查询数据库，并存入缓存
        DepartmentDO departmentDO = departmentMapper.selectById(id);
        if(departmentDO==null){
            throw new ServiceException(CANNOT_FIND_DEPARTMENT);
        }
        instance.opsForValue().set(DEPARTMENT_INFO_KEY_PREFIX + id, JSONUtil.toJsonStr(departmentDO));
        return BeanUtil.convert(departmentDO, DepartmentQueryRespDTO.class);

    }

    @Override
    public List<DepartmentQueryRespDTO> queryAll() {
        //先检测计数器和全量缓存是否一致
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        //获取redis中的全量数据
        Set<String> keys = instance.keys(DEPARTMENT_INFO_KEY_PREFIX.concat("*"));
        //获取版本号
        String version = distributedCache.get(DEPARTMENT_INFO_VERSION_CONTROLLER, String.class);
        //如果版本号与keys不一致或者版本号不存在
        if(version==null||version.isEmpty()||!version.equals(String.valueOf(keys.size()))){
            RLock lock = redissonClient.getLock(LOCK_DEPARTMENT_INFO_KEY);
            boolean tryLock = lock.tryLock();
            if (!tryLock) {
                throw new ServiceException("系统错误");
            }
            List<DepartmentDO> departmentDOS;
            try {
                //查询数据库
                departmentDOS = departmentMapper.selectList(Wrappers.lambdaQuery(DepartmentDO.class));
                //批量删除
                distributedCache.delete(keys);
                //将数据库存入redis
                departmentDOS.forEach(department -> {
                    distributedCache.put(DEPARTMENT_INFO_KEY_PREFIX + department.getId(), JSONUtil.toJsonStr(department));
                });
                //把版本号填入redis，设置版本号过期时间，保证定期执行缓存更新操作，作为兜底策略
                distributedCache.put(DEPARTMENT_INFO_VERSION_CONTROLLER, departmentDOS.size(), 30, TimeUnit.MINUTES);
            }catch (DuplicateKeyException ex){
                throw new ServiceException("系统执行错误");
            }finally {
                lock.unlock();
            }
             //返回值
            return BeanUtil.convert(departmentDOS, DepartmentQueryRespDTO.class);
        }
        //如果版本号与keys数量一致，直接返回
        List<DepartmentDO> result = new ArrayList<>();
        keys.forEach(key -> {
            String s = instance.opsForValue().get(key);
            result.add(JSONUtil.toBean(s, DepartmentDO.class));
        });
        return BeanUtil.convert(result, DepartmentQueryRespDTO.class);
    }

    @Override
    public PageResponse<DepartmentQueryRespDTO> pageQuery(DepartmentPageQueryReqDTO requestParam) {
        LambdaQueryWrapper<DepartmentDO> departmentWrapper = Wrappers.lambdaQuery(DepartmentDO.class);
        if(requestParam.getCode()!=null&& !requestParam.getCode().isEmpty())
            departmentWrapper.like(DepartmentDO::getCode, requestParam.getCode());
        if(requestParam.getName()!=null&& !requestParam.getName().isEmpty())
            departmentWrapper.like(DepartmentDO::getName, requestParam.getName());
        if(requestParam.getAddress()!=null&& !requestParam.getAddress().isEmpty())
            departmentWrapper.like(DepartmentDO::getAddress, requestParam.getAddress());
        departmentWrapper.orderByDesc(DepartmentDO::getUpdateTime);
        IPage<DepartmentDO> departmentDOIpage = departmentMapper.selectPage(PageUtil.convert(requestParam), departmentWrapper);
        return PageUtil.convert(departmentDOIpage , each ->{
            return BeanUtil.convert(each, DepartmentQueryRespDTO.class);
        });
    }
}
