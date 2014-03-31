create table CLOUDSTACK_INSTANCE (
    INSTANCE_NO bigint not null,
    KEY_NAME varchar(100),
    INSTANCE_TYPE varchar(20),
    INSTANCE_ID varchar(20),
    DISPLAYNAME varchar(100),
    IPADDRESS varchar(100),
    STATE varchar(20),
    ZONEID varchar(100),
    NETWORKID varchar(20),
    SECURITYGROUP varchar(100),
    constraint CLOUDSTACK_INSTANCE_PK primary key(INSTANCE_NO)
);
