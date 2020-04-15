/* Multi-object SQL
 1. The table tc220_1 should be resolved through OUTPUT location: hdfs:///data/bda/xda-test/dout/tc220_1
 2. The table tc221 should be resolved through INPUT file: hdfs:///data/bda/xda-test/dinp/tc221  
 3. The table country should be resolved through parameter file: 
 
*/

-- The table AIRSTAT_AIRLINE_BYREG contains average number of passenger per airline departured to or arrived from region: ${GEO_REGION}
/*create table TC221_AIRSTAT_AIRLINE_BYREG as
select OperatingAirline, AVG(PassengerCount) AC
from tc220_1
group by OperatingAirline order by AC desc;*/

-- The table AIRSTAT_TERM_BY_DATE_AIRL aggregates sum of all passenges caming through terminals ${TERMINAL1} and ${TERMINAL2}
-- by Airline for entire period
/*create table TC221_AIRSTAT_TERM_BY_AIRL as
select OperatingAirline, SUM(PassengerCount) 
from tc221
where BoardingArea in ( '${TERMINAL1}', '${TERMINAL2}')
group by OperatingAirline;*/

-- Decoding
create table TC221_AIRSTAT as 
select t.GEORegion, 
concat(t.ActivityDate, '01') as aDate, 
concat (t.Terminal ,concat(' - ', t.BoardingArea)) as Gate, 
case t.PriceCategoryCode when 'Low Fare' then 'E'  else 'R' end as PriceType, 
c.AirlineCountry, t.PassengerCount 
from tc221 t join country c on t.OperatingAirlineIATACode = c.OperatingAirlineIATACode;