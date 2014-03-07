create table CLOUDSTACK_ADDRESS (
    ADDRESS_NO bigint not null auto_increment,
    ACCOUNT bigint not null,
    PLATFORM_NO bigint not null,
    INSTANCE_NO bigint,
    INSTANCE_ID varchar(20),
    ADDRESS_ID varchar(20),
    IPADDRESS varchar(100),
    NETWORKID varchar(20),
    STATE varchar(20),
    ZONEID varchar(100),
    constraint CLOUDSTACK_ADDRESS_PK primary key(ADDRESS_NO)
);
