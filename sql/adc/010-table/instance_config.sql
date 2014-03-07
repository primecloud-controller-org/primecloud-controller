create table INSTANCE_CONFIG (
    CONFIG_NO bigint not null auto_increment,
    INSTANCE_NO bigint not null,
    COMPONENT_NO bigint not null,
    CONFIG_NAME varchar(50) not null,
    CONFIG_VALUE varchar(200),
    constraint INSTANCE_CONFIG_PK primary key(CONFIG_NO)
);
