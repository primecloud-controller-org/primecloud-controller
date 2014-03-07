create table LOAD_BALANCER (
    LOAD_BALANCER_NO bigint not null auto_increment,
    FARM_NO bigint not null,
    LOAD_BALANCER_NAME varchar(30) not null,
    COMMENT varchar(100),
    FQDN varchar(100),
    PLATFORM_NO bigint not null,
    TYPE varchar(20),
    ENABLED boolean,
    STATUS varchar(20),
    COMPONENT_NO bigint not null,
    CANONICAL_NAME varchar(100),
    CONFIGURE boolean,
    constraint LOAD_BALANCER_PK primary key(LOAD_BALANCER_NO)
);
