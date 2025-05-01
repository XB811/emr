package top.xblog1.emr.services.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.xblog1.emr.framework.starter.base.constant.UserConstant;
import top.xblog1.emr.framework.starter.cache.DistributedCache;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.services.gateway.config.UserServicesPermissionConfig;
import top.xblog1.emr.services.gateway.toolkit.JWTUtil;
import top.xblog1.emr.services.gateway.toolkit.UserInfoDTO;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 *
 */
@Component
@Slf4j
public class UserServicesPermissionsVerifyFilter extends AbstractGatewayFilterFactory<UserServicesPermissionsVerifyFilter.Config> {

    @Autowired
    private  UserServicesPermissionConfig permissionConfig;
    @Autowired
    private DistributedCache distributedCache;


    public UserServicesPermissionsVerifyFilter() {
        super(UserServicesPermissionsVerifyFilter.Config.class);
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
            UserInfoDTO userInfo = distributedCache.get(token, UserInfoDTO.class);
            ServerHttpRequest.Builder builder;
            //用于传入权限校验的userInfo
            UserInfoDTO permissionsVerifyUserInfo = new UserInfoDTO();
            //如果token不存在
            if (!validateToken(userInfo)) {
                //构建一个ServerHttpRequest 其中userType写为访客
                builder = exchange.getRequest().mutate().headers(httpHeaders -> {
                    httpHeaders.set(UserConstant.USER_TYPE_KEY, UserTypeEnum.GUEST.code());
                });
                //这里写为访客是为了后边的权限校验校验
                permissionsVerifyUserInfo.setUserType(UserTypeEnum.GUEST.code());
            }else {
                //将由token解析得到的数据存入请求header中
                builder = exchange.getRequest().mutate().headers(httpHeaders -> {
                    httpHeaders.set(UserConstant.USER_ID_KEY, userInfo.getUserId());
                    httpHeaders.set(UserConstant.USER_NAME_KEY, userInfo.getUsername());
                    httpHeaders.set(UserConstant.REAL_NAME_KEY, URLEncoder.encode(userInfo.getRealName(), StandardCharsets.UTF_8));
                    httpHeaders.set(UserConstant.USER_TYPE_KEY, userInfo.getUserType());
                    httpHeaders.set(UserConstant.USER_TOKEN_KEY, token);
                });
                BeanUtil.convert(userInfo, permissionsVerifyUserInfo);
            }
            ServerWebExchange serverWebExchange = exchange.mutate().request(builder.build()).build();
            // 做权限校验
            if (!permissionsVerify(permissionsVerifyUserInfo,request)) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.setComplete();
            }
            return chain.filter(serverWebExchange);
        };
    }



    private Boolean permissionsVerify(UserInfoDTO userInfoDTO, ServerHttpRequest request) {
        Map<String, Map<String, List<String>>> pathPermissions =
                permissionConfig.getPathPermissions();
        // 没有配置权限规则，根据严格模式决定
        if (pathPermissions == null || pathPermissions.isEmpty()) {
            return !permissionConfig.isStrictMode(); // 非严格模式下允许访问
        }
        String operatorUserType = userInfoDTO.getUserType();
        //如果是根用户，更改用户类型为root
        if(Objects.equals(operatorUserType, UserTypeEnum.ADMIN.code()) && Objects.equals(userInfoDTO.getUsername(), "root")){
            operatorUserType="root";
        }
        //从路径中 获取被操作用户类型
        String twoUserType =getTwoUserType(request.getURI().getPath());

        //获取请求uri
        String apiPath = getApiUrl(request.getURI().getPath());
        //检测登录
        List<String> allowUrls = pathPermissions.get(operatorUserType).get(twoUserType);
        for(String allowUrl : allowUrls) {
            if (apiPath.startsWith(allowUrl)) {
                return true; // 找到匹配的权限
            }
        }
        // 没有找到匹配的路径规则，根据严格模式决定
        return !permissionConfig.isStrictMode();
    }

    private String getTwoUserType(String path){
        if (path == null || path.isEmpty()) {
            return UserTypeEnum.GUEST.code();
        }
        int slashCount = 0;
        int beginIndex=0;
        int endIndex=0;
        for (int i=0;i<path.length();i++) {
            if (path.charAt(i) == '/') {
                slashCount++;
                if(slashCount==5){//找到第五个斜杠后，设置beginindex
                    beginIndex = i+1;
                }
                if (slashCount >= 6) { // 找到第6个斜杠后

                    break;
                }
            }
            endIndex=i;
        }
        return slashCount < 5 ? UserTypeEnum.GUEST.code() : path.substring(beginIndex, endIndex + 1);
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
        return "UserServicesFilter";
    }
}
