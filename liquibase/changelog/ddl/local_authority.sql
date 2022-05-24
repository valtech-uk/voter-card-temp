--liquibase formatted sql
--changeset abdullah.sanver:create-local_authority context:ddl splitStatements:true endDelimiter:;

CREATE TABLE local_authority (
    id varchar(40) NOT NULL,
    gss_code varchar(20),
    api_client_id varchar(40),
    address_line_1 varchar(255),
    address_line_2 varchar(255),
    address_line_3 varchar(255),
    address_line_4 varchar(255),
    postcode varchar(12),
    phone_number varchar(20),
    email_address varchar(255),
    url varchar(255),
    name varchar(100) NOT NULL,
    canvass_email varchar(255),
    ip_address_cidrs varchar(2047)
);