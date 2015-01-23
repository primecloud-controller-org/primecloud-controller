create table OPENSTACK_VOLUME (
    VOLUME_NO bigint not null auto_increment,
    FARM_NO bigint not null,
    VOLUME_NAME varchar(100) not null,
    PLATFORM_NO bigint not null,
    COMPONENT_NO bigint,
    INSTANCE_NO bigint,
    SIZE int,
    SNAPSHOT_ID varchar(40),
    AVAILABILITY_ZONE varchar(100),
    DEVICE varchar(40),
    VOLUME_ID varchar(40),
    STATUS varchar(40),
    INSTANCE_ID varchar(40),
    constraint OPENSTACK_VOLUME_PK primary key(VOLUME_NO)
);
