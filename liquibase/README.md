# How to run Liquibase scripts

## Run Mysql docker image
Go to /docker folder and run

`MYSQL_PASSWORD=password docker-compose up -d`

## Run Liquibase scripts

`MYSQL_HOST=localhost MYSQL_PORT=3306 MYSQL_USER=root MYSQL_PASSWORD=password gradle build update`