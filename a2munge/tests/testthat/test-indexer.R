# Indexer Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)
library(tidyr)
library(lubridate)


context("indexer function unit tests")


# Create Spark Connection
sc <- spark_connect(master = "local")

# Copy R data.frame to Spark - need to convert date class to character
sim_tbl <- mutate_at(sim_df, "date", as.character) %>% 
  copy_to(sc, ., name = "df", overwrite = TRUE) %>%
  mutate(date = to_date(date))


# Test 1:Difference between sysdate and today should be 0 -----------------

systm_date <- data.frame(today = Sys.Date())
class(systm_date)

dif_date <- indexer(systm_date,
                    "today",
                    origin = "today",
                    units = "days",
                    periods = 1)

test_that("indexer differece between sysdate and today", {
  expect_equal(dif_date$days_since_today , 0)
})


#Test 2:Difference between sysdate and a fixed date string units is days ----------

ndays <- 7
day_lag <- today() - days(ndays)

dif_date_days <- indexer(systm_date,
                         "today",
                         origin = day_lag,
                         units = "days",
                         periods = 1 )

test_that("indexer differece between sysdate and today", {
  expect_equal(dif_date_days[1,2] , ndays)
})


# Test 3:Difference between sysdate and a fixed date string units hours --------

dif_date_hours <- indexer(systm_date,
                          "today",
                          origin = day_lag,
                          units = "hours",
                          periods = 1 )
test_that("indexer differece between sysdate and today", {
  expect_equal(dif_date_hours[1,2], ndays*24)
})


# Test 4:Difference between sysdate and a fixed date string units hours period=4 --------

dif_date_hours_p <-indexer(systm_date,
                           "today",
                           origin = day_lag,
                           units = "hours",
                           periods = 4)

test_that("indexer differece between sysdate and today", {
  expect_equal(dif_date_hours_p[1,2], ndays*24/4)
})




# Test9:Dateparter test for different output values generated --------------------

Date_parter_check <- date_parter(systm_date, "today")

sys_year <- as.numeric(format(systm_date, format = "%Y"))
sys_month <- as.numeric(format(systm_date, format = "%m"))
sys_date <- as.numeric(format(systm_date, format = "%d"))
quater_of_year <- lubridate::quarter(systm_date$today)
week_of_d <- as.numeric(format(systm_date, format = "%W"))
day_of_year <-  as.numeric(format(systm_date, format = "%j"))

# Indexer uses lubridate::wday() for day_of_week
#day_of_week <-  as.numeric(format(systm_date, format = "%w"))
day_of_week <- lubridate::wday(systm_date$today)


mod_val <- sys_year %% 4
remaining_days_of_yr <-
  ifelse((mod_val == 0), 366 - sys_date, 365 - sys_date)

remaining_days_of_month <- switch(
  sys_month,
  "1" = remaining_days_of_month <- 31 - sys_date ,
  "2" = remaining_days_of_month <-
    ifelse((mod_val == 0), 29 - sys_date, 28 - sys_date),
  "3" = remaining_days_of_month <- 31 - sys_date,
  "4" = remaining_days_of_month <- 30 - sys_date,
  "5" = remaining_days_of_month <- 31 - sys_date,
  "6" = remaining_days_of_month <- 30 - sys_date,
  "7" = remaining_days_of_month <- 31 - sys_date,
  "8" = remaining_days_of_month <- 31 - sys_date,
  "9" = remaining_days_of_month <- 30 - sys_date,
  "10" = remaining_days_of_month <- 31 - sys_date,
  "11" = remaining_days_of_month <- 30 - sys_date,
  "12" = remaining_days_of_month <- 31 - sys_date,
  print('default')
)

test_that("Dateparter give right values", {
  expect_equal(Date_parter_check$year, sys_year)
  expect_equal(Date_parter_check$quarter, quater_of_year)
  expect_equal(Date_parter_check$month, sys_month)
  expect_equal(Date_parter_check$week, week_of_d)
  expect_equal(Date_parter_check$day_of_month, sys_date)
  expect_equal(Date_parter_check$day_of_week, day_of_week)
  expect_equal(Date_parter_check$day_of_year, day_of_year)
  expect_equal(as.numeric(Date_parter_check$days_left_in_month),
               remaining_days_of_month)
})


# test 10:Column name and count comparision between R and Spark Dataset -------------

Date_parter_check_DF <- date_parter(sim_df, "date")
Date_parter_check_Spark_DF <- date_parter(sim_tbl, "date")

test_that("Date-part-column details of R ans Spark are matching", {
  expect_equal(colnames(Date_parter_check_DF),
               colnames(Date_parter_check_Spark_DF))
})
