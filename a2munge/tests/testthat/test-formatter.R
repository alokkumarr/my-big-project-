# Formatter Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)
library(tidyr)
library(lubridate)

context("Date Formatter unit tests")

n = 20

# Basic Tests -------------------------------------------------------------
set.seed(n)
id_vars <- seq(1, n, by = 1)
dates <-  as.Date('2018-09-13') +
  lubridate::minutes(seq(
    from = 1,
    length.out = n,
    by = 2
  ))
cat1 <- c("A", "B")

dat <- data.frame()
for (id in id_vars) {
  n <- floor(runif(1) * n)
  d <- data.frame(
    id = sample(id, n, replace = T),
    date = sample(dates, n, replace = T),
    cat1 = sample(cat1, n, replace = T)
  )
  dat <- rbind(dat, d)
}

# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark

dat_tbl <- copy_to(sc, dat, overwrite = TRUE)

# Test 1 : Input date to output format-yyyy-MM-dd HH:mm:ss ---------------

Date1_R_dtTime <-
  formatter(
    dat,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

Date1_spk_dttime <-
  formatter(
    dat_tbl,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

spk_typ <- sdf_schema(Date1_spk_dttime)$date_FORMATTER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_dtTime),
               colnames(Date1_spk_dttime))
  expect_equal(class(Date1_R_dtTime$date_FORMATTER), "character")
  expect_equal(spk_typ, "StringType")
})

# Test 2 : Input date to output format-dd-MM-yyyy HH:mm:ss ---------------

Date1_R_dtTime <-
  formatter(
    dat,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_format = "dd-MM-yyyy HH:mm:ss",
    output_suffix = "FORMATTER_test"
  )

Date1_spk_dttime <-
  formatter(
    dat_tbl,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_format = "dd-MM-yyyy HH:mm:ss",
    output_suffix = "FORMATTER_test"
  )

spk_typ <- sdf_schema(Date1_spk_dttime)$date_FORMATTER_test$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_dtTime),
               colnames(Date1_spk_dttime))
  expect_equal(class(Date1_R_dtTime$date_FORMATTER_test), "character")
  expect_equal(spk_typ, "StringType")
})

# Test 3 : Input date to output format-MM-dd-yyyy HH:mm:ss ---------------

Date1_R_dtTime <-
  formatter(
    dat,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_format = "MM-dd-yyyy HH:mm:ss",
    output_suffix = "FORMATTER"
  )

Date1_spk_dttime <-
  formatter(
    dat_tbl,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_format = "MM-dd-yyyy HH:mm:ss",
    output_suffix = "FORMATTER"
  )

spk_typ <- sdf_schema(Date1_spk_dttime)$date_FORMATTER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_dtTime),
               colnames(Date1_spk_dttime))
  expect_equal(class(Date1_R_dtTime$date_FORMATTER), "character")
  expect_equal(spk_typ, "StringType")
})

# Test 4 : Input date to output format-MM-dd-yyyy ---------------

Date1_R_dtTime <-
  formatter(
    dat,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_format = "MM-dd-yyyy",
    output_suffix = "FORMATTER"
  )

Date1_spk_dttime <-
  formatter(
    dat_tbl,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_format = "MM-dd-yyyy",
    output_suffix = "FORMATTER"
  )

spk_typ <- sdf_schema(Date1_spk_dttime)$date_FORMATTER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_dtTime),
               colnames(Date1_spk_dttime))
  expect_equal(class(Date1_R_dtTime$date_FORMATTER), "character")
  expect_equal(spk_typ, "StringType")
})

# Test 5 : Input date to output format-dd-MM-yyyy ---------------

Date1_R_dtTime <-
  formatter(
    dat,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_format = "dd-MM-yyyy",
    output_suffix = "FORMATTER"
  )

Date1_spk_dttime <-
  formatter(
    dat_tbl,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_format = "dd-MM-yyyy",
    output_suffix = "FORMATTER"
  )

spk_typ <- sdf_schema(Date1_spk_dttime)$date_FORMATTER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_dtTime),
               colnames(Date1_spk_dttime))
  expect_equal(class(Date1_R_dtTime$date_FORMATTER), "character")
  expect_equal(spk_typ, "StringType")
})

# Test 6 : Input date to output format-yyyy-MM-dd ---------------

Date1_R_dtTime <-
  formatter(
    dat,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_format = "yyyy-MM-dd",
    output_suffix = "FORMATTER"
  )

Date1_spk_dttime <-
  formatter(
    dat_tbl,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_format = "yyyy-MM-dd",
    output_suffix = "FORMATTER"
  )

spk_typ <- sdf_schema(Date1_spk_dttime)$date_FORMATTER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_dtTime),
               colnames(Date1_spk_dttime))
  expect_equal(class(Date1_R_dtTime$date_FORMATTER), "character")
  expect_equal(spk_typ, "StringType")
})

# Test 7:Different input date formats dd/MM/yyyy-------------------------------------------

date_format1 <- dat

date_format1$date <- format(as.Date(date_format1$date), "%d/%m/%Y")

dat_tbl_format <-
  copy_to(sc, date_format1, overwrite = TRUE)

Date1_R_format_dtTime <-
  formatter(
    date_format1,
    measure_vars = "date",
    input_format = "dd/MM/yyyy",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

Date1_spk_format_dttime <-
  formatter(
    dat_tbl_format,
    measure_vars = "date",
    input_format = "dd/MM/yyyy",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_FORMATTER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))
  expect_equal(class(Date1_R_format_dtTime$date_FORMATTER), "character")
  expect_equal(spk_typ, "StringType")
})

# Test 8:Different input date formats MM/dd/yyyy-------------------------------------------

date_format2 <- dat

date_format2$date <- format(as.Date(date_format2$date), "%m/%d/%Y")

dat_tbl_format_2 <-
  copy_to(sc, date_format2 %>% mutate(date = as.character(date)), overwrite = TRUE)

Date1_R_format_dtTime <-
  formatter(
    date_format2,
    measure_vars = "date",
    input_format = "MM/dd/yyyy",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

Date1_spk_format_dttime <-
  formatter(
    dat_tbl_format_2,
    measure_vars = "date",
    input_format = "MM/dd/yyyy",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_FORMATTER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))
  expect_equal(class(Date1_R_format_dtTime$date_FORMATTER), "character")
  expect_equal(spk_typ, "StringType")
})

# Test 9:Different input date formats yyyy/MM/dd-------------------------------------------

date_format3 <- dat

date_format3$date <- format(as.Date(date_format3$date), "%Y/%m/%d")

dat_tbl_format_3 <-
  copy_to(sc, date_format3 %>% mutate(date = as.character(date)), overwrite = TRUE)

Date1_R_format_dtTime <-
  formatter(
    date_format3,
    measure_vars = "date",
    input_format = "yyyy/MM/dd",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

Date1_spk_format_dttime <-
  formatter(
    dat_tbl_format_3,
    measure_vars = "date",
    input_format = "yyyy/MM/dd",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_FORMATTER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))
  expect_equal(class(Date1_R_format_dtTime$date_FORMATTER), "character")
  expect_equal(spk_typ, "StringType")
})

# Test 10:Different input date formats yyyy/MM/dd HH:mm:ss-------------------------------------------

date_format4 <- dat

date_format4$date <-
  format(as.POSIXct(date_format4$date), "%Y/%m/%d %H:%M:%S")

dat_tbl_format_4 <-
  copy_to(sc, date_format4 %>% mutate(date = as.character(date)), overwrite = TRUE)

Date1_R_format_dtTime <-
  formatter(
    date_format4,
    measure_vars = "date",
    input_format = "yyyy/MM/dd HH:mm:ss",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

Date1_spk_format_dttime <-
  formatter(
    dat_tbl_format_4,
    measure_vars = "date",
    input_format = "yyyy/MM/dd HH:mm:ss",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_FORMATTER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))
  expect_equal(class(Date1_R_format_dtTime$date_FORMATTER), "character")
  expect_equal(spk_typ, "StringType")
})

# Test 11:Different input date formats MM/dd/yyyy HH:mm:ss-------------------------------------------

date_format5 <- dat

date_format5$date <-
  format(as.POSIXct(date_format5$date), "%m/%d/%Y %H:%M:%S")

dat_tbl_format_5 <-
  copy_to(sc, date_format5 %>% mutate(date = as.character(date)), overwrite = TRUE)

Date1_R_format_dtTime <-
  formatter(
    date_format5,
    measure_vars = "date",
    input_format = "MM/dd/yyyy HH:mm:ss",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

Date1_spk_format_dttime <-
  formatter(
    dat_tbl_format_5,
    measure_vars = "date",
    input_format = "MM/dd/yyyy HH:mm:ss",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_FORMATTER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))
  expect_equal(class(Date1_R_format_dtTime$date_FORMATTER), "character")
  expect_equal(spk_typ, "StringType")
})

# Test 12:Test for formatting of multiple measure variables-------------------------------------------

date_format7 <- dat

date_format7 <- date_format7 %>%
  mutate(., date_tmp = date) %>%
  mutate(., date = format(as.POSIXct(date_tmp), "%d/%m/%Y %H:%M:%S"),
         date1 = format(as.POSIXct(date_tmp) + duration(30, 'days'), "%d/%m/%Y %H:%M:%S")
  )

dat_tbl_format_7 <-
  copy_to(sc, date_format7 %>% mutate(date = as.character(date), date1 = as.character(date1)), overwrite = TRUE)

Date1_R_format_dtTime <-
  formatter(
    date_format7,
    measure_vars = c("date", "date1"),
    input_format = "dd/MM/yyyy HH:mm:ss",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

Date1_spk_format_dttime <-
  formatter(
    dat_tbl_format_7,
    measure_vars = c("date", "date1"),
    input_format = "dd/MM/yyyy HH:mm:ss",
    output_format = "yyyy-MM-dd HH:mm:ss",
    output_suffix = "FORMATTER"
  )

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_FORMATTER$type
spk_typ1 <- sdf_schema(Date1_spk_format_dttime)$date1_FORMATTER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))
  expect_equal(class(Date1_R_format_dtTime$date_FORMATTER), "character")
  expect_equal(class(Date1_R_format_dtTime$date1_FORMATTER), "character")
  expect_equal(spk_typ, "StringType")
  expect_equal(spk_typ1, "StringType")
})
