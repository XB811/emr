package top.xblog1.emr.services.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "emr.general-services")
public class GeneralServicesPermissionConfig {
    /**
     * 各角色专属路径配置
     * key: 路径前缀, value: 允许访问的用户类型列表
     */
    private Map<String, List<String>> pathPermissions = new HashMap<>();

    /**
     * 是否启用严格权限模式
     * true: 没有明确允许的路径将被拒绝
     * false: 没有明确配置的路径将被允许
     */
    private boolean strictMode = false;
}
