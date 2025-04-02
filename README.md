## 数据库结构

#### 管理员表

| 字段          | 类型   | 空 | 默认    | 注释   |
|-------------|------|---|-------|------|
| id          |      |   |       | 主键   |
| username    |      |   |       | 用户名  |
| password    |      |   |       | 密码   |
| real_name   |      |   |       | 姓名   |
| phone       |      |   |       | 电话   |
| create_time |      |   |       | 新增时间 |
| update_time |      |   |       | 更新时间 |
| delete      | bool |   | false | 是否删除 |

#### 用户表

| 字段          | 类型   | 空 | 默认    | 注释   |
|-------------|------|---|-------|------|
| id          |      |   |       | 主键   |
| username    |      |   |       | 用户名  |
| password    |      |   |       | 密码   |
| real_name   |      |   |       | 姓名   |
| phone       |      |   |       | 电话   |
| gender      |      |   |       | 性别   |
| id_number   |      |   |       | 身份证号 |
| create_time |      |   |       | 新增时间 |
| update_time |      |   |       | 更新时间 |
| delete      | bool |   | false | 是否删除 |

#### 医生表

| 字段            | 类型   | 空 | 默认    | 注释              |
|---------------|------|---|-------|-----------------|
| id            |      |   |       | 主键              |
| username      |      |   |       | 用户名/医生工号/系统自动分配 |
| password      |      |   |       | 密码              |
| real_name     |      |   |       | 姓名              |
| gender        |      |   |       | 性别              |
| phone         |      |   |       | 手机号             |
| department_id |      |   |       | 科室id            |
| title         |      |   |       | 职称              |
| specialty     |      |   |       | 专业方向            |
| create_time   |      |   |       | 新增时间            |
| update_time   |      |   |       | 更新时间            |
| delete        | bool |   | false | 是否删除            |

#### 科室表

department

| 字段          | 类型   | 空 | 默认    | 注释      |
|-------------|------|---|-------|---------|
| id          |      |   |       | 科室id/主键 |
| name        |      |   |       | 科室名称    |
| describe    |      |   |       | 科室介绍    |
| location    |      |   |       | 科室位置    |
| create_time |      |   |       | 新增时间    |
| update_time |      |   |       | 更新时间    |
| delete      | bool |   | false | 是否删除    |

#### 电子病历表

| 字段              | 类型   | 空 | 默认    | 注释        |
|-----------------|------|---|-------|-----------|
| id              |      |   |       | 主键        |
| user_id         |      |   |       | 患者id      |
| user_real_name  |      |   |       | 患者姓名      |
| age             |      |   |       | 患者年龄      |
| department_id   |      |   |       | 科室id      |
| department_name |      |   |       | 科室名       |
| content         |      |   |       | 主诉/病情关键信息 |
|                 |      |   |       | 现病史/病历详情  |
|                 |      |   |       | 既往史       |
|                 |      |   |       | 药敏史       |
|                 |      |   |       | 诊断/检测结果   |
|                 |      |   |       | 治疗方案      |
|                 |      |   |       | 医嘱        |
| doctor_id       |      |   |       | 医生id      |
| doctor_name     |      |   |       | 医生姓名      |
| create_time     |      |   |       | 新增时间/出诊时间 |
| update_time     |      |   |       | 更新时间      |
| delete          | bool |   | false | 是否删除      |

#### 预约时间管理表（医生预约挂号信息管理）

| 字段          | 类型   | 空 | 默认    | 注释             |
|-------------|------|---|-------|----------------|
| id          |      |   |       | 主键             |
| doctor_id   |      |   |       | 医生id           |
|             |      |   |       | 可预约时间/14位二进制表示 |
|             | bool |   | false | 当前医生是否可用       |
| create_time |      |   |       | 新增时间           |
| update_time |      |   |       | 更新时间           |
| delete      | bool |   | false | 是否删除           |

#### 用户挂号管理表

| 字段          | 类型   | 空 | 默认    | 注释    |
|-------------|------|---|-------|-------|
| id          |      |   |       | 主键    |
| user_id     |      |   |       | 患者id  |
| doctor_id   |      |   |       | 医生id  |
|             |      |   |       | 预约日期  |
|             |      |   |       | 预约时间段 |
| create_time |      |   |       | 新增时间  |
| update_time |      |   |       | 更新时间  |
| delete      | bool |   | false | 是否删除  |

#### 就诊评价表

| 字段          | 类型   | 空 | 默认    | 注释     |
|-------------|------|---|-------|--------|
| id          |      |   |       | 主键     |
| user_id     |      |   |       | 患者id   |
| doctor_id   |      |   |       | 医生id   |
| emr_id      |      |   |       | 电子病历id |
| constant    |      |   |       | 评价内容   |
| create_time |      |   |       | 新增时间   |
| update_time |      |   |       | 更新时间   |
| delete      | bool |   | false | 是否删除   |

#### 公告表

| 字段          | 类型   | 空 | 默认    | 注释    |
|-------------|------|---|-------|-------|
| id          |      |   |       | 主键    |
| admin_id    |      |   |       | 管理员id |
| title       |      |   |       | 公告标题  |
| constant    |      |   |       | 公告内容  |
| create_time |      |   |       | 新增时间  |
| update_time |      |   |       | 更新时间  |
| delete      | bool |   | false | 是否删除  |

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