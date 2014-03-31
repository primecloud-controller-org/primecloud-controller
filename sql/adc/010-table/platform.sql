create table PLATFORM (
    PLATFORM_NO bigint(20) not null auto_increment,
    PLATFORM_NAME varchar(100) not null,
    PLATFORM_NAME_DISP varchar(300) not null,
    PLATFORM_SIMPLENAME_DISP varchar(200) not null,
    INTERNAL tinyint(1) not null,
    PROXY tinyint(1) not null,
    PLATFORM_TYPE varchar(100) not null,
    SELECTABLE tinyint(1) not null,
    constraint PLATFORM_PK primary key(PLATFORM_NO)
);