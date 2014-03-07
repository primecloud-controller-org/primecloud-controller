create table TEMPLATE (
    TEMPLATE_NO bigint(20) not null auto_increment,
    TEMPLATE_NAME varchar(50) not null,
    TEMPLATE_NAME_DISP varchar(300) not null,
    TEMPLATE_DESCRIPTION_DISP varchar(500) not null,
    constraint TEMPLATE_PK primary key(TEMPLATE_NO)
);