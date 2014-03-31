alter table TEMPLATE_INSTANCE add constraint TEMPLATE_INSTANCE_FK1 foreign key (TEMPLATE_NO) references TEMPLATE (TEMPLATE_NO);
alter table TEMPLATE_INSTANCE add constraint TEMPLATE_INSTANCE_FK2 foreign key (PLATFORM_NO) references PLATFORM (PLATFORM_NO);
alter table TEMPLATE_INSTANCE add constraint TEMPLATE_INSTANCE_FK3 foreign key (IMAGE_NO) references IMAGE (IMAGE_NO);
