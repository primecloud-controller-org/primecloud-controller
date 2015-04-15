create table IMAGE_AZURE (
    IMAGE_NO bigint(20) not null,
    IMAGE_NAME varchar(100) not null,
    INSTANCE_TYPES varchar(500) not null,
    constraint IMAGE_AZURE primary key (IMAGE_NO)
);