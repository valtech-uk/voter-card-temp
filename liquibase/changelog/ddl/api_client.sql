--liquibase formatted sql
--changeset abdullah.sanver:create-api_client context:ddl splitStatements:true endDelimiter:;

CREATE TABLE api_client (
    id varchar(40) NOT NULL,
    description varchar(200) NOT NULL,
    identifier varchar(200) NOT NULL,
    av_applications_to_portal boolean
);
