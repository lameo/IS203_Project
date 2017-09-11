create schema `data`;
use `data`;

#demographics
create table demographics
(
macaddress varchar(50) not null, 
name varchar(50) not null, 
password varchar(50) not null, 
email varchar(50) not null, 
gender char(1) not null,
constraint demographics_pk primary key(macaddress)
);

LOAD DATA LOCAL INFILE 'D:/testt/app/web/resource/data/demographics.csv' INTO TABLE demographics
FIELDS TERMINATED BY ',' LINES TERMINATED BY '\r' IGNORE 1 LINES (macaddress, name, password, email, gender);

select * from demographics;