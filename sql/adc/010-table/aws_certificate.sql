create table AWS_CERTIFICATE (
    USER_NO bigint not null,
    PLATFORM_NO bigint not null,
    AWS_ACCESS_ID varchar(100) not null,
    AWS_SECRET_KEY varchar(100) not null,
    DEF_KEYPAIR varchar(100),
    DEF_SUBNET varchar(30),
    DEF_LB_SUBNET varchar(100),
    constraint AWS_CERTIFICATE_PK primary key(USER_NO, PLATFORM_NO)
);
