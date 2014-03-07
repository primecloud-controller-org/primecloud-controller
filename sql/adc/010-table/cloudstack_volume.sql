create table CLOUDSTACK_VOLUME (
    VOLUME_NO bigint not null auto_increment,
    FARM_NO bigint not null,
    PLATFORM_NO bigint not null,
    COMPONENT_NO bigint,
    INSTANCE_NO bigint,
    VOLUME_ID varchar(20),
    DEVICEID varchar(20),
    DISKOFFERINGID varchar(20),
    NAME varchar(100),
    SIZE int,
    SNAPSHOTID varchar(20),
    STATE varchar(20),
    INSTANCE_ID varchar(20),
    ZONEID varchar(100),
    HYPERVISOR varchar(100),
    constraint CLOUDSTACK_VOLUME_PK primary key(VOLUME_NO)
);
