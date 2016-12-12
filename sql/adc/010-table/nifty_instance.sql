create table NIFTY_INSTANCE (
    INSTANCE_NO bigint not null,
    KEY_PAIR_NO bigint,
    INSTANCE_TYPE varchar(20),
    INSTANCE_ID varchar(20),
    STATUS varchar(20),
    DNS_NAME varchar(100),
    PRIVATE_DNS_NAME varchar(100),
    IP_ADDRESS varchar(100),
    PRIVATE_IP_ADDRESS varchar(100),
    INITIALIZED boolean,
    constraint NIFTY_INSTANCE_PK primary key(INSTANCE_NO)
);
