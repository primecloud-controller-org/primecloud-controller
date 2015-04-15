create table AZURE_CERTIFICATE (
    USER_NO bigint not null,
    PLATFORM_NO bigint not null,
    SUBSCRIPTION_ID varchar(100) not null,
    CERTIFICATE varchar(2000) not null,
    DEFAULT_SUBNET_ID varchar(30),
    KEY_NAME varchar(100),
    KEY_PUBLIC varchar(1000),
    constraint AZURE_CERTIFICATE_PK primary key(USER_NO, PLATFORM_NO)
);
