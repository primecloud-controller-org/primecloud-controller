create table PLATFORM_AWS (
    PLATFORM_NO bigint(20) not null,
    HOST varchar(500) not null,
    PORT int(10) not null,
    SECURE tinyint(1) not null,
    EUCA tinyint(1) not null,
    VPC tinyint(1) not null,
    REGION varchar(50),
    AVAILABILITY_ZONE varchar(300),
    VPC_ID varchar(30),
    constraint PLATFORM_AWS_PK primary key (PLATFORM_NO)
);