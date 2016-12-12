create table CLOUDSTACK_LOAD_BALANCER (
    LOAD_BALANCER_NO bigint not null,
    LOAD_BALANCER_ID varchar(20),
    ALGORITHM  varchar(100),
    DESCRIPTION varchar(100),
    NAME varchar(30) not null,
    ADDRESS_ID varchar(20),
    PUBLICIP varchar(100),
    PUBLICPORT varchar(20),
    PRIVATEPORT varchar(20),
    STATE varchar(20),
    ZONEID varchar(100),
    constraint CLOUDSTACK_LOAD_BALANCER_PK primary key(LOAD_BALANCER_NO)
);
