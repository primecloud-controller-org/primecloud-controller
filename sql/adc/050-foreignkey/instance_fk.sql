alter table INSTANCE add constraint INSTANCE_FK2 foreign key (PLATFORM_NO) references PLATFORM (PLATFORM_NO);
alter table INSTANCE add constraint INSTANCE_FK3 foreign key (IMAGE_NO) references IMAGE (IMAGE_NO);
