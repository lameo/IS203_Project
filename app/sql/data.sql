create schema `data`;
use `data`;

#demographics
create table demographics
(
macaddress varchar(40) not null, 
name varchar(50) not null, 
password varchar(50) not null, 
email varchar(50) not null, 
gender char(1) not null,
constraint demographics_pk primary key(macaddress)
);

LOAD DATA LOCAL INFILE 'D:/testt/app/web/resource/data/demographics.csv' INTO TABLE demographics
FIELDS TERMINATED BY ',' LINES TERMINATED BY '\r' IGNORE 1 LINES (macaddress, name, password, email, gender);

create table location
(
timestamp datetime(6) not null, 
macaddress varchar(40) not null, 
locationid varchar(10) not null, 
constraint location_pk primary key(macaddress, timestamp)
);

LOAD DATA LOCAL INFILE 'D:/testt/app/web/resource/data/location.csv' INTO TABLE location
FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' IGNORE 1 LINES (timestamp, macaddress, locationid);

create table locationlookup
(
locationid varchar(12) not null,
locationname varchar(25) not null,
constraint locationlookup_pk primary key(locationid)
);

LOAD DATA LOCAL INFILE 'D:/testt/app/web/resource/data/location-lookup.csv' INTO TABLE locationlookup
FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' IGNORE 1 LINES (locationid, locationname);