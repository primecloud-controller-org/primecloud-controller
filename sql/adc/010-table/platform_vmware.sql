create table PLATFORM_VMWARE (
    PLATFORM_NO bigint(20) not null,
    URL varchar(500) not null,
    USERNAME varchar(100) not null,
    PASSWORD varchar(100) not null,
    DATACENTER varchar(300) not null,
    PUBLIC_NETWORK varchar(300) not null,
    PRIVATE_NETWORK varchar(300) not null,
    COMPUTE_RESOURCE varchar(300) not null,
    constraint PLATFORM_VMWARE_PK primary key (PLATFORM_NO)
);