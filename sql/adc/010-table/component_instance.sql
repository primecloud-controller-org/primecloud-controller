create table COMPONENT_INSTANCE (
    COMPONENT_NO bigint not null,
    INSTANCE_NO bigint not null,
    ASSOCIATE boolean,
    ENABLED boolean,
    STATUS varchar(20),
    CONFIGURE boolean,
    constraint COMPONENT_INSTANCE_PK primary key(COMPONENT_NO, INSTANCE_NO)
);
