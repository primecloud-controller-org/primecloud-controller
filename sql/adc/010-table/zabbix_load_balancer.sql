create table ZABBIX_LOAD_BALANCER (
    LOAD_BALANCER_NO bigint not null,
    HOSTID varchar(20),
    STATUS varchar(20),
    constraint ZABBIX_LOAD_BALANCER_PK primary key(LOAD_BALANCER_NO)
);
