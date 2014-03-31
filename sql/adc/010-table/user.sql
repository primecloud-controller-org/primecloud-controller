create table USER (
    USER_NO bigint not null auto_increment,
    USERNAME varchar(50),
    PASSWORD varchar(50),
    MASTER_USER bigint,
    POWER_USER  boolean,
    constraint USER_PK primary key(USER_NO)
);
