# Indexer Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)
library(tidyr)
library(lubridate)


context("indexer function unit tests")


# Data Creation -----------------------------------------------------------

# Function to create simulated dataset
sim_data <- function(n_ids, n_recs, n_iter, seed = 319) {
  n <- n_ids * n_recs
  ids <- 1:n_ids
  dates <- seq(from = Sys.Date() - 365,
               to = Sys.Date(),
               by = "day")
  cat1 <- c("A", "B")
  cat2 <- c("X", "Y", "Z")

  do.call("rbind",
          replicate(n_iter,
                    {
                      data.frame(
                        id = sample(ids, n, replace = T),
                        date = sample(dates, n, replace = T),
                        cat1 = as.character(sample(cat1, n, replace = T)),
                        cat2 = as.character(sample(cat2, n, replace = T)),
                        metric1 = sample(1:7, n, replace = T),
                        metric2 = rnorm(n, mean = 50, sd = 5),
                        metric3 = sample(11:15, n, replace = T)
                      )
                    },
                    simplify = FALSE))
}

# R data.frame
dat <- sim_data(3, 2, 1, seed = 319) %>%
  mutate(index = row_number())

# Create Spark Connection
sc <- spark_connect(master = "local")

# Copy R data.frame to Spark - need to convert date class to character
tbl <-
  copy_to(sc,
          mutate_at(dat, "date", as.character),
          name = "dat",
          overwrite = TRUE) %>%
  mutate(date = to_date(date))



# Data creation end -------------------------------------------------------


# Convert DS to char for comparision --------------------------------------

to_char_ds <- function(df) {
  char_df <- df %>%
    mutate(
      id = as.character(id),
      date = as.character(date),
      cat1 = as.character(cat1),
      cat2 = as.character(cat2),
      metric1 = as.character(metric1),
      metric2 = as.character(metric2),
      metric3 = as.character(metric3),
      index = as.character(index)
    )

  return(char_df)
}


# Test Bed ----------------------------------------------------------------

# Test 1:Difference between sysdate and today should be 0 -----------------

systm_date <- data.frame(today = Sys.Date())
class(systm_date)

dif_date <-
  indexer(
    systm_date,
    "today",
    origin = "today",
    units = "days",
    periods = 1
  )

test_that("indexer differece between sysdate and today", {
  expect_equal(dif_date$days_since_today , 0)
})


#Test 2:Difference between sysdate and a fixed date string units is days ----------

dif_date_days <-
  indexer(
    systm_date,
    "today",
    origin = "2018-05-25",
    units = "days",
    periods = 1
  )

origin_val <- as.Date("2018-05-25")

date_value <- as.data.frame(systm_date$today - origin_val)
colnames(date_value)[1] <- "difdate"

date_value <- gsub(" .*$", "", date_value$difdate)

date_value <- as.numeric(date_value)

test_that("indexer differece between sysdate and today", {
  expect_equal(dif_date_days$`days_since_2018-05-25` , date_value)
})


# Test 3:Difference between sysdate and a fixed date string units hours --------

dif_date_hours <-
  indexer(
    systm_date,
    "today",
    origin = "2018-05-25",
    units = "hours",
    periods = 1
  )

origin_val <- as.Date("2018-05-25")

diff_in_hour <-
  difftime(systm_date$today , origin_val , units = c("hours"))

diff_in_hour <- strsplit(as.character(diff_in_hour), split = " +")

diff_in_hour <- as.numeric(diff_in_hour[1])

test_that("indexer differece between sysdate and today", {
  expect_equal(dif_date_hours$`hours_since_2018-05-25`, diff_in_hour)
})


# Test 4:Difference between sysdate and a fixed date string units hours period=4 --------

dif_date_hours_p <-
  indexer(
    systm_date,
    "today",
    origin = "2018-05-25",
    units = "hours",
    periods = 4
  )

origin_val <- as.Date("2018-05-25")

diff_in_hour <-
  difftime(systm_date$today , origin_val , units = c("hours"))

diff_in_hour <- strsplit(as.character(diff_in_hour), split = " +")

diff_in_hour <- as.numeric(diff_in_hour[1]) / 4

test_that("indexer differece between sysdate and today", {
  expect_equal(dif_date_hours_p$`4hour_periods_since_2018-05-25`,
               diff_in_hour)
})


#Test5:Number of Months between origin and today -------------------------

.origin <- as.Date("2018-04-25")
.date <- as.Date("2018-06-25")

df <- data.frame(date = .date)
dif_date_months <- indexer(df, "date", origin = .origin , units = "months", periods = 1)

test_that("indexer- Number of months differece between origin and today", {
  expect_equal(dif_date_months[[2]], 2)
})



#Test6:Number of years between origin and today -------------------------

dif_date_year <-
  indexer(
    systm_date,
    "today",
    origin = "2018-05-25",
    units = "years",
    periods = 1
  )

origin_val <- as.Date("2018-05-25")

date_value <- as.data.frame(systm_date$today - origin_val)
colnames(date_value)[1] <- "difdate"

date_value <- gsub(" .*$", "", date_value$difdate)

date_value <- as.numeric(date_value)

years_to_that_date <- round(date_value / 365, 8)

test_that("indexer- Number of years differece between origin and today", {
  expect_equal(dif_date_year$`years_since_2018-05-25`,
               years_to_that_date)
})


# Test 7:R Data set with date column-compare with spark DS-Units=Days --------

Unit_Days_R_DS <-
  indexer(dat,
          "date",
          origin = "2018-05-20",
          units = "days",
          periods = 1) %>%
  select(index, date, `days_since_2018-05-20`)

Unit_Days_spark_DS <-
  indexer(tbl, "date", origin = "2018-05-20", units = "days") %>%
  collect()  %>%
  select(index, date, `days_since_2018-05-20`) %>%
  as.data.frame()

test_that("indexer differece between spark and R DS", {
  expect_equal(Unit_Days_R_DS, Unit_Days_spark_DS)
})



# Test 7.5 : Test months unit ---------------------------------------------

df <- data.frame(today = today(), date1 = today() - days(1:31))
df_tbl <- copy_to(sc, df %>% mutate_all(as.character), overwrite = TRUE)
df_tbl <- mutate_all(df_tbl, funs(to_date))

months_r <- indexer(df,
                    "today",
                    origin = "date1",
                    units = "months",
                    periods = 1) %>%
  mutate_at("months_since_date1", funs(round(., 4)))

months_spk <- indexer(df_tbl,
                      "today",
                      origin = "date1",
                      units = "months",
                      periods = 1) %>%
  collect() %>%
  as.data.frame() %>%
  mutate_at("months_since_date1", funs(round(., 4)))


test_that("indexer differece between spark and R DS for months units", {
  expect_equal(months_r, months_spk)
})


# Test 7.75 : Test days unit ---------------------------------------------

days_r <- indexer(df,
                  "today",
                  origin = "date1",
                  units = "days",
                  periods = 1) %>%
  mutate_at("days_since_date1", funs(round(., 4)))

days_spk <- indexer(df_tbl,
                    "today",
                    origin = "date1",
                    units = "days",
                    periods = 1) %>%
  collect() %>%
  as.data.frame() %>%
  mutate_at("days_since_date1", funs(round(., 4)))


test_that("indexer differece between spark and R DS for months units", {
  expect_equal(days_r, days_spk)
})



# Test 8:Check for date difference when region and DS date --------

origin_date <- today() - months(1)
origin_name <- paste("months_since", origin_date, sep="_")

ds_R <-
  indexer(dat,
          "date",
          origin = origin_date,
          units = "months",
          periods = 1) %>%
  select(index, date, !!origin_name) %>% 
  mutate_at(origin_name, funs(round(., 1)))

ds_sp <-
  indexer(tbl, "date", origin = origin_date, units = "months") %>%
  collect()  %>%
  select(index, date, !!origin_name) %>% 
  mutate_at(origin_name, funs(round(., 1))) %>%
  as.data.frame()


test_that("indexer differece between spark and R DS", {
  expect_equal(ds_R, ds_sp)
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

Date_parter_check_DF <- date_parter(dat, "date")
Date_parter_check_Spark_DF <- date_parter(tbl, "date")

test_that("Date-part-column details of R ans Spark are matching", {
  expect_equal(colnames(Date_parter_check_DF),
               colnames(Date_parter_check_Spark_DF))
})
