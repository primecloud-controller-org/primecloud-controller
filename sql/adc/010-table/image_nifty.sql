create table IMAGE_NIFTY (
    IMAGE_NO bigint(20) not null,
    IMAGE_ID varchar(100) not null,
    INSTANCE_TYPES varchar(500) not null,
    constraint IMAGE_NIFTY_PK primary key (IMAGE_NO)
);