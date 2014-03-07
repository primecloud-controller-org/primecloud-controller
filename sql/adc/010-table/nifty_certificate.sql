create table NIFTY_CERTIFICATE (
    USER_NO bigint not null,
    PLATFORM_NO bigint not null,
    CERTIFICATE varchar(10240) not null,
    PRIVATE_KEY varchar(10240) not null,
    constraint NIFTY_CERTIFICATE_PK primary key(USER_NO, PLATFORM_NO)
);
