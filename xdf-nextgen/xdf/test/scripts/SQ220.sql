-- Create table that contains data from one region
create table tc220_1 as 
select GEORegion, 
ActivityDate, 
ActivityTypeCode, 
GEOSummary, 
OperatingAirline, 
PublishedAirlineIATACode, 
Terminal, 
PriceCategoryCode, 
BoardingArea, 
PassengerCount
from T220
where GEORegion = '${GEO_REGION}';