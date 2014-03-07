alter table VMWARE_NETWORK add constraint VMWARE_NETWORK_FK1 foreign key (FARM_NO) references FARM (FARM_NO);
