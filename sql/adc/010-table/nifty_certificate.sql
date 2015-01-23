create table NIFTY_CERTIFICATE (
    USER_NO bigint not null,
    PLATFORM_NO bigint not null,
    NIFTY_ACCESS_ID varchar(100) not null,
    NIFTY_SECRET_KEY varchar(100) not null,
    constraint NIFTY_CERTIFICATE_PK primary key(USER_NO, PLATFORM_NO)
);
