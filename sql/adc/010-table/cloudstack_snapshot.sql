create table CLOUDSTACK_SNAPSHOT (
    SNAPSHOT_NO bigint not null auto_increment,
    SNAPSHOT_ID varchar(20),
    FARM_NO bigint not null,
    PLATFORM_NO bigint not null,
    CREATE_DATE varchar(30),
    VOLUMEID varchar(20),
    constraint CLOUDSTACK_SNAPSHOT_PK primary key(SNAPSHOT_NO)
);
