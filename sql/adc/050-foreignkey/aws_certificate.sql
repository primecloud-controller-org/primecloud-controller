alter table AWS_CERTIFICATE add constraint AWS_CERTIFICATE_FK1 foreign key (USER_NO) references USER (USER_NO);
