create table OPENSTACK_INSTANCE (
    INSTANCE_NO bigint not null,
    KEY_NAME varchar(100),
    INSTANCE_TYPE varchar(20),
    SECURITY_GROUPS varchar(100),
    AVAILABILITY_ZONE varchar(100),
    INSTANCE_ID varchar(40),
    STATUS varchar(20),
    CLIENT_IP_ADDRESS varchar(100),
    PRIVATE_IP_ADDRESS varchar(100),
    NETWORK_ID varchar(40),
    constraint OPENSTACK_INSTANCE_PK primary key(INSTANCE_NO)
);
