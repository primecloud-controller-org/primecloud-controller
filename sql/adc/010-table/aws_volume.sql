create table AWS_VOLUME (
    VOLUME_NO bigint not null auto_increment,
    FARM_NO bigint not null,
    VOLUME_NAME varchar(30) not null,
    PLATFORM_NO bigint not null,
    COMPONENT_NO bigint,
    INSTANCE_NO bigint,
    SIZE int,
    SNAPSHOT_ID varchar(20),
    AVAILABILITY_ZONE varchar(100),
    DEVICE varchar(20),
    VOLUME_ID varchar(20),
    STATUS varchar(20),
    INSTANCE_ID varchar(20),
    constraint AWS_VOLUME_PK primary key(VOLUME_NO)
);
