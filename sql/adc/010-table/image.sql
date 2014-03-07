create table IMAGE (
    IMAGE_NO bigint(20) not null auto_increment,
    PLATFORM_NO bigint(20) not null,
    IMAGE_NAME varchar(100) not null,
    IMAGE_NAME_DISP varchar(300) not null,
    OS varchar(100) not null,
    OS_DISP varchar(300) not null,
    SELECTABLE tinyint(1) not null,
    COMPONENT_TYPE_NOS varchar(500) not null,
    ZABBIX_TEMPLATE varchar(100),
    constraint IMAGE_PK primary key(IMAGE_NO)
);