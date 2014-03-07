create table IMAGE_VMWARE (
    IMAGE_NO bigint(20) not null,
    TEMPLATE_NAME varchar(100) not null,
    INSTANCE_TYPES varchar(500) not null,
    constraint IMAGE_VMWARE_PK primary key (IMAGE_NO)
);