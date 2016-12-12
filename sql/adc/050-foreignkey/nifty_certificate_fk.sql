alter table NIFTY_CERTIFICATE add constraint NIFTY_CERTIFICATE_FK2 foreign key (PLATFORM_NO) references PLATFORM (PLATFORM_NO);
