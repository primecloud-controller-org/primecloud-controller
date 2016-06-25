create table AWS_SNAPSHOT (
    SNAPSHOT_NO bigint not null auto_increment,
    FARM_NO bigint not null,
    PLATFORM_NO bigint not null,
    VOLUME_NO bigint,
    SNAPSHOT_ID varchar(30),
    CREATE_DATE datetime,
    constraint AWS_SNAPSHOT_PK primary key(SNAPSHOT_NO)
);
