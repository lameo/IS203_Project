#select * from location;

#returns the max time the particular person spend at a location
SELECT max(TIMESTAMP) as TIMESTAMP, macaddress, locationid 
                    FROM location 
                    WHERE timestamp BETWEEN (SELECT DATE_SUB('2017-02-06 11:00:02' ,INTERVAL 15 MINUTE)) 
                    AND (SELECT DATE_SUB('2017-02-06 11:00:02' ,INTERVAL 1 SECOND))
group by macaddress, locationid;

#people who are in a specific place in a given time frame in a specific location
select distinct l.macaddress, llu.locationname from location l, locationlookup llu 
                    WHERE timestamp BETWEEN (SELECT DATE_SUB('2017-02-06 11:00:00' ,INTERVAL 15 MINUTE)) 
                    AND (SELECT DATE_SUB('2017-02-06 11:00:00' ,INTERVAL 1 SECOND))
 and l.locationid = llu.locationid
 and llu.locationname = 'SMUSISL1LOBBY'
 group by l.macaddress;

select lu.locationname, l.timestamp from location l, locationlookup lu where l.locationid= lu.locationid and macaddress = '16221efe58802213454132077cd432285cad4c23';
#DATE_SUB -> Substraction
#DATE_ADD -> Addition

#get location name and time given a specfic user
select llu.locationname, l.timestamp from locationlookup llu, location l 
	where macaddress = '16221efe58802213454132077cd432285cad4c23'
    and timestamp BETWEEN (SELECT DATE_ADD('2017-02-06 11:00:00' ,INTERVAL 0 MINUTE))
                    AND (SELECT DATE_ADD(DATE_ADD('2017-02-06 11:00:00' ,INTERVAL 14 MINUTE), INTERVAL 59 SECOND))
    and llu.locationid = l.locationid;

#SELECT timestamp from location where timestamp between '2017-02-06 11:00:00' and '2017-02-06 11:15:00';

#select llu.locationname, l.timestamp from locationlookup llu, location l where macaddress = '16221efe58802213454132077cd432285cad4c23'and llu.locationid = l.locationid;
select lu.locationname, timestamp from locationlookup lu, location l where lu.locationid = l.locationid;