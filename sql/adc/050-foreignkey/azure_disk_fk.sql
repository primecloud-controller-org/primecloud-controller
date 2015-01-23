alter table AZURE_DISK add constraint AZURE_DISK_FK1 foreign key (FARM_NO) references FARM (FARM_NO);
alter table AZURE_DISK add constraint AZURE_DISK_FK2 foreign key (COMPONENT_NO) references COMPONENT (COMPONENT_NO);
alter table AZURE_DISK add constraint AZURE_DISK_FK3 foreign key (INSTANCE_NO) references AZURE_INSTANCE (INSTANCE_NO);
alter table AZURE_DISK add constraint AZURE_DISK_FK4 foreign key (PLATFORM_NO) references PLATFORM (PLATFORM_NO);
