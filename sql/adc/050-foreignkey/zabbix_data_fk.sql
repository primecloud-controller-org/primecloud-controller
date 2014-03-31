alter table ZABBIX_DATA add constraint ZABBIX_DATA_FK1 foreign key (INSTANCE_NO) references INSTANCE (INSTANCE_NO);
