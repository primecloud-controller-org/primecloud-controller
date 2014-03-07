create table PLATFORM_CLOUDSTACK (
    PLATFORM_NO bigint(20) not null,
    HOST varchar(300) not null,
    PATH varchar(500) not null,
    PORT int(10) not null,
    SECURE tinyint(1) not null,
    ZONE_ID varchar(100) not null,
    NETWORK_ID varchar(500) not null,
    TIMEOUT int(10) not null,
    DEVICE_TYPE varchar(20) not null,
    HOST_ID varchar(200),
    constraint PLATFORM_CLOUDSTACK_PK primary key (PLATFORM_NO)
);