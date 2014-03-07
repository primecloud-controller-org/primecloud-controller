create table COMPONENT_TYPE (
    COMPONENT_TYPE_NO bigint(20) not null auto_increment,
    COMPONENT_TYPE_NAME varchar(100) not null,
    COMPONENT_TYPE_NAME_DISP varchar(300) not null,
    LAYER varchar(100) not null,
    LAYER_DISP varchar(300) not null,
    RUN_ORDER int(10) not null,
    SELECTABLE tinyint(1) not null,
    ZABBIX_TEMPLATE varchar(100),
    ADDRESS_URL varchar(100),
    constraint COMPONENT_TYPE_PK primary key(COMPONENT_TYPE_NO)
);