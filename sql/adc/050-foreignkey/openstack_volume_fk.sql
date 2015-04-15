alter table OPENSTACK_VOLUME add constraint OPENSTACK_VOLUME_FK1 foreign key (FARM_NO) references FARM (FARM_NO);
alter table OPENSTACK_VOLUME add constraint OPENSTACK_VOLUME_FK2 foreign key (COMPONENT_NO) references COMPONENT (COMPONENT_NO);
alter table OPENSTACK_VOLUME add constraint OPENSTACK_VOLUME_FK3 foreign key (INSTANCE_NO) references OPENSTACK_INSTANCE (INSTANCE_NO);
alter table OPENSTACK_VOLUME add constraint OPENSTACK_VOLUME_FK4 foreign key (PLATFORM_NO) references PLATFORM (PLATFORM_NO);
