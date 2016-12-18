create table AWS_INSTANCE (
    INSTANCE_NO bigint not null,
    KEY_NAME varchar(100),
    INSTANCE_TYPE varchar(20),
    SECURITY_GROUPS varchar(100),
    AVAILABILITY_ZONE varchar(100),
    SUBNET_ID varchar(30),
    ROOT_SIZE int,
    INSTANCE_ID varchar(30),
    STATUS varchar(20),
    DNS_NAME varchar(100),
    PRIVATE_DNS_NAME varchar(100),
    IP_ADDRESS varchar(100),
    PRIVATE_IP_ADDRESS varchar(100),
    constraint AWS_INSTANCE_PK primary key(INSTANCE_NO)
);
