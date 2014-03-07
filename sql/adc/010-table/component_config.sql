create table COMPONENT_CONFIG (
    CONFIG_NO bigint not null auto_increment,
    COMPONENT_NO bigint not null,
    CONFIG_NAME varchar(50) not null,
    CONFIG_VALUE varchar(200),
    constraint COMPONENT_CONFIG_PK primary key(CONFIG_NO)
);
