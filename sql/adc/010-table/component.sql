create table COMPONENT (
    COMPONENT_NO bigint(20) not null auto_increment,
    FARM_NO bigint(20) not null,
    COMPONENT_NAME varchar(100) not null,
    COMPONENT_TYPE_NO bigint(20) not null,
    COMMENT varchar(100),
    LOAD_BALANCER tinyint(1),
    constraint COMPONENT_PK primary key(COMPONENT_NO)
);
