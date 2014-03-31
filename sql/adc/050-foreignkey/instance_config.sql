alter table INSTANCE_CONFIG add constraint INSTANCE_CONFIG_FK1 foreign key (INSTANCE_NO) references INSTANCE (INSTANCE_NO);
alter table INSTANCE_CONFIG add constraint INSTANCE_CONFIG_FK2 foreign key (COMPONENT_NO) references COMPONENT (COMPONENT_NO);
