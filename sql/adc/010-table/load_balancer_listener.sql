create table LOAD_BALANCER_LISTENER (
    LOAD_BALANCER_NO bigint not null,
    LOAD_BALANCER_PORT int not null,
    SERVICE_PORT int not null,
    PROTOCOL varchar(20) not null,
    SSL_KEY_NO bigint,
    ENABLED boolean,
    STATUS varchar(20),
    CONFIGURE boolean,
    constraint LOAD_BALANCER_LISTENER_PK primary key(LOAD_BALANCER_NO, LOAD_BALANCER_PORT)
);
