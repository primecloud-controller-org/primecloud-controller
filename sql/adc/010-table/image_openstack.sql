create table IMAGE_OPENSTACK (
    IMAGE_NO bigint(20) not null,
    IMAGE_ID varchar(100) not null,
    KERNEL_ID varchar(100),
    RAMDISK_ID varchar(100),
    INSTANCE_TYPES varchar(500) not null,
    constraint IMAGE_OPENSTACK primary key (IMAGE_NO)
);