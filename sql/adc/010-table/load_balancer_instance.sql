create table LOAD_BALANCER_INSTANCE (
    LOAD_BALANCER_NO bigint not null,
    INSTANCE_NO bigint not null,
    ENABLED boolean,
    STATUS varchar(20),
    constraint LOAD_BALANCER_INSTANCE_PK primary key(LOAD_BALANCER_NO, INSTANCE_NO)
);
