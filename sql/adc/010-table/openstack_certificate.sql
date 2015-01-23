create table OPENSTACK_CERTIFICATE (
    USER_NO bigint not null,
    PLATFORM_NO bigint not null,
    OS_ACCESS_ID varchar(100) not null,
    OS_SECRET_KEY varchar(100) not null,
    DEF_KEYPAIR varchar(100),
    constraint OPENSTACK_CERTIFICATE_PK primary key(USER_NO, PLATFORM_NO)
);
