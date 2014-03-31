create table VMWARE_NETWORK (
    NETWORK_NO bigint not null auto_increment,
    NETWORK_NAME varchar(100) not null,
    PLATFORM_NO bigint not null,
    VLAN_ID int,
    VSWITCH_NAME varchar(100),
    FARM_NO bigint,
    PUBLIC_NETWORK boolean not null,
    constraint VMWARE_NETWORK_PK primary key(NETWORK_NO)
);
