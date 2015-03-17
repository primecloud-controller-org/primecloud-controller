CREATE TABLE PLATFORM_VCLOUD_INSTANCE_TYPE (
    INSTANCE_TYPE_NO BIGINT(20) NOT NULL AUTO_INCREMENT,
    PLATFORM_NO BIGINT(20) NOT NULL,
    INSTANCE_TYPE_NAME VARCHAR(100) NOT NULL,
    CPU INT(10) NOT NULL,
    MEMORY BIGINT(20) NOT NULL,
    CONSTRAINT PLATFORM_VCLOUD_INSTANCE_TYPE_PK1 PRIMARY KEY (INSTANCE_TYPE_NO)
);