create table AWS_LOAD_BALANCER (
    LOAD_BALANCER_NO bigint not null,
    NAME varchar(30) not null,
    DNS_NAME varchar(100),
    SUBNET_ID varchar(100),
    SECURITY_GROUPS varchar(100),
    AVAILABILITY_ZONE varchar(100),
    HOSTID varchar(20),
    STATUS varchar(20),
    constraint AWS_LOAD_BALANCER_PK primary key(LOAD_BALANCER_NO)
);
