create table USER (
    USER_NO bigint not null auto_increment,
    USERNAME varchar(50),
    PASSWORD varchar(50),
    ENABLED boolean not null default true,
    LAST_LOGIN_DATE datetime,
    constraint USER_PK primary key(USER_NO)
);
