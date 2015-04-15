alter table OPENSTACK_INSTANCE add constraint OPENSTACK_INSTANCE_FK1 foreign key (INSTANCE_NO) references INSTANCE (INSTANCE_NO);
