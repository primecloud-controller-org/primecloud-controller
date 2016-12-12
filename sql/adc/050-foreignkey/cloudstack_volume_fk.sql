alter table CLOUDSTACK_VOLUME add constraint CLOUDSTACK_VOLUME_FK1 foreign key (FARM_NO) references FARM (FARM_NO);
alter table CLOUDSTACK_VOLUME add constraint CLOUDSTACK_VOLUME_FK2 foreign key (COMPONENT_NO) references COMPONENT (COMPONENT_NO);
alter table CLOUDSTACK_VOLUME add constraint CLOUDSTACK_VOLUME_FK3 foreign key (INSTANCE_NO) references CLOUDSTACK_INSTANCE (INSTANCE_NO);
alter table CLOUDSTACK_VOLUME add constraint CLOUDSTACK_VOLUME_FK4 foreign key (PLATFORM_NO) references PLATFORM (PLATFORM_NO);
