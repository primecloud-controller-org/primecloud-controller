create table VMWARE_INSTANCE (
    INSTANCE_NO bigint not null,
    MACHINE_NAME varchar(100),
    INSTANCE_TYPE varchar(30),
    COMPUTE_RESOURCE varchar(100),
    RESOURCE_POOL varchar(100),
    DATASTORE varchar(100),
    KEY_PAIR_NO bigint,
    IP_ADDRESS varchar(100),
    PRIVATE_IP_ADDRESS varchar(100),
    constraint VMWARE_INSTANCE_PK primary key(INSTANCE_NO)
);
