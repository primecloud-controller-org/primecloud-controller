create table AWS_ADDRESS (
    ADDRESS_NO bigint not null auto_increment,
    USER_NO bigint not null,
    PLATFORM_NO bigint not null,
    PUBLIC_IP varchar(100),
    COMMENT varchar(100),
    INSTANCE_NO bigint,
    INSTANCE_ID varchar(30),
    constraint AWS_ADDRESS_PK primary key(ADDRESS_NO)
);
