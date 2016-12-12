create table NIFTY_KEY_PAIR (
    KEY_NO bigint not null auto_increment,
    USER_NO bigint not null,
    PLATFORM_NO bigint not null,
    KEY_NAME varchar(100) not null,
    PRIVATE_KEY varchar(10240) not null,
    PASSPHRASE varchar(100),
    constraint NIFTY_KEY_PAIR_PK primary key(KEY_NO)
);
