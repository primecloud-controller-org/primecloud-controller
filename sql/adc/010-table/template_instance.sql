create table TEMPLATE_INSTANCE (
    TEMPLATE_INSTANCE_NO bigint(20) not null auto_increment,
    TEMPLATE_INSTANCE_NAME varchar(50) not null,
    TEMPLATE_NO bigint(20) not null,
    PLATFORM_NO bigint(20) not null,
    IMAGE_NO bigint(20) not null,
    COMMENT varchar(100),
    INSTANCE_TYPE varchar(100) not null,
    constraint TEMPLATE_INSTANCE_PK primary key(TEMPLATE_INSTANCE_NO)
);