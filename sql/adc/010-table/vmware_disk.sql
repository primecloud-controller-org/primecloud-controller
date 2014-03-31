create table VMWARE_DISK (
    DISK_NO bigint not null auto_increment,
    FARM_NO bigint not null,
    PLATFORM_NO bigint not null,
    COMPONENT_NO bigint,
    INSTANCE_NO bigint,
    SIZE int,
    SCSI_ID int,
    ATTACHED boolean,
    DATASTORE varchar(100),
    FILE_NAME varchar(200),
    constraint VMWARE_DISK_PK primary key(DISK_NO)
);
