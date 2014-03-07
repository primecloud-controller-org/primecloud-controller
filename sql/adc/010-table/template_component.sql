create table TEMPLATE_COMPONENT (
    TEMPLATE_COMPONENT_NO bigint(20) not null auto_increment,
    TEMPLATE_COMPONENT_NAME varchar(50) not null,
    TEMPLATE_NO bigint(20) not null,
    COMPONENT_TYPE_NO bigint(20) not null,
    COMMENT varchar(100),
    DISK_SIZE int(10) not null,
    ASSOCIATE varchar(1000),
    constraint TEMPLATE_COMPONENT_PK primary key(TEMPLATE_COMPONENT_NO)
);