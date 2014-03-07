alter table AWS_SNAPSHOT add constraint AWS_SNAPSHOT_FK3 foreign key (PLATFORM_NO) references PLATFORM (PLATFORM_NO);
