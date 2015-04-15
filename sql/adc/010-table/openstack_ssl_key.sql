create table OPENSTACK_SSL_KEY (
    KEY_NO bigint(20) not null,
    KEY_NAME varchar(100),
    SSLCERTIFICATEID varchar(100),
    FARM_NO bigint(20),
    PLATFORM_NO bigint(20),
    constraint OPENSTACK_SSL_KEY primary key (KEY_NO)
);