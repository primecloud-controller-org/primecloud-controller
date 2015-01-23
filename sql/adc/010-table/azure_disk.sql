create table AZURE_DISK (
    DISK_NO bigint not null auto_increment,
    FARM_NO bigint not null,
    PLATFORM_NO bigint not null,
    COMPONENT_NO bigint,
    INSTANCE_NO bigint,
    DISK_NAME varchar(100),
    INSTANCE_NAME varchar(30),
    LUN bigint,
    SIZE int,
    DEVICE varchar(20),
    constraint AZURE_DISK_PK primary key(DISK_NO)
);
