create table AZURE_INSTANCE (
    INSTANCE_NO bigint not null,
    INSTANCE_NAME varchar(30),
    AFFINITY_GROUP_NAME varchar(100),
    CLOUD_SERVICE_NAME varchar(100),
    STORAGE_ACCOUNT_NAME varchar(100),
    NETWORK_NAME varchar(100),
    INSTANCE_TYPE varchar(20),
    STATUS varchar(20),
    SUBNET_ID varchar(30),
    PRIVATE_IP_ADDRESS varchar(100),
    LOCATION_NAME varchar(100),
    AVAILABILITY_SET varchar(100),
    constraint AZURE_INSTANCE_PK primary key(INSTANCE_NO)
);
