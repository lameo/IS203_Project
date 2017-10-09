LOAD DATA LOCAL INFILE 'D:/testt/app/web/resource/data/location.csv' INTO TABLE location
FIELDS TERMINATED BY ',' LINES TERMINATED BY '\r\n' IGNORE 1 LINES (timestamp, macaddress, locationid);

# contains empty
select * from location where timestamp is null or timestamp like '' or macaddress is null or macaddress like '' or locationid is null or locationid like ''
;

# failed macaddress
select * from location where
	char_length(macaddress) <> 40
;

# failed date format
select * from location where
    timestamp not like '____-__-__ __:__:__'
    or SUBSTRING_INDEX(timestamp, '-', 1) not between 2013 and 2017
    or SUBSTRING(SUBSTRING_INDEX(timestamp, '-', 2), -2) not between 1 and 12
    or SUBSTRING(SUBSTRING_INDEX(timestamp, ' ', 1), -2) not between 1 and 31
    or SUBSTRING(SUBSTRING_INDEX(timestamp, ':', 1), -2) not between 00 and 23
    or SUBSTRING(SUBSTRING_INDEX(timestamp, ':', 2), -2) not between 00 and 60
    or SUBSTRING(timestamp, -2) not between 00 and 60
;


# failed everything
select * from location where
	timestamp is null or timestamp like ''
    or macaddress is null or macaddress like ''
    or locationid is null or locationid like ''
	or char_length(macaddress) <> 40
    or timestamp not like '____-__-__ __:__:__'
    or SUBSTRING_INDEX(timestamp, '-', 1) not between 2013 and 2017
    or SUBSTRING(SUBSTRING_INDEX(timestamp, '-', 2), -2) not between 1 and 12
    or SUBSTRING(SUBSTRING_INDEX(timestamp, ' ', 1), -2) not between 1 and 31
    or SUBSTRING(SUBSTRING_INDEX(timestamp, ':', 1), -2) not between 00 and 23
    or SUBSTRING(SUBSTRING_INDEX(timestamp, ':', 2), -2) not between 00 and 60
    or SUBSTRING(timestamp, -2) not between 00 and 60
;


# delete failed item
delete location from location,
(select * from location where char_length(macaddress) <> 40 or timestamp not like '____-__-__ __:__:__' 
or SUBSTRING_INDEX(timestamp, '-', 1) not between 2013 and 2017 
or SUBSTRING(SUBSTRING_INDEX(timestamp, '-', 2), -2) not between 1 and 12 
or SUBSTRING(SUBSTRING_INDEX(timestamp, ' ', 1), -2) not between 1 and 31 
or SUBSTRING(SUBSTRING_INDEX(timestamp, ':', 1), -2) not between 00 and 23 
or SUBSTRING(SUBSTRING_INDEX(timestamp, ':', 2), -2) not between 00 and 60 
or SUBSTRING(timestamp, -2) not between 00 and 60) t1
where location.locationid = t1.locationid and location.macaddress = t1.macaddress and location.timestamp = t1.timestamp;

# conversation back to proper format, need varchar to check date formatting
ALTER TABLE `data`.`location` CHANGE COLUMN `locationid` `locationid` DATETIME NOT NULL ;