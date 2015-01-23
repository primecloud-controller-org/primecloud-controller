alter table AWS_INSTANCE add constraint AWS_INSTANCE_FK1 foreign key (INSTANCE_NO) references INSTANCE (INSTANCE_NO);
