CREATE TABLE tc235_BUSINESS AS SELECT * FROM tc235;

CREATE TABLE tc235_Customer_Array AS
SELECT BusinessEntityID[0], YEAR(QuotaDate) AS SalesYear, SalesQuota AS CurrentQuota, cume_dist() OVER (ORDER BY YEAR(QuotaDate)) AS Cume_Dist_Quota
FROM tc235
WHERE BusinessEntityID[0].id = 225;

CREATE TABLE tc235_BUSINESS_Array_NTile AS
SELECT BusinessEntityID[0], YEAR(QuotaDate) AS SalesYear, SalesQuota AS CurrentQuota, ntile(5) OVER (ORDER BY YEAR(QuotaDate)) AS ntile_data
FROM tc235;