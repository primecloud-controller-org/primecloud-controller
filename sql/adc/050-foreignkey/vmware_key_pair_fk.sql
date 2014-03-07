alter table VMWARE_KEY_PAIR add constraint VMWARE_KEY_PAIR_FK2 foreign key (PLATFORM_NO) references PLATFORM (PLATFORM_NO);
