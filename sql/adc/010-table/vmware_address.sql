create table VMWARE_ADDRESS (
    ADDRESS_NO bigint not null auto_increment,
    PLATFORM_NO bigint not null,
    IP_ADDRESS varchar(100),
    SUBNET_MASK varchar(100),
    DEFAULT_GATEWAY varchar(100),
    USER_NO bigint,
    INSTANCE_NO bigint,
    ENABLED boolean,
    ASSOCIATED boolean,
    constraint VMWARE_ADDRESS_PK primary key(ADDRESS_NO)
);
