alter table VMWARE_KEY_PAIR add constraint VMWARE_KEY_PAIR_FK1 foreign key (USER_NO) references USER (USER_NO);
