create table AZURE_SUBNET (
    SUBNET_NO bigint not null auto_increment,
    SUBNET_NAME varchar(100) not null,
    NETWORK_NAME varchar(100) not null,
    constraint AZURE_SUBNET primary key (SUBNET_NO)
);