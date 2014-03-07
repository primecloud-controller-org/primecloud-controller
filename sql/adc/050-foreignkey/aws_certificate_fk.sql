alter table AWS_CERTIFICATE add constraint AWS_CERTIFICATE_FK2 foreign key (PLATFORM_NO) references PLATFORM (PLATFORM_NO);
