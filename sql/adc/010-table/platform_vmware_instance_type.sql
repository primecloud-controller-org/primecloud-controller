create table PLATFORM_VMWARE_INSTANCE_TYPE (
    INSTANCE_TYPE_NO bigint(20) not null auto_increment,
    PLATFORM_NO bigint(20) not null,
    INSTANCE_TYPE_NAME varchar(100) not null,
    CPU int(10) not null,
    MEMORY bigint(20) not null,
    constraint PLATFORM_VMWARE_INSTANCE_TYPE_PK1 primary key (INSTANCE_TYPE_NO)
);