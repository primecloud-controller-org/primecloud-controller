alter table AWS_ADDRESS add constraint AWS_ADDRESS_FK1 foreign key (USER_NO) references USER (USER_NO);
alter table AWS_ADDRESS add constraint AWS_ADDRESS_FK2 foreign key (INSTANCE_NO) references AWS_INSTANCE (INSTANCE_NO);
