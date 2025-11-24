create table address
(
    id             bigint auto_increment comment '地址ID'
        primary key,
    user_id        bigint                             not null comment '用户ID',
    receiver_name  varchar(50)                        not null comment '收货人姓名',
    phone          varchar(20)                        not null comment '收货人电话',
    province       varchar(50)                        not null comment '省份',
    city           varchar(50)                        not null comment '城市',
    district       varchar(50)                        not null comment '区/县',
    detail_address varchar(255)                       not null comment '详细地址',
    is_default     tinyint  default 0                 not null comment '是否默认地址(1:是,0:否)',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '收货地址表';

create index idx_user_id
    on address (user_id);

create table `order`
(
    id                 bigint auto_increment comment '订单ID'
        primary key,
    order_no           varchar(50)                        not null comment '订单编号',
    user_id            bigint                             not null comment '用户ID',
    product_id         bigint                             not null comment '商品ID',
    seckill_product_id bigint                             not null comment '秒杀商品ID',
    product_name       varchar(200)                       not null comment '商品名称',
    quantity           int                                not null comment '购买数量',
    total_amount       decimal(10, 2)                     not null comment '订单总金额',
    status             tinyint  default 0                 not null comment '订单状态(0:待支付,1:已支付,2:已取消)',
    image_url          varchar(255)                       null comment '商品图片URL',
    created_at         datetime default CURRENT_TIMESTAMP null comment '创建时间',
    payment_time       datetime                           null comment '支付时间',
    update_time        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_order_no
        unique (order_no)
)
    comment '订单表';

create index idx_seckill_product_id
    on `order` (seckill_product_id);

create index idx_status
    on `order` (status);

create index idx_user_id
    on `order` (user_id);

create table payment
(
    id            bigint auto_increment comment 'ID'
        primary key,
    order_no      varchar(50)                        not null comment '订单编号',
    user_id       bigint                             not null comment '用户ID',
    payment_type  tinyint                            not null comment '支付方式(1:微信,2:支付宝)',
    amount        decimal(10, 2)                     not null comment '支付金额',
    paid          tinyint  default 0                 not null comment '是否支付(1:是,0:否)',
    payment_time  datetime                           null comment '支付时间',
    callback_data text                               null comment '支付回调数据',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '支付记录表';

create index idx_order_no
    on payment (order_no);

create index idx_user_id
    on payment (user_id);

create table product
(
    id             bigint auto_increment comment '商品ID'
        primary key,
    name           varchar(200)                       not null comment '商品名称',
    description    text                               null comment '商品描述',
    original_price decimal(10, 2)                     not null comment '原价',
    image_url      varchar(255)                       null comment '商品图片URL',
    status         tinyint  default 1                 not null comment '状态(1:正常,0:下架)',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '商品表';

create table seckill_product
(
    id              bigint auto_increment comment '秒杀商品ID'
        primary key,
    product_id      bigint                             not null comment '关联商品ID',
    seckill_price   decimal(10, 2)                     not null comment '秒杀价格',
    stock           int                                not null comment '总库存',
    available_stock int                                not null comment '可用库存',
    start_time      datetime                           not null comment '秒杀开始时间',
    end_time        datetime                           not null comment '秒杀结束时间',
    status          tinyint  default 0                 not null comment '状态(0:未开始,1:进行中,2:已结束)',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '秒杀商品表';

create index idx_product_id
    on seckill_product (product_id);

create index idx_status
    on seckill_product (status);

create index idx_time
    on seckill_product (start_time, end_time);

create table user
(
    id          bigint auto_increment comment '用户ID'
        primary key,
    username    varchar(50)                        not null comment '用户名',
    password    varchar(100)                       not null comment '密码(加密存储)',
    nickname    varchar(50)                        null comment '昵称',
    phone       varchar(20)                        null comment '手机号',
    email       varchar(100)                       null comment '邮箱',
    avatar      varchar(255)                       null comment '头像URL',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_email
        unique (email),
    constraint uk_username
        unique (username)
)
    comment '用户表';

create table user_seckill_record
(
    id                 bigint auto_increment comment 'ID'
        primary key,
    user_id            bigint                             not null comment '用户ID',
    seckill_product_id bigint                             not null comment '秒杀商品ID',
    create_time        datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint uk_user_seckill
        unique (user_id, seckill_product_id) comment '唯一索引防止重复抢购'
)
    comment '用户秒杀记录表';


