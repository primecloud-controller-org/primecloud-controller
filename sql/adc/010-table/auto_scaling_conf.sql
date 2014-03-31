create table AUTO_SCALING_CONF (
    LOAD_BALANCER_NO bigint not null,
    FARM_NO bigint not null,
    PLATFORM_NO bigint not null,
    IMAGE_NO bigint not null,
    INSTANCE_TYPE varchar(20),
    NAMING_RULE varchar(30),
    IDLE_TIME_MAX bigint not null,
    IDLE_TIME_MIN bigint not null,
    CONTINUE_LIMIT bigint not null,
    ADD_COUNT bigint not null,
    DEL_COUNT bigint not null,
    ENABLED boolean,
    constraint AUTO_SCALING_CONF_PK primary key(LOAD_BALANCER_NO)
);
