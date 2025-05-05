create table admin
(
    id          bigint       not null comment '主键'
        primary key,
    username    varchar(256) not null comment '用户名',
    password    varchar(512) not null comment '密码',
    real_name   varchar(256) null comment '真实姓名',
    phone       varchar(128) null comment '手机号',
    create_time datetime     null comment '新增时间',
    update_time datetime     null comment '更新时间',
    del_flag    bigint       null comment '删除标识',
    constraint admin_pk
        unique (phone, del_flag),
    constraint unique_admin_username
        unique (username)
);

create table booking
(
    id             bigint       not null comment '主键'
        primary key,
    doctor_id      bigint       null comment '医生ID',
    doctor_name    varchar(256) null comment '医生姓名',
    available_time bigint       null comment '可预约时间',
    is_available   tinyint(1)   null comment '当前是否可预约',
    create_time    datetime     null comment '新增时间',
    update_time    datetime     null comment '更新时间',
    del_flag       bigint       null comment '删除标识',
    constraint unique_doctor_id_and_del_flag
        unique (doctor_id, del_flag) comment 'doctor_id + del_flag唯一'
);

create table department
(
    id          bigint       not null comment '主键'
        primary key,
    code        varchar(255) null comment '科室编码',
    name        varchar(255) null comment '科室名称',
    detail      text         null comment '科室介绍',
    address     varchar(255) null comment '科室位置',
    create_time datetime     null comment '新增时间',
    update_time datetime     null comment '更新时间',
    del_flag    bigint       null comment '删除标识',
    constraint unique_code
        unique (code, del_flag),
    constraint unique_name
        unique (name, del_flag)
);

create table doctor
(
    id            bigint       not null comment '主键'
        primary key,
    username      varchar(256) null comment '用户名/工号',
    password      varchar(512) null comment '密码',
    real_name     varchar(256) null comment '姓名',
    gender        tinyint      null comment '性别',
    phone         varchar(128) null comment '手机号',
    department_id bigint       null comment '科室ID',
    title         varchar(256) null comment '职称',
    specialty     text         null comment '专业方向
',
    create_time   datetime     null comment '新增时间',
    update_time   datetime     null comment '更新时间',
    del_flag      bigint       null comment '删除标识',
    constraint doctor_pk
        unique (username),
    constraint unique_doctor_phone
        unique (phone, del_flag)
);

create table emr
(
    id              bigint       not null comment '主键'
        primary key,
    patient_id      bigint       not null comment '患者ID',
    real_name       varchar(255) null comment '患者姓名',
    gender          tinyint(1)   null comment '患者性别',
    age             int          null comment '患者年龄',
    department_id   bigint       null comment '科室ID',
    department_code varchar(255) null comment '科室编号',
    department_name text         null comment '科室名',
    content         text         null comment '主诉/病情关键信息',
    present_history text         null comment '现病史',
    past_history    text         null comment '既往史',
    allergy_history text         null comment '药敏史',
    diagnosis       text         null comment '诊断',
    treatment_plan  text         null comment '治疗方案',
    doctor_advice   text         null comment '医嘱',
    doctor_id       bigint       not null comment '医生ID',
    doctor_name     varchar(255) null comment '医生姓名',
    create_time     datetime     null comment '新增时间',
    update_time     datetime     null comment '更新时间',
    del_flag        tinyint(1)   null comment '删除标识'
);

create table evaluation
(
    id          bigint   not null comment '主键'
        primary key,
    patient_id  bigint   null comment '患者ID',
    doctor_id   bigint   null comment '医生ID',
    emr_id      bigint   null comment '电子病历ID',
    content     text     null comment '评价内容',
    create_time datetime null comment '新增时间',
    update_time datetime null comment '更新时间',
    del_flag    bigint   null comment '删除标识',
    constraint evaluation_pk
        unique (emr_id, del_flag)
);

create table notice
(
    id          bigint       not null comment '主键'
        primary key,
    admin_id    bigint       null comment '管理员id',
    admin_name  varchar(255) null comment '管理员真实姓名',
    title       varchar(255) null comment '公告标题',
    content     text         null comment '公告内容',
    create_time datetime     null comment '新增时间',
    update_time datetime     null comment '更新时间',
    del_flag    tinyint(1)   null comment '删除标识'
);

create table patient
(
    id          bigint       not null comment '主键'
        primary key,
    username    varchar(256) not null comment '用户名',
    password    varchar(512) null comment '密码',
    real_name   varchar(256) null comment '姓名',
    phone       varchar(128) null comment '电话',
    gender      tinyint      null comment '性别',
    id_card     varchar(256) null comment '身份证号',
    create_time datetime     null comment '新增时间',
    update_time datetime     null comment '更新时间',
    del_flag    bigint       null comment '删除标识',
    constraint patient_pk
        unique (username, del_flag),
    constraint patient_pk_2
        unique (phone, del_flag),
    constraint patient_pk_3
        unique (id_card, del_flag)
);

create table patient_phone_reuse
(
    id          bigint       not null
        primary key,
    phone       varchar(128) not null,
    create_time datetime     null,
    update_time datetime     null,
    del_flag    bigint       null,
    constraint patient_phone_reuse_pk
        unique (phone, del_flag)
);

create table registration
(
    id               bigint     not null comment '主键'
        primary key,
    patient_id       bigint     null comment '患者ID',
    doctor_id        bigint     null comment '医生ID',
    appointment_date date       null comment '预约时间',
    appointment_time tinyint    null comment '预约时间段',
    is_finish        tinyint(1) null comment '是否完成',
    create_time      datetime   null comment '新增时间',
    update_time      datetime   null comment '更新时间',
    del_flag         tinyint(1) null comment '删除标识'
);

