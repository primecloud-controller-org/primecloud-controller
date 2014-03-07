create table ZABBIX_DATA (
    INSTANCE_NO bigint not null,
    HOSTID varchar(20),
    IDLE_TIME bigint not null,
    FIRST_CLOCK bigint not null,
    LAST_CLOCK bigint not null,
    CONTINUE_CLOCK bigint not null,
    ALART bigint not null,
    constraint ZABBIX_DATA_PK primary key(INSTANCE_NO)
);
