alter table VMWARE_INSTANCE add constraint VMWARE_INSTANCE_FK1 foreign key (INSTANCE_NO) references INSTANCE (INSTANCE_NO);
alter table VMWARE_INSTANCE add constraint VMWARE_INSTANCE_FK2 foreign key (KEY_PAIR_NO) references VMWARE_KEY_PAIR (KEY_NO);
