create table AWS_SSL_KEY (
    KEY_NO bigint not null auto_increment,
    KEY_NAME varchar(100) not null,
    SSLCERTIFICATEID varchar(100) not null,
    FARM_NO bigint not null,
    PLATFORM_NO bigint not null,
    constraint AWS_SSL_KEY_PK primary key(KEY_NO)
);
