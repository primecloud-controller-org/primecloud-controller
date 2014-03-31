alter table AWS_SNAPSHOT add constraint AWS_SNAPSHOT_FK1 foreign key (FARM_NO) references FARM (FARM_NO);
alter table AWS_SNAPSHOT add constraint AWS_SNAPSHOT_FK2 foreign key (VOLUME_NO) references AWS_VOLUME (VOLUME_NO);
