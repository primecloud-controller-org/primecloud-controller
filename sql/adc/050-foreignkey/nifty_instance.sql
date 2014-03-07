alter table NIFTY_INSTANCE add constraint NIFTY_INSTANCE_FK1 foreign key (INSTANCE_NO) references INSTANCE (INSTANCE_NO);
alter table NIFTY_INSTANCE add constraint NIFTY_INSTANCE_FK2 foreign key (KEY_PAIR_NO) references NIFTY_KEY_PAIR (KEY_NO);
