package top.xblog1.emr.services.department.services.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static top.xblog1.emr.services.department.common.constant.RedisKeyConstant.DEPARTMENT_INFO_KEY_PREFIX;
import static top.xblog1.emr.services.department.common.enums.DepartmentCreateErrorCodeEnum.*;

/**
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServicesImpl implements DepartmentServices {

    private final DepartmentMapper departmentMapper;
    private final AbstractChainContext abstractChainContext;
    private final DistributedCache distributedCache;
    private final Mapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(DepartmentInsertReqDTO requestParam) {
        // 数据校验
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        abstractChainContext.handler(DepartmentChainMarkEnum.DEPARTMENT_CREATE_FILTER.name(),requestParam);
        DepartmentDO departmentDO = BeanUtil.convert(requestParam, DepartmentDO.class);
        //log.info(departmentDO.toString());
        try {
            departmentMapper.insert(departmentDO);
        }catch (DuplicateKeyException ex){
            throw new ClientException(HAS_NAME);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException(DEPARTMENT_CREATE_FAIL);
        }
        instance.opsForValue().set(DEPARTMENT_INFO_KEY_PREFIX + departmentDO.getId(), JSONUtil.toJsonStr(departmentDO),30, TimeUnit.MINUTES);

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
        try {
            //先删除redis
            instance.delete(DEPARTMENT_INFO_KEY_PREFIX+id);
            //再删除数据库 手动将更新时间置为null
            DepartmentDO departmentDO = departmentMapper.selectById(id);
            departmentDO.setUpdateTime(null);
            departmentMapper.updateById(departmentDO);
            departmentMapper.deleteById(id);
            //再删除
            instance.delete(DEPARTMENT_INFO_KEY_PREFIX+id);
        } catch (Exception e) {
            throw new ServiceException(DEPARTMENT_DELETE_FAIL);
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
        int update = departmentMapper.updateById(departmentDO);
        if(update == 0){
            throw new ClientException(ID_NOTNULL);
        }
        //再删除缓存
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        instance.delete(DEPARTMENT_INFO_KEY_PREFIX+departmentDO.getId());
        DepartmentQueryRespDTO result = queryById(departmentDO.getId());
        return BeanUtil.convert(result, DepartmentUpdateRespDTO.class);
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
        instance.opsForValue().set(DEPARTMENT_INFO_KEY_PREFIX + id, JSONUtil.toJsonStr(departmentDO),30, TimeUnit.MINUTES);
        return BeanUtil.convert(departmentDO, DepartmentQueryRespDTO.class);

    }

    @Override
    public List<DepartmentQueryRespDTO> queryAll() {
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        Set<String> keys = instance.keys(DEPARTMENT_INFO_KEY_PREFIX.concat("*"));
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
