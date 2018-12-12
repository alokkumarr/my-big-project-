# index Unit Tests ---------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(dplyr)
library(sparklyr)
library(lubridate)

context("index unit tests")


# Basic Tests -------------------------------------------------------------

# Create Spark Connection
sc <- spark_connect(master = "local")

n <- 20

# Test1:Numeric and integer value indexing ------------------------------------------

seq_q <- seq(1:n)

seq_num <- as.numeric(seq_q)

ind_int <- index(seq_q, unit = NULL)
ind_num <- index(seq_num, unit = NULL)

ind_int_ext <- extend(ind_int, 5)
ind_num_ext <- extend(ind_num, 5)

s_q_ext <- seq(21, 25, 1)

test_that("Index for Numeric class", {
  expect_class(ind_int, "index")
  expect_equal(ind_int_ext, s_q_ext)
  expect_equal(ind_num_ext, s_q_ext)
})

# Test2:Numeric value indexing where unit is not NULL ------------------------------------------

seq_q_unit <- seq(1, 20, 3)
seq_num <- as.numeric(seq_q_unit)

ind_num_unit <- index(seq_q_unit, unit = NULL)
ind_numeric_unit <- index(seq_num, unit = NULL)

ind_num_ext <- extend(ind_num_unit, 5)
ind_numeric_ext <- extend(ind_numeric_unit, 5)

seq_q_unit_ext <- seq(22, 35, 3)

test_that("Index for Numeric class", {
  expect_class(ind_num, "index")
  expect_equal(ind_num_ext, seq_q_unit_ext)
  expect_equal(ind_numeric_ext, seq_q_unit_ext)
})


#Test3:Date value indexing ------------------------------------------

date_check <-
  seq(as.Date("2018-07-12"), as.Date("2018-07-20"), by = "days")

ind_date <- index(date_check, unit = "days")

ind_date_ext <- extend(ind_date, 5)

List_date <-
  seq(as.Date("2018-07-21"), as.Date("2018-07-25"), by = "days")

test_that("Index for Date class", {
  expect_class(ind_date, "index")
  expect_class(ind_date_ext, "Date")
  expect_equal(ind_date_ext, List_date)
})


#Test4:Index value for sequence of Weeks ------------------------------------------

dat_time_week <-
  as.Date("2018-07-31") + lubridate::weeks(seq(
    from = 1,
    length.out = 3,
    by = 1
  ))
date_ind_week <- index(dat_time_week, unit = "weeks")

date_ind_week_ext <- extend(date_ind_week, 3)

List_week <-
  seq(as.Date("2018-08-28"), as.Date("2018-09-11"), by = "weeks")

test_that("Index for Date-Week class", {
  expect_class(date_ind_week, "time_index")
  expect_equal(date_ind_week_ext, List_week)

})

#Test5:Index value for sequence of Years ------------------------------------------

dat_year <-
  as.Date("2018-07-31") + 365 + lubridate::years(seq(
    from = 1,
    length.out = 5,
    by = 4
  ))

date_ind <- index(dat_year, unit = "years")

date_ind_ext <- extend(date_ind, 3)

List_year <-
  seq(as.Date("2040-07-31"), as.Date("2048-07-31"), by = "4 years")

test_that("Test the index for years", {
  expect_class(date_ind, "time_index")
  expect_equal(date_ind_ext, List_year)
})


#Test6:Create Index for POSIXt class ------------------------------------------
day_hours <-
  Sys.Date() + lubridate::hours(seq(
    from = 1,
    length.out = n,
    by = 2
  ))

date_ind <- index(x = day_hours, unit = "hours")

seq_etx <-
  seq(as_datetime(date_ind$end) + 2 * 60 * 60,
      as_datetime(date_ind$end) + 6 * 60 * 60,
      by = 2 * 60 * 60)

date_in_ext <- extend(date_ind, 3)


test_that("Index for Posixt class", {
  expect_class(date_ind, "index")
  expect_equal(date_in_ext, seq_etx)
})

#Test7:Create Index for POSIXt-Minutes class ------------------------------------------
day_minutes <-
  Sys.Date() + lubridate::minutes(seq(
    from = 1,
    length.out = 6,
    by = 2
  ))

date_min_ind <- index(day_minutes, unit = "minutes")

Min_in_ext <- extend(date_min_ind, 3)

seq_min_etx <-
  seq(as_datetime(date_min_ind$end) + 2 * 60,
      as_datetime(date_min_ind$end) + 6 * 60,
      by = 2 * 60)

test_that("Index for Posixt class", {
  expect_class(date_ind, "index")
  expect_equal(Min_in_ext, seq_min_etx)
})

#Test8:Create Index for POSIXt-secords class ------------------------------------------
day_seconds <-
  Sys.Date() + lubridate::seconds(seq(
    from = 1,
    length.out = 6,
    by = 3
  ))

date_sec_ind <- index(day_seconds, unit = "seconds")

Sec_in_ext <- extend(date_sec_ind, 3)

seq_sec_etx <-
  seq(as_datetime(date_sec_ind$end) + 3,
      as_datetime(date_sec_ind$end) + 9,
      by = 3)

test_that("Index for Posixt class", {
  expect_class(date_sec_ind, "index")
  expect_equal(Sec_in_ext, seq_sec_etx)
})
