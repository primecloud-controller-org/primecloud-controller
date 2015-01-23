alter table AZURE_INSTANCE add constraint AZURE_INSTANCE_FK1 foreign key (INSTANCE_NO) references INSTANCE (INSTANCE_NO);
