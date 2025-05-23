## 依赖

java17

## 数据库结构

#### 管理员表

admin

| 字段          | 类型         | 空 | 默认    | 注释   |
|-------------|------------|---|-------|------|
| id          | bigint(20) |   |       | 主键   |
| username    | varchar(256) |   |       | 用户名  |
| password    | varchar(512) |  |       | 密码   |
| real_name   | varchar(256) |   |       | 姓名   |
| phone       | varchar(128) |   |       | 电话   |
| create_time | datetime   |   |       | 新增时间 |
| update_time | datetime   |   |       | 更新时间 |
| del_flag    | bool       |   | false | 删除标识 |

#### 患者表

patient

| 字段          | 类型         | 空 | 默认    | 注释   |
|-------------|------------|---|-------|------|
| id          | bigint(20) |   |       | 主键   |
| username    | varchar(256) |   |       | 用户名  |
| password    | varchar(512) |  |       | 密码   |
| real_name   | varchar(256) |   |       | 姓名   |
| phone       | varchar(128) |   |       | 电话   |
| gender      | tinyint    |   |       | 性别   |
| id_card | varchar(256) |   |       | 身份证号 |
| create_time | datetime   |   |       | 新增时间 |
| update_time | datetime   |   |       | 更新时间 |
| del_flag    | bool       |   | false | 删除标识 |

#### 医生表
doctor

| 字段            | 类型         | 空 | 默认    | 注释              |
|---------------|------------|---|-------|-----------------|
| id            | bigint(20) |   |       | 主键              |
| username      | varchar(256) |   |       | 用户名/医生工号/系统自动分配 |
| password      | varchar(512) |  |       | 密码              |
| real_name     | varchar(256) |   |       | 姓名              |
| gender        | tinyint    |   |       | 性别              |
| phone         | varchar(128) |   |       | 手机号             |
| department_id | bigint     |   |       | 科室id            |
| title         | varchar(50)|   |       | 职称              |
| specialty     | text       |   |       | 专业方向            |
| create_time   | datetime   |   |       | 新增时间            |
| update_time   | datetime   |   |       | 更新时间            |
| del_flag      | bool       |   | false | 删除标识            |

#### 科室表

department

| 字段          | 类型         | 空 | 默认    | 注释      |
|-------------|------------|---|-------|---------|
| id          | bigint(20) |   |       | 科室id/主键 |
| name        | varchar(50)|   |       | 科室名称    |
| describe    | text       |   |       | 科室介绍    |
| address    | varchar(256)|  |       | 科室位置    |
| create_time | datetime   |   |       | 新增时间    |
| update_time | datetime   |   |       | 更新时间    |
| del_flag    | bool       |   | false | 删除标识    |

#### 电子病历表

emr

| 字段            | 类型        | 空   | 默认  | 注释              |
| --------------- | ----------- | ---- | ----- | ----------------- |
| id              | bigint(20)  |      |       | 主键              |
| user_id         | bigint      |      |       | 患者id            |
| user_real_name  | varchar(256) |      |       | 患者姓名          |
| age             | int         |      |       | 患者年龄          |
| department_id   | bigint      |      |       | 科室id            |
| department_name | varchar(50) |      |       | 科室名            |
| content         | text        |      |       | 主诉/病情关键信息 |
| present_history | text        |      |       | 现病史/病历详情   |
| past_history    | text        |      |       | 既往史            |
| allergy_history | text        |      |       | 药敏史            |
| diagnosis       | text        |      |       | 诊断/检测结果     |
| treatment_plan  | text        |      |       | 治疗方案          |
| doctor_advice   | text        |      |       | 医嘱              |
| doctor_id       | bigint      |      |       | 医生id            |
| doctor_name     | varchar(50) |      |       | 医生姓名          |
| create_time     | datetime    |      |       | 新增时间/出诊时间 |
| update_time     | datetime    |      |       | 更新时间          |
| del_flag        | bool        |      | false | 删除标识          |

#### 预约时间管理表（医生预约挂号信息管理）
booking


| 字段           | 类型        | 空   | 默认  | 注释                      |
| -------------- | ----------- | ---- | ----- | ------------------------- |
| id             | bigint(20)  |      |       | 主键                      |
| doctor_id      | bigint      |      |       | 医生id                    |
| available_time | varchar(50) |      |       | 可预约时间/14位二进制表示 |
| is_available   | bool        |      | false | 当前医生是否可用          |
| create_time    | datetime    |      |       | 新增时间                  |
| update_time    | datetime    |      |       | 更新时间                  |
| del_flag       | bool        |      | false | 删除标识                  |

#### 用户挂号管理表

registration

| 字段             | 类型        | 空   | 默认  | 注释       |
| ---------------- | ----------- | ---- | ----- | ---------- |
| id               | bigint(20)  |      |       | 主键       |
| user_id          | bigint      |      |       | 患者id     |
| doctor_id        | bigint      |      |       | 医生id     |
| appointment_date | date        |      |       | 预约日期   |
| appointment_time | varchar(20) |      |       | 预约时间段 |
| create_time      | datetime    |      |       | 新增时间   |
| update_time      | datetime    |      |       | 更新时间   |
| del_flag         | bool        |      | false | 删除标识   |

#### 就诊评价表

evaluation

| 字段        | 类型       | 空   | 默认  | 注释       |
| ----------- | ---------- | ---- | ----- | ---------- |
| id          | bigint(20) |      |       | 主键       |
| user_id     | bigint     |      |       | 患者id     |
| doctor_id   | bigint     |      |       | 医生id     |
| emr_id      | bigint     |      |       | 电子病历id |
| content     | text       |      |       | 评价内容   |
| create_time | datetime   |      |       | 新增时间   |
| update_time | datetime   |      |       | 更新时间   |
| del_flag    | bool       |      | false | 删除标识   |

#### 公告表

notice

| 字段          | 类型         | 空 | 默认    | 注释    |
|-------------|------------|---|-------|-------|
| id          | bigint(20) |   |       | 主键    |
| admin_id    | bigint     |   |       | 管理员id |
| title       | varchar(100)|  |       | 公告标题  |
| content     | text       |   |       | 公告内容  |
| create_time | datetime   |   |       | 新增时间  |
| update_time | datetime   |   |       | 更新时间  |
| del_flag    | bool       |   | false | 删除标识  |

#### 权限表

permissions

| 字段        | 类型         | 空   | 默认  | 注释                         |
| ----------- | ------------ | ---- | ----- | ---------------------------- |
| id          | bigint(20)   |      |       | 主键                         |
| url         | varchar(512) |      |       | 请求路径                     |
| license     | bigint       |      |       | 使用二进制存在不同用户的权限 |
| create_time | datetime     |      |       | 新增时间                     |
| update_time | datetime     |      |       | 更新时间                     |
| del_flag    | bool         |      | false | 删除标识                     |


## 实体关系图

![image-20250402101931300](http://qnimg.xblog1.top/typora/image-20250402101931300.png)

## 后端设计

![image-20250402134619383](http://qnimg.xblog1.top/typora/image-20250402134619383.png)
![image-20250402134159497](http://qnimg.xblog1.top/typora/image-20250402134159497.png)

## 开发规范

#### git日志提交

- **feat**: 新功能（feature）
- **fix**: 修复 bug
- **docs**: 文档变更
- **style**: 代码风格变动（不影响代码逻辑）
- **refactor**: 代码重构（既不是新增功能也不是修复bug的代码更改）
- **perf**: 性能优化
- **test**: 添加或修改测试
- **chore**: 杂项（构建过程或辅助工具的变动）
- **build**: 构建系统或外部依赖项的变更
- **ci**: 持续集成配置的变更
- **revert**: 回滚

## RESTful API 设计
在RESTful API设计中，增删改查(CRUD)操作对应的四种HTTP请求方式如下：

1. **增(Create)** - **POST**
2. **删(Delete)** - **DELETE**
3. **改(Update)** - **PUT/PATCH**
4. **查(Read)** - **GET**

这些注解在Spring MVC中用于映射HTTP请求到控制器方法。





```
├─dependencies //依赖包统一管理
├─frameworks//公告组件封装，如UserConstant ，Result，全局异常处理器，设计模式等
│  ├─base
│  ├─bizs
│  ├─cache
│  ├─common
│  ├─convention
│  ├─//······
└─services
    ├─department-services
    |-//······
    └─user-services
        ├─src
        │  ├─main
        │  │  ├─java
        │  │  │  └─top.xblog1.emr.services.user
        │  │  │                      ├─common//常量
        │  │  │                      ├─config
        │  │  │                      ├─controller
        │  │  │                      ├─dao
        │  │  │                      │  ├─entity
        │  │  │                      │  └─mapper
        │  │  │                      ├─dto
        │  │  │                      │  ├─req
        │  │  │                      │  ├─resp
        │  │  │                      │  └─strategy
        │  │  │                      ├─services
        │  │  │                      │  ├─handler//责任链校验
        │  │  │                      │  │  └─filter
        │  │  │                      │  │      └─user
        │  │  │                      │  ├─impl
        │  │  │                      │  └─strategy//策略模式
        │  │  │                      │      ├─admin
        │  │  │                      │      ├─doctor
        │  │  │                      │      └─patient
        │  │  │                      └─toolkit
        │  │  └─resources
```

