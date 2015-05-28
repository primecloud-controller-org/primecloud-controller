#alter table AWS_SSL_KEY add constraint AWS_SSL_KEY_FK1 foreign key (FARM_NO) references FARM (FARM_NO);
#alter table AWS_SSL_KEY add constraint AWS_SSL_KEY_FK2 foreign key (PLATFORM_NO) references PLATFORM (PLATFORM_NO);
