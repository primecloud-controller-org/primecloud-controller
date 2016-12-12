alter table LOAD_BALANCER add constraint LOAD_BALANCER_FK1 foreign key (FARM_NO) references FARM (FARM_NO);
alter table LOAD_BALANCER add constraint LOAD_BALANCER_FK2 foreign key (COMPONENT_NO) references COMPONENT (COMPONENT_NO);
