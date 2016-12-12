create table USER_AUTH (
    FARM_NO bigint,
    USER_NO bigint,
    FARM_USE boolean,
    SERVER_MAKE boolean,
    SERVER_DELETE boolean,
    SERVER_OPERATE boolean,
    SERVICE_MAKE boolean,
    SERVICE_DELETE boolean,
    SERVICE_OPERATE boolean,
    LB_MAKE boolean,
    LB_DELETE boolean,
    LB_OPERATE boolean,
    constraint USER_AUTH_PK primary key(FARM_NO, USER_NO)
);

