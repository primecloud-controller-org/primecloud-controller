create table FARM (
    FARM_NO bigint not null auto_increment,
    USER_NO bigint not null,
    FARM_NAME varchar(30) not null,
    COMMENT varchar(100),
    DOMAIN_NAME varchar(100),
    SCHEDULED boolean,
    COMPONENT_PROCESSING boolean,
    constraint FARM_PK primary key(FARM_NO)
);
