LOAD DATA LOCAL INFILE 'C:/Users/Yang/Desktop/demographics.csv' INTO TABLE demographics
FIELDS TERMINATED BY ',' LINES TERMINATED BY '\r' IGNORE 1 LINES (macaddress, name, password, email, gender);

# if fields are empty
select  *  from  demographics where password is null or password like '' or name is null or name like '' or macaddress is null or macaddress like '' or gender is null or gender like '' or email is null or email like ''
;

# password not valid
select  *  from  demographics where password like ('% %') or char_length(password) < 8
;

#email not valid
select  *  from  demographics where
	email like '%..%'
	OR email NOT LIKE '%2013@%' AND email NOT LIKE '%2014@%' AND email NOT LIKE '%2015@%' AND email NOT LIKE '%2016@%' AND email NOT LIKE '%2017@%' AND email NOT LIKE '%2010@%' AND email NOT LIKE '%2011@%' AND email NOT LIKE '%2012@%'
    OR email NOT LIKE '%@business%' AND email NOT LIKE '%@accountancy%' AND email NOT LIKE '%@sis%' AND email NOT LIKE '%@economics%' AND email NOT LIKE '%@law%' AND email NOT LIKE '%@socsc%'
	or (select SUBSTRING_INDEX(email, '@', 1)) like '%(%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%)%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%<%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%>%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%[%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%]%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%:%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%,%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%;%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%@%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%\%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%"%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%!%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%#%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%$%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%&%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%\'%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%-%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%/%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%=%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%?%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%^%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%`%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%{%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%}%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%|%' or (select SUBSTRING_INDEX(email, '@', 1)) like '% %' or (select SUBSTRING_INDEX(email, '@', 1)) like '%~%'
;

#gender not valid
select  *  from  demographics where NOT(gender = 'F' OR gender = 'M' OR gender = 'f' OR gender = 'm')
;

#mac not valid
select  *  from  demographics where char_length(macaddress) <> 40
;

#everything not valid
select 
	* 
from 
	demographics
where
	password is null or password like ''
    or name is null or name like ''
    or macaddress is null or macaddress like ''
    or gender is null or gender like ''
    or email is null or email like ''
	or password like ('% %')
	or char_length(password) < 8
    or char_length(macaddress) <> 40
    or NOT(gender = 'F' OR gender = 'M' OR gender = 'f' OR gender = 'm')
    or email like '%..%'
	or email NOT LIKE '%2013@%' AND email NOT LIKE '%2014@%' AND email NOT LIKE '%2015@%' AND email NOT LIKE '%2016@%' AND email NOT LIKE '%2017@%'
    OR email NOT LIKE '%@business%' AND email NOT LIKE '%@accountancy%' AND email NOT LIKE '%@sis%' AND email NOT LIKE '%@economics%' AND email NOT LIKE '%@law%' AND email NOT LIKE '%@socsc%'
    or (select SUBSTRING_INDEX(email, '@', 1)) like '%\%%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%*%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%+%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%\_%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%(%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%)%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%<%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%>%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%[%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%]%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%:%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%,%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%;%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%@%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%\%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%"%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%!%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%#%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%$%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%&%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%\'%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%-%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%/%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%=%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%?%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%^%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%`%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%{%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%}%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%|%' or (select SUBSTRING_INDEX(email, '@', 1)) like '% %' or (select SUBSTRING_INDEX(email, '@', 1)) like '%~%'
;

#delete those invalid dataset
delete demographics
from demographics,
(select 
	* 
from 
	demographics
where
	password is null or password like ''
    or name is null or name like ''
    or macaddress is null or macaddress like ''
    or gender is null or gender like ''
    or email is null or email like ''
	or password like ('% %')
	or char_length(password) < 8
    or char_length(macaddress) <> 40
    or NOT(gender = 'F' OR gender = 'M' OR gender = 'f' OR gender = 'm')
    or email like '%..%'
	or email NOT LIKE '%2013@%' AND email NOT LIKE '%2014@%' AND email NOT LIKE '%2015@%' AND email NOT LIKE '%2016@%' AND email NOT LIKE '%2017@%'
    OR email NOT LIKE '%@business%' AND email NOT LIKE '%@accountancy%' AND email NOT LIKE '%@sis%' AND email NOT LIKE '%@economics%' AND email NOT LIKE '%@law%' AND email NOT LIKE '%@socsc%'
    or (select SUBSTRING_INDEX(email, '@', 1)) like '%\%%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%*%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%+%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%\_%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%(%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%)%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%<%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%>%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%[%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%]%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%:%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%,%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%;%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%@%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%\%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%"%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%!%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%#%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%$%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%&%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%\'%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%-%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%/%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%=%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%?%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%^%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%`%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%{%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%}%' or (select SUBSTRING_INDEX(email, '@', 1)) like '%|%' or (select SUBSTRING_INDEX(email, '@', 1)) like '% %' or (select SUBSTRING_INDEX(email, '@', 1)) like '%~%'
) t1 
where 
	demographics.macaddress = t1.macaddress
    and demographics.email = t1.email
    and demographics.gender = t1.gender
    and demographics.name = t1.name
    and demographics.password = t1.password
;
