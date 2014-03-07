alter table CLOUDSTACK_ADDRESS add constraint CLOUDSTACK_ADDRESS_FK1 foreign key (ACCOUNT) references USER (USER_NO);
alter table CLOUDSTACK_ADDRESS add constraint CLOUDSTACK_ADDRESS_FK2 foreign key (INSTANCE_NO) references CLOUDSTACK_INSTANCE (INSTANCE_NO);
alter table CLOUDSTACK_ADDRESS add constraint CLOUDSTACK_ADDRESS_FK3 foreign key (PLATFORM_NO) references PLATFORM (PLATFORM_NO);