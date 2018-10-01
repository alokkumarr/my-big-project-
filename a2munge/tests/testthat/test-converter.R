# Date Converter Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)
library(tidyr)
library(lubridate)

context("Date Converter unit tests")

n = 20

# Basic Tests -------------------------------------------------------------
set.seed(n)
id_vars <- seq(1, n, by = 1)
dates <-
  as.Date('2018-09-13') + lubridate::minutes(seq(
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

dat_tbl <-
  copy_to(sc, dat %>% mutate(date = as.character(date)), overwrite = TRUE)

dat <- dat %>% mutate(date = as.character(date))

# Test 1 : Input String format Date to output type-DateTime ---------------

Date1_R_dtTime <-
  converter(
    dat,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_type = "datetime",
    time_zone = "UTC",
    output_suffix = "CONV"
  )

Date1_spk_dttime <-
  converter(
    dat_tbl,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_type = "datetime",
    time_zone = "UTC",
    output_suffix = "CONV"
  )

spk_typ <- sdf_schema(Date1_spk_dttime)$date_CONV$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_dtTime),
               colnames(Date1_spk_dttime))
  expect_equal(class(Date1_R_dtTime$date_CONV), c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")
})


# Test 2 : Input String format Date to output type-Date ---------------

Date1_R_dt <-
  converter(
    dat,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_type = "date",
    time_zone = "UTC",
    output_suffix = "CONV"
  )

Date1_spk_dt <-
  converter(
    dat_tbl,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_type = "date",
    time_zone = "UTC",
    output_suffix = "CONV"
  )

spk_dt_type <- sdf_schema(Date1_spk_dt)$date_CONV$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_dt),
               colnames(Date1_spk_dt))
  expect_equal(class(Date1_R_dt$date_CONV), c("Date"))
  expect_equal(spk_dt_type, "DateType")

})

# Test 3 : Don't specify output format,default output type-Datetime ---------------

Date1_R_dt_opt <-
  converter(
    dat,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    time_zone = "PST",
    output_suffix = "CONV_Test"
  )

Date1_spk_dt_opt <-
  converter(
    dat_tbl,
    measure_vars = "date",
    input_format = "yyyy-MM-dd HH:mm:ss",
    time_zone = "PST",
    output_suffix = "CONV_Test"
  )

spk_typ <- sdf_schema(Date1_spk_dt_opt)$date_CONV$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_dt_opt),
               colnames(Date1_spk_dt_opt))
  expect_equal(class(Date1_R_dt_opt$date_CONV), c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")

})

# Test 4:Different input date format-"dd/MM/yyyy"-------------------------------------------

date_format1 <- dat

date_format1$date <- format(as.Date(date_format1$date), "%d/%m/%Y")

dat_tbl_format <-
  copy_to(sc, date_format1 %>% mutate(date = as.character(date)), overwrite = TRUE)

Date1_R_format_dtTime <-
  converter(
    date_format1,
    measure_vars = "date",
    input_format = "dd/MM/yyyy",
    output_type = "datetime",
    time_zone = "UTC",
    output_suffix = "CONV"
  )


Date1_spk_format_dttime <-
  converter(
    dat_tbl_format,
    measure_vars = "date",
    input_format = "dd/MM/yyyy",
    output_type = "datetime",
    time_zone = "UTC",
    output_suffix = "CONV"
  )

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_CONV$type


test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))

  #expect_equal(class(Date1_R_format_dtTime$date_CONV),
   #            c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")
})


# Test 5:Different inputdate formats "MM/dd/yyyy"-------------------------------------------

date_format2 <- dat

date_format2$date <- format(as.Date(date_format2$date), "%m/%d/%Y")

dat_tbl_format_2 <-
  copy_to(sc, date_format2 %>% mutate(date = as.character(date)), overwrite = TRUE)

Date1_R_format_dtTime <-
  converter(
    date_format2,
    measure_vars = "date",
    input_format = "MM/dd/yyyy",
    output_type = "datetime",
    time_zone = "UTC",
    output_suffix = "CONV"
  )

Date1_spk_format_dttime <-
  converter(
    dat_tbl_format_2,
    measure_vars = "date",
    input_format = "MM/dd/yyyy",
    output_type = "datetime",
    time_zone = "UTC",
    output_suffix = "CONV"
  )

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_CONV$type


test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))

  #expect_equal(class(Date1_R_format_dtTime$date_CONV),
   #            c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")
})

# Test 6:Different inputdate formats yyyy/MM/dd-------------------------------------------

date_format3 <- dat

date_format3$date <- format(as.Date(date_format3$date), "%Y/%m/%d")

dat_tbl_format_3 <-
  copy_to(sc, date_format3 %>% mutate(date = as.character(date)), overwrite = TRUE)

Date1_R_format_dtTime <-
  converter(
    date_format3,
    measure_vars = "date",
    input_format = "yyyy/MM/dd",
    output_type = "datetime",
    time_zone = "IST",
    output_suffix = "CONV"
  )

Date1_spk_format_dttime <-
  converter(
    dat_tbl_format_3,
    measure_vars = "date",
    input_format = "yyyy/MM/dd",
    output_type = "datetime",
    time_zone = "IST",
    output_suffix = "CONV"
  )

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_CONV$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))
  #expect_equal(class(Date1_R_format_dtTime$date_CONV),
   #            c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")
})

# Test 7:Different Date formats MM/dd/yyyy HH:mm:ss-------------------------------------------

date_format5 <- dat

date_format5$date <-
  format(as.POSIXct(date_format5$date), "%m/%d/%Y %H:%M:%S")

dat_tbl_format_5 <-
  copy_to(sc, date_format5 %>% mutate(date = as.character(date)), overwrite = TRUE)

Date1_R_format_dtTime <-
  converter(
    date_format5,
    measure_vars = "date",
    input_format = "MM/dd/yyyy HH:mm:ss",
    output_type = "datetime",
    time_zone = "PST",
    output_suffix = "CONV"
  )

Date1_spk_format_dttime <-
  converter(
    dat_tbl_format_5,
    measure_vars = "date",
    input_format = "MM/dd/yyyy HH:mm:ss",
    output_type = "datetime",
    time_zone = "PST",
    output_suffix = "CONV"
  )

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_CONV$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))
  expect_equal(class(Date1_R_format_dtTime$date_CONV),
               c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")
})

# Test 8:Different Date formats dd/MM/yyyy HH:mm:ss-------------------------------------------

date_format6 <- dat

date_format6$date <-
  format(as.POSIXct(date_format6$date), "%d/%m/%Y %H:%M:%S")

dat_tbl_format_6 <-
  copy_to(sc, date_format6 %>% mutate(date = as.character(date)), overwrite = TRUE)

Date1_R_format_dtTime <-
  converter(
    date_format6,
    measure_vars = "date",
    input_format = "dd/MM/yyyy HH:mm:ss",
    output_type = "datetime",
    time_zone = "PST",
    output_suffix = "CONV"
  )

Date1_spk_format_dttime <-
  converter(
    dat_tbl_format_6,
    measure_vars = "date",
    input_format = "dd/MM/yyyy HH:mm:ss",
    output_type = "datetime",
    time_zone = "PST",
    output_suffix = "CONV"
  )

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_CONV$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))
  expect_equal(class(Date1_R_format_dtTime$date_CONV),
               c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")
})

# Test 9:Negative scenario -give different date format than expected-------------------------------------------

date_format6 <- dat

date_format6$date <-
  format(as.POSIXct(date_format6$date), "%d/%m/%Y %H:%M:%S")

dat_tbl_format_6 <-
  copy_to(sc, date_format6 %>% mutate(date = as.character(date)), overwrite = TRUE)

Date1_R_format_dtTime <-
  converter(
    date_format6,
    measure_vars = "date",
    input_format = "MM/dd/yyyy HH:mm:ss",
    output_type = "datetime",
    time_zone = "PST",
    output_suffix = "CONV"
  )

Date1_spk_format_dttime <-
  converter(
    dat_tbl_format_6,
    measure_vars = "date",
    input_format = "MM/dd/yyyy HH:mm:ss",
    output_type = "datetime",
    time_zone = "PST",
    output_suffix = "CONV"
  )

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_CONV$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))

  expect_equal(class(Date1_R_format_dtTime$date_CONV),
               c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")
})
