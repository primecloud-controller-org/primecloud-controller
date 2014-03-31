alter table ZABBIX_INSTANCE add constraint ZABBIX_INSTANCE_FK1 foreign key (INSTANCE_NO) references INSTANCE (INSTANCE_NO);
