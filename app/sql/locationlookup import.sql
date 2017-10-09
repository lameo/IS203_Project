LOAD DATA LOCAL INFILE 'D:/testt/app/web/resource/data/location-lookup.csv' INTO TABLE locationlookup
FIELDS TERMINATED BY ',' LINES TERMINATED BY '\r\n' IGNORE 1 LINES (locationid, locationname);


# if fields are empty
select  *  from  locationlookup where locationid is null or locationid like '' or locationname is null or locationname like '' 
;


# failed location name
select * from locationlookup where locationname = "invalid semantic place"
;

# failed location id
select * from locationlookup where locationid < 0
;

#failed everything
select * from locationlookup where locationname = "invalid semantic place" or locationid < 0 or locationid is null or locationid like '' or locationname is null or locationname like '' 
;

#delete those invalid dataset
delete locationlookup from locationlookup, (select * from locationlookup where locationid is null or locationid like '' or locationname is null or locationname like '' or locationname = "invalid semantic place" or locationid < 0) t1 where locationlookup.locationid = t1.locationid;