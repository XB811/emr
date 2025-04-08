package top.xblog1.emr.framework.starter.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 操作类型
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum OperationTypeEnum {

    SAVE("save","新增"),

    DELETE("delete","删除"),

    UPDATE("update","更新"),

    QUERY_BY_ID("queryById","根据ID查询");
    private final String code;
    private final String desc;
}
