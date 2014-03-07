create table VMWARE_KEY_PAIR (
    KEY_NO bigint not null auto_increment,
    USER_NO bigint not null,
    PLATFORM_NO bigint not null,
    KEY_NAME varchar(100) not null,
    KEY_PUBLIC varchar(1000),
    constraint VMWARE_KEY_PAIR_PK primary key(KEY_NO)
);
