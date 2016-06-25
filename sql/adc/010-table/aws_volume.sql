create table AWS_VOLUME (
    VOLUME_NO bigint not null auto_increment,
    FARM_NO bigint not null,
    VOLUME_NAME varchar(30) not null,
    PLATFORM_NO bigint not null,
    COMPONENT_NO bigint,
    INSTANCE_NO bigint,
    SIZE int,
    SNAPSHOT_ID varchar(30),
    AVAILABILITY_ZONE varchar(100),
    DEVICE varchar(20),
    VOLUME_ID varchar(30),
    STATUS varchar(20),
    INSTANCE_ID varchar(30),
    constraint AWS_VOLUME_PK primary key(VOLUME_NO)
);
