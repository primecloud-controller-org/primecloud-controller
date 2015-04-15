create table NIFTY_VOLUME (
    VOLUME_NO bigint not null auto_increment,
    FARM_NO bigint not null,
    VOLUME_NAME varchar(100) not null,
    PLATFORM_NO bigint not null,
    COMPONENT_NO bigint,
    INSTANCE_NO bigint,
    SIZE int,
    VOLUME_ID varchar(20),
    STATUS varchar(20),
    INSTANCE_ID varchar(20),
    SCSI_ID INT(11),
    constraint NIFTY_VOLUME_PK primary key(VOLUME_NO)
);
