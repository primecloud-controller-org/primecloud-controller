create table INSTANCE (
    INSTANCE_NO bigint not null auto_increment,
    FARM_NO bigint not null,
    INSTANCE_NAME varchar(30) not null,
    PLATFORM_NO bigint not null,
    IMAGE_NO bigint not null,
    ENABLED boolean,
    COMMENT varchar(100),
    FQDN varchar(100),
    INSTANCE_CODE varchar(30),
    PUBLIC_IP varchar(100),
    PRIVATE_IP varchar(100),
    STATUS varchar(20),
    PROGRESS int,
    COODINATE_STATUS varchar(20),
    LOAD_BALANCER boolean,
    constraint INSTANCE_PK primary key(INSTANCE_NO)
);
