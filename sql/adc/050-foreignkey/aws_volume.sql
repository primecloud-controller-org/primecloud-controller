alter table AWS_VOLUME add constraint AWS_VOLUME_FK1 foreign key (FARM_NO) references FARM (FARM_NO);
alter table AWS_VOLUME add constraint AWS_VOLUME_FK2 foreign key (COMPONENT_NO) references COMPONENT (COMPONENT_NO);
alter table AWS_VOLUME add constraint AWS_VOLUME_FK3 foreign key (INSTANCE_NO) references AWS_INSTANCE (INSTANCE_NO);
