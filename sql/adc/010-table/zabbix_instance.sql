create table ZABBIX_INSTANCE (
    INSTANCE_NO bigint not null,
    HOSTID varchar(20),
    STATUS varchar(20),
    constraint ZABBIX_INSTANCE_PK primary key(INSTANCE_NO)
);
