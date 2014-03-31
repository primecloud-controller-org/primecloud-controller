create table LOAD_BALANCER_HEALTH_CHECK (
    LOAD_BALANCER_NO bigint not null,
    CHECK_PROTOCOL varchar(20),
    CHECK_PORT int,
    CHECK_PATH varchar(100),
    CHECK_TIMEOUT int,
    CHECK_INTERVAL int,
    HEALTHY_THRESHOLD int,
    UNHEALTHY_THRESHOLD int,
    constraint LOAD_BALANCER_HEALTH_CHECK_PK primary key(LOAD_BALANCER_NO)
);
