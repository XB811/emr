/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.xblog1.emr.services.user.common.enums;

import lombok.AllArgsConstructor;
import top.xblog1.emr.framework.starter.convention.errorcode.IErrorCode;

/**
 * 用户注册错误码枚举
 */
@AllArgsConstructor
public enum UserRegisterErrorCodeEnum implements IErrorCode {

    USER_REGISTER_FAIL("A001000", "用户注册失败"),

    USER_NAME_NOTNULL("A001001", "用户名不能为空"),

    PASSWORD_NOTNULL("A001002", "密码不能为空"),

    PHONE_NOTNULL("A001003", "手机号不能为空"),

    USER_TYPE_NOTNULL("A001004","用户类型不能为空"),

    ID_CARD_NOTNULL("A001005", "证件号不能为空"),

    HAS_USERNAME_NOTNULL("A001006", "用户名已存在"),

    PHONE_REGISTERED("A001007", "手机号已被占用"),

    REAL_NAME_NOTNULL("A001015", "真实姓名不能为空"),
    USER_TYPE_ERROR("A001016","用户类型错误"),
    DEPARTMENT_ID_NOTNULL("A001017","部门id不能为空"),

    TITLE_NOTNULL("A001018", "职称不能为空"),
    SPECIALTY_NOTNULL("A001019", "专业方向不能为空"),
    GENDER_NOTNULL("A001020", "性别不能为空"),
    HAS_PHONE("A001021","手机号已经注册"),
    PHONE_PATTERN_ERROR("A001022","手机号格式错误"),
    ID_CARD_PATTERN_ERROR("A001023","身份证号格式错误"),
    ;

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误提示消息
     */
    private final String message;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
