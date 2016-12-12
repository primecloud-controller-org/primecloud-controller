create table IMAGE_CLOUDSTACK (
    IMAGE_NO bigint(20) not null,
    TEMPLATE_ID varchar(100) not null,
    INSTANCE_TYPES varchar(500) not null,
    constraint IMAGE_CLOUDSTACK primary key (IMAGE_NO)
);