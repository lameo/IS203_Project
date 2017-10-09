create schema `data`;
use `data`;

#demographics
create table demographics
(
macaddress varchar(40) ,
name varchar(50) ,
password varchar(50) ,
email varchar(50) ,
gender char(1) not null,
constraint demographics_pk primary key(macaddress)
);

LOAD DATA LOCAL INFILE 'D:/testt/app/web/resource/data/demographics.csv' INTO TABLE demographics
FIELDS TERMINATED BY ',' LINES TERMINATED BY '\r' IGNORE 1 LINES (macaddress, name, password, email, gender);


create table locationlookup
(
locationid varchar(12) not null,
locationname varchar(25) not null,
constraint locationlookup_pk primary key(locationid)
);

LOAD DATA LOCAL INFILE 'D:/testt/app/web/resource/data/location-lookup.csv' INTO TABLE locationlookup
FIELDS TERMINATED BY ',' LINES TERMINATED BY '\r\n' IGNORE 1 LINES (locationid, locationname);


create table location
(
timestamp varchar(20) ,
macaddress varchar(40) ,
locationid varchar(10) ,
constraint location_pk primary key(macaddress, timestamp)
);

LOAD DATA LOCAL INFILE 'D:/testt/app/web/resource/data/location.csv' INTO TABLE location
FIELDS TERMINATED BY ',' LINES TERMINATED BY '\r\n' IGNORE 1 LINES (timestamp, macaddress, locationid);


create table stalkerMode
(
macaddress varchar(40) ,
locationid varchar(10) ,
locationname varchar(25) not null,
maxTimestamp datetime(6) not null,
minTimestamp datetime(6) not null,
minutesSpent int(4) not null,
constraint location_pk primary key(macaddress, maxTimestamp)
);

INSERT INTO stalkerMode ()
SELECT 
	macaddress, location.locationid, locationname, max(timestamp), min(timestamp), TIMESTAMPDIFF(MINUTE, min(timestamp),max(timestamp))
from 
	location, 
    locationlookup 
where 
	location.locationid = locationlookup.locationid
group by 
	macaddress, location.locationid, locationname, day(timestamp);