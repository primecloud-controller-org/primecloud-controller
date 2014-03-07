alter table AUTO_SCALING_CONF add constraint AUTO_SCALING_CONF_FK1 foreign key (FARM_NO) references FARM (FARM_NO);
alter table AUTO_SCALING_CONF add constraint AUTO_SCALING_CONF_FK2 foreign key (LOAD_BALANCER_NO) references LOAD_BALANCER (LOAD_BALANCER_NO);
