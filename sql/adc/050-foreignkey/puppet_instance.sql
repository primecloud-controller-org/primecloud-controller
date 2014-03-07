alter table PUPPET_INSTANCE add constraint PUPPET_INSTANCE_FK1 foreign key (INSTANCE_NO) references INSTANCE (INSTANCE_NO);
