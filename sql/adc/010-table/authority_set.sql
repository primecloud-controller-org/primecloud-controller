create table AUTHORITY_SET (
    SET_NO bigint(20) not null auto_increment,
    SET_NAME varchar(30),
    FARM_USE boolean,
    SERVER_MAKE boolean,
    SERVER_DELETE boolean,
    SERVER_OPERATE boolean,
    SERVICE_MAKE boolean,
    SERVICE_DELETE boolean,
    SERVICE_OPERATE boolean,
    LB_MAKE boolean,
    LB_DELETE boolean,
    LB_OPERATE boolean,
    constraint AUTHORITY_SET_PK primary key(SET_NO)
);

