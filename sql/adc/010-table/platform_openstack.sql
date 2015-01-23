create table PLATFORM_OPENSTACK (
    PLATFORM_NO bigint(20) not null,
    URL varchar(500) not null,
    NETWORK_ID varchar(40),
    TENANT_ID varchar(40),
    TENANT_NM varchar(100),
    AVAILABILITY_ZONE varchar(100),
    constraint PLATFORM_OPENSTACK_PK primary key (PLATFORM_NO)
);