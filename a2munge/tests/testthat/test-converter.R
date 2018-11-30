# Date Converter Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)
library(tidyr)
library(lubridate)

context("Date Converter unit tests")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
sim_df <- sim_df %>% 
  mutate(rand_mins = sample(0:(23*60), n(), replace=TRUE), 
         date_time = as.POSIXct(date + minutes(rand_mins))) %>% 
  select(-rand_mins)

sim_tbl <- sim_df %>% 
  mutate_at(c("date", "date_time"), as.character) %>% 
  copy_to(sc, ., overwrite = TRUE)

sim_df <- sim_df %>% 
  mutate_at("date", as.Date) %>% 
  mutate_at("date_time", as.POSIXct)


# Test 1 : Input String format Date to output type-DateTime ---------------

Date1_R_dtTime <- converter(sim_df,
                            measure_vars = "date_time",
                            input_format = "yyyy-MM-dd HH:mm:ss",
                            output_type = "datetime",
                            time_zone = "UTC",
                            output_suffix = "CONV")

Date1_spk_dttime <- converter(sim_tbl,
                              measure_vars = "date_time",
                              input_format = "yyyy-MM-dd HH:mm:ss",
                              output_type = "datetime",
                              time_zone = "UTC",
                              output_suffix = "CONV")

spk_typ <- sdf_schema(Date1_spk_dttime)$date_CONV$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_dtTime),
               colnames(Date1_spk_dttime))
  expect_equal(class(Date1_R_dtTime$date_CONV), c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")
})


# Test 2 : Input String format Date to output type-Date ---------------

Date1_R_dt <- converter(sim_df,
                        measure_vars = "date_time",
                        input_format = "yyyy-MM-dd HH:mm:ss",
                        output_type = "date",
                        time_zone = "UTC",
                        output_suffix = "CONV")

Date1_spk_dt <- converter(sim_tbl,
                          measure_vars = "date_time",
                          input_format = "yyyy-MM-dd HH:mm:ss",
                          output_type = "date",
                          time_zone = "UTC",
                          output_suffix = "CONV")

spk_dt_type <- sdf_schema(Date1_spk_dt)$date_CONV$type

test_that("compare output of both data R and Spark Dataframes", {

  expect_equal(class(Date1_R_dt$date_CONV), c("Date"))
  expect_equal(spk_dt_type, "DateType")

})


# Test 3 : Don't specify output format,default output type-Datetime ---------------

Date1_R_dt_opt <- converter(sim_df,
                            measure_vars = "date_time",
                            input_format = "yyyy-MM-dd HH:mm:ss",
                            time_zone = "PST",
                            output_suffix = "CONV_Test" )

Date1_spk_dt_opt <- converter(sim_tbl,
                              measure_vars = "date_time",
                              input_format = "yyyy-MM-dd HH:mm:ss",
                              time_zone = "PST",
                              output_suffix = "CONV_Test" )

spk_typ <- sdf_schema(Date1_spk_dt_opt)$date_CONV$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_dt_opt),
               colnames(Date1_spk_dt_opt))
  expect_equal(class(Date1_R_dt_opt$date_CONV), c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")

})

# Test 4:Different input date format-"yyyy-MM-dd"-------------------------------------------

Date1_R_format_dtTime <- converter(sim_df,
                                   measure_vars = "date",
                                   input_format = "yyyy-MM-dd",
                                   output_type = "datetime",
                                   time_zone = "UTC",
                                   output_suffix = "CONV")


Date1_spk_format_dttime <- converter(sim_tbl,
                                     measure_vars = "date",
                                     input_format = "yyyy-MM-dd",
                                     output_type = "datetime",
                                     time_zone = "UTC",
                                     output_suffix = "CONV")

spk_typ <- sdf_schema(Date1_spk_format_dttime)$date_CONV$type


test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(colnames(Date1_R_format_dtTime),
               colnames(Date1_spk_format_dttime))
  expect_equal(spk_typ, "TimestampType")
})

