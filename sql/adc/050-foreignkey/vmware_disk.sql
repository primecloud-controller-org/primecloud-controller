alter table VMWARE_DISK add constraint VMWARE_DISK_FK1 foreign key (FARM_NO) references FARM (FARM_NO);
alter table VMWARE_DISK add constraint VMWARE_DISK_FK2 foreign key (COMPONENT_NO) references COMPONENT (COMPONENT_NO);
alter table VMWARE_DISK add constraint VMWARE_DISK_FK3 foreign key (INSTANCE_NO) references VMWARE_INSTANCE (INSTANCE_NO);
