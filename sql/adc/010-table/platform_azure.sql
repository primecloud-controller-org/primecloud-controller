create table PLATFORM_AZURE (
    PLATFORM_NO bigint(20) not null,
    LOCATION_NAME varchar(100) not null,
    AFFINITY_GROUP_NAME varchar(100) not null,
    CLOUD_SERVICE_NAME varchar(100) not null,
    STORAGE_ACCOUNT_NAME varchar(100) not null,
    NETWORK_NAME varchar(100) not null,
    AVAILABILITY_SETS varchar(500),
    constraint PLATFORM_AZURE_PK primary key (PLATFORM_NO)
);