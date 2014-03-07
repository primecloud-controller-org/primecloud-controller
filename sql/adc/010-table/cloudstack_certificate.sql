create table CLOUDSTACK_CERTIFICATE (
    ACCOUNT bigint not null,
    PLATFORM_NO bigint not null,
    CLOUDSTACK_ACCESS_ID varchar(100) not null,
    CLOUDSTACK_SECRET_KEY varchar(100) not null,
    DEF_KEYPAIR varchar(100),
    constraint CLOUDSTACK_CERTIFICATE_PK primary key(ACCOUNT, PLATFORM_NO)
);
