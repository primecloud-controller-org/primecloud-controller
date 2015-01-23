alter table OPENSTACK_CERTIFICATE add constraint OPENSTACK_CERTIFICATE_FK1 foreign key (USER_NO) references USER (USER_NO);
alter table OPENSTACK_CERTIFICATE add constraint OPENSTACK_CERTIFICATE_FK2 foreign key (PLATFORM_NO) references PLATFORM (PLATFORM_NO);
