package top.xblog1.emr.services.gateway.filter;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import top.xblog1.emr.framework.starter.base.constant.UserConstant;
import top.xblog1.emr.framework.starter.cache.DistributedCache;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.services.gateway.toolkit.UserInfoDTO;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import top.xblog1.emr.services.gateway.config.GeneralServicesPermissionConfig;

/**
 *
 */
@Component
@Slf4j
public class GeneralServicesPermissionsVerifyFilter extends AbstractGatewayFilterFactory<GeneralServicesPermissionsVerifyFilter.Config> {

    @Autowired
    private GeneralServicesPermissionConfig permissionConfig;

    @Autowired
    private DistributedCache distributedCache;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public GeneralServicesPermissionsVerifyFilter() {
        super(Config.class);
    }

    /**
     * 过滤器
     * @param config
     * @return GatewayFilter
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            //获取请求
            ServerHttpRequest request = exchange.getRequest();
            //获取请求路径
            String requestPath = request.getPath().toString();
            log.info("requestPath:{}", requestPath);
            /*
             从请求头Header获取token
              token的名字为 Authorization
            */
            String token = request.getHeaders().getFirst("Authorization");
            // TODO 需要验证 Token 是否有效，有可能用户注销了账户，但是 Token 有效期还未过

            //解析token
//            UserInfoDTO userInfo = JWTUtil.parseJwtToken(token);
            // 修改为从缓存中获取token，方便后续将 JWTUtil 更换为其他的 token 生成方式
            UserInfoDTO userInfo1= null;
            //StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
            if (token != null && !token.isEmpty()){
                // userInfo1 =distributedCache.get(token, UserInfoDTO.class);
//                log.info("userInfoDTO:{}", userInfo1);
                String s = redisTemplate.opsForValue().get(token);
                userInfo1=JSON.parseObject(s, UserInfoDTO.class);
            }

            UserInfoDTO userInfo = userInfo1;
            // 因为后续需要做权限校验，故不做token的校验
            ServerHttpRequest.Builder builder;
            //如果token不存在
            if (!validateToken(userInfo)) {
                //构建一个ServerHttpRequest 其中userType写为访客
                builder = exchange.getRequest().mutate().headers(httpHeaders -> {
                    httpHeaders.set(UserConstant.USER_TYPE_KEY, UserTypeEnum.GUEST.code());
                });
            }else {
                //将由token解析得到的数据存入请求header中
                builder = exchange.getRequest().mutate().headers(httpHeaders -> {
                    httpHeaders.set(UserConstant.USER_ID_KEY, userInfo.getUserId());
                    httpHeaders.set(UserConstant.USER_NAME_KEY, userInfo.getUsername());
                    httpHeaders.set(UserConstant.REAL_NAME_KEY, URLEncoder.encode(userInfo.getRealName(), StandardCharsets.UTF_8));
                    httpHeaders.set(UserConstant.USER_TYPE_KEY, userInfo.getUserType());
                    httpHeaders.set(UserConstant.USER_TOKEN_KEY, token);
                });
            }
            ServerWebExchange serverWebExchange = exchange.mutate().request(builder.build()).build();
            // 做权限校验
            if (!permissionsVerify(serverWebExchange)) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.setComplete();
            }
            return chain.filter(serverWebExchange);
        };
    }

    // 权限校验管理
    private Boolean permissionsVerify(ServerWebExchange serverWebExchange){
        Map<String, List<String>> pathPermissions = permissionConfig.getPathPermissions();
        // 没有配置权限规则，根据严格模式决定
        if (pathPermissions == null || pathPermissions.isEmpty()) {
            return !permissionConfig.isStrictMode(); // 非严格模式下允许访问
        }
        //获取用户类型
        String operatorUserType =serverWebExchange.getRequest().getHeaders().getFirst(UserConstant.USER_TYPE_KEY);
        //如果是管理员，直接放行
        //TODO gateway 这里先给了管理员全部的全部的权限，后续可用根据需求关闭权限
        if(operatorUserType.equals(UserTypeEnum.ADMIN.code())){
            return true;
        }
        //获取请求uri
        String apiUrl = getApiUrl(serverWebExchange.getRequest().getPath().toString());

        //log.info("apiUrl:{}", apiUrl);
        //权限校验并返回
        for(Map.Entry<String,List<String>> entry : pathPermissions.entrySet()){
            String pathPattern = entry.getKey();
            List<String> allowedTypes = entry.getValue();
            if(apiUrl.startsWith(pathPattern)){
                return allowedTypes.contains(operatorUserType);
            }
        }
        // 没有找到匹配的路径规则，根据严格模式决定
        return !permissionConfig.isStrictMode();
    }
    /**
    * 获取接口路径
    * @param path
    * @return String
    */
    private String getApiUrl(String path){
        if (path == null || path.isEmpty()) {
            return "";
        }

        int slashCount = 0;
        int endIndex = 0;

        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') {
                slashCount++;
                if (slashCount >= 5) { // 找到第五个斜杠后的位置
                    break;
                }
            }
            endIndex = i;
        }

        // 如果斜杠数量少于5个，返回整个路径
        return slashCount < 5 ? path : path.substring(0, endIndex + 1);
    }
    /**
     * 验证token不为空
     * @param userInfo
     * @return boolean
     */
    private boolean validateToken(UserInfoDTO userInfo) {
        return userInfo != null;
    }
    public static class Config{
    }

    //这个name方法 用来在yml配置中指定对应的过滤器名称
    @Override
    public String name() {
        return "GeneralServicesFilter";
    }
}
