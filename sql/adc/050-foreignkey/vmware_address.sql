alter table VMWARE_ADDRESS add constraint VMWARE_ADDRESS_FK1 foreign key (USER_NO) references USER (USER_NO);
alter table VMWARE_ADDRESS add constraint VMWARE_ADDRESS_FK2 foreign key (INSTANCE_NO) references VMWARE_INSTANCE (INSTANCE_NO);
