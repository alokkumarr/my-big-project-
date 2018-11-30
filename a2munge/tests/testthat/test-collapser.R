# Collapser Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)
library(tidyr)
library(lubridate)

context("Collapser unit tests")

n = 20


# Create Spark Connection
spk_versions <- sparklyr::spark_installed_versions()


if(! "2.3.0" %in% spk_versions$spark) {
  sparklyr::spark_install(version = "2.3.0")
}

sc <- spark_connect(master = "local")


# Basic Tests -------------------------------------------------------------
set.seed(n)
id_vars <- seq(1, n, by = 1)

dates_day <- as.Date('2018-09-13') +
  lubridate::days(seq(
    from = 1,
    length.out = n,
    by = 2
  ))

dates_min <-as.POSIXct('2018-09-13 10:20:25') + 
  lubridate::minutes(seq(
    from = 1,
    length.out = n,
    by = 2
  ))

dates_sec <- as.POSIXct('2018-09-13 10:27:20') + 
  lubridate::seconds(seq(
    from = 1,
    length.out = n,
    by = 2
  ))

cat1 <- c("A", "B")

set.seed(319)
dat <- data.frame()
for (id in id_vars) {
  nid <- sample(1:20, 1, replace = TRUE)
  d <- data.frame(
    id = sample(id, 20, replace = T),
    dates_day = sample(dates_day, 20, replace = T),
    dates_min = sample(dates_min, 20, replace = T),
    dates_sec = sample(dates_sec, 20, replace = T),
    cat1 = sample(cat1, 20, replace = T)
  )
  dat <- rbind(dat, d)
}

dat$dates_day <- format(as.Date(dat$dates_day), "%Y-%m-%d %H:%M:%S")

# Load data into Spark
dat_tbl <- dat %>%
  mutate(dates_min = as.character(dates_min)) %>% 
  copy_to(sc, ., overwrite = TRUE)

dat <- dat %>% mutate(dates_min = as.character(dates_min))

Convert_R_dtTime <- converter(
    dat,
    measure_vars = "dates_min",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_type = "datetime",
    time_zone = "UTC",
    output_suffix = "CONV"
  )

Convert_spk_dttime <- converter(
    dat_tbl,
    measure_vars = "dates_min",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_type = "datetime",
    time_zone = "UTC",
    output_suffix = "CONV"
  )


Convert_R_dtTime_sec <- converter(
    dat,
    measure_vars = "dates_sec",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_type = "datetime",
    time_zone = "UTC",
    output_suffix = "CONV"
  )

Convert_spk_dtTime_sec <- converter(
    dat_tbl,
    measure_vars = "dates_sec",
    input_format = "yyyy-MM-dd HH:mm:ss",
    output_type = "datetime",
    time_zone = "UTC",
    output_suffix = "CONV"
  )

Convert_R_dtTime <- Convert_R_dtTime %>%
  mutate(st_time = Convert_R_dtTime$dates_min,
         end_time = Convert_R_dtTime$dates_min)
Convert_spk_dttime_new <- Convert_R_dtTime %>% mutate(
  st_hur = as.character(Convert_R_dtTime$dates_min),
  end_hr = as.character(Convert_R_dtTime$dates_min)) %>% 
  copy_to(sc, ., overwrite = TRUE)

Convert_R_dtTime$st_hur <-
  as.POSIXct(paste0(substring(Convert_R_dtTime$end_time, 0, 13), ":00:00"))
Convert_R_dtTime$end_hr <-
  as.POSIXct(paste0(substring(Convert_R_dtTime$end_time, 0, 13), ":59:59"))

# Test 1:Collapser for param=Hour,start -----------------------------------

R_df_collapser_hr_strt <-
  collapser(
    Convert_R_dtTime,
    measure_vars = "dates_min_CONV",
    unit = "hour",
    side = "start",
    time_zone = "IST",
    output_suffix = "COLAPSER"
  )
spk_df_collapser_hr_strt <-
  collapser(
    Convert_spk_dttime,
    measure_vars = "dates_min_CONV",
    unit = "hour",
    side = "start",
    time_zone = "IST",
    output_suffix = "COLAPSER"
  )

spk_typ <-
  sdf_schema(spk_df_collapser_hr_strt)$dates_min_CONV_COLAPSER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(
    as.character(R_df_collapser_hr_strt$dates_min_CONV_COLAPSER),
    as.character(Convert_R_dtTime$st_hur)
  )
  # expect_equal(
  #  spk_df_collapser_hr_strt %>%
  #   collect() %>%
  #    select(dates_min_CONV_COLAPSER),
  # R_df_collapser_hr_strt %>%
  #  collect() %>%
  # select(dates_min_CONV_COLAPSER)
  #)
  expect_equal(class(R_df_collapser_hr_strt$dates_min_CONV_COLAPSER),
               c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")
})

# Test 2:Collapser for param=Hour,end -----------------------------------

R_df_collapser_hr_end <-
  collapser(
    Convert_R_dtTime,
    measure_vars = "dates_min_CONV",
    unit = "hour",
    side = "end",
    time_zone = "IST",
    output_suffix = "COLAPSER"
  )
spk_df_collapser_hr_end <-
  collapser(
    Convert_spk_dttime,
    measure_vars = "dates_min_CONV",
    unit = "hour",
    side = "end",
    time_zone = "IST",
    output_suffix = "COLAPSER"
  )

spk_typ <-
  sdf_schema(spk_df_collapser_hr_end)$dates_min_CONV_COLAPSER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(
    as.character(R_df_collapser_hr_end$dates_min_CONV_COLAPSER),
    as.character(Convert_R_dtTime$end_hr)
  )
  # expect_equal(
  #  spk_df_collapser_hr_end %>%
  #   collect() %>%
  #  select(dates_min_CONV_COLAPSER),
  #R_df_collapser_hr_end %>%
  # collect() %>%
  #select(dates_min_CONV_COLAPSER)
  #)
  expect_equal(class(R_df_collapser_hr_end$dates_min_CONV_COLAPSER),
               c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")
})

# Test 3:Collapser for param=day,start -----------------------------------

R_df_collapser_day_st <-
  collapser(
    Convert_R_dtTime,
    measure_vars = "dates_min_CONV",
    unit = "day",
    side = "start",
    time_zone = "IST",
    output_suffix = "COLAPSER"
  )
spk_df_collapser_day_st <-
  collapser(
    Convert_spk_dttime,
    measure_vars = "dates_min_CONV",
    unit = "day",
    side = "start",
    time_zone = "IST",
    output_suffix = "COLAPSER"
  )

Convert_R_dtTime$st_day <-
  as.POSIXct(paste0(substring(Convert_R_dtTime$end_time, 0, 11), "00:00:00"))
Convert_R_dtTime$end_day <-
  as.POSIXct(paste0(substring(Convert_R_dtTime$end_time, 0, 11), "23:59:59"))

spk_typ <-
  sdf_schema(spk_df_collapser_day_st)$dates_min_CONV_COLAPSER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(
    as.character(R_df_collapser_day_st$dates_min_CONV_COLAPSER),
    as.character(Convert_R_dtTime$st_day)
  )
  #expect_equal(
  #spk_df_collapser_day_st %>%
  #  collect() %>%
  #   select(dates_min_CONV_COLAPSER),
  #  R_df_collapser_day_st %>%
  #    collect() %>%
  #    select(dates_min_CONV_COLAPSER)
  #)

  expect_equal(class(R_df_collapser_day_st$dates_min_CONV_COLAPSER),
               c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")
})

# Test 4:Collapser for param=day,end -----------------------------------

R_df_collapser_day_end <-
  collapser(
    Convert_R_dtTime,
    measure_vars = "dates_min_CONV",
    unit = "day",
    side = "end",
    time_zone = "IST",
    output_suffix = "COLAPSER"
  )
spk_df_collapser_day_end <-
  collapser(
    Convert_spk_dttime,
    measure_vars = "dates_min_CONV",
    unit = "day",
    side = "end",
    time_zone = "IST",
    output_suffix = "COLAPSER"
  )

spk_typ <-
  sdf_schema(spk_df_collapser_day_end)$dates_min_CONV_COLAPSER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(
    as.character(R_df_collapser_day_end$dates_min_CONV_COLAPSER),
    as.character(Convert_R_dtTime$end_day)
  )

  # spk_df_collapser_day_end %>%
  #   collect() %>%
  #  select(dates_min_CONV_COLAPSER),
  #R_df_collapser_day_end %>%
  # collect() %>%
  #select(dates_min_CONV_COLAPSER)
  #)
  expect_equal(class(R_df_collapser_day_end$dates_min_CONV_COLAPSER),
               c("POSIXct", "POSIXt"))
  expect_equal(spk_typ, "TimestampType")
})

# Test 5:Collapser for param=month,start -----------------------------------

R_df_collapser_month_st <-
  collapser(
    Convert_R_dtTime,
    measure_vars = "dates_min_CONV",
    unit = "month",
    side = "start",
    time_zone = "UTC",
    output_suffix = "COLAPSER"
  )
spk_df_collapser_month_st <-
  collapser(
    Convert_spk_dttime,
    measure_vars = "dates_min_CONV",
    unit = "month",
    side = "start",
    time_zone = "UTC",
    output_suffix = "COLAPSER"
  )

Convert_R_dtTime$st_month <-
  as.character("2018-09-01")
Convert_R_dtTime$end_month <-
  as.character("2018-09-30")

spk_typ <-
  sdf_schema(spk_df_collapser_month_st)$dates_min_CONV_COLAPSER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(
    as.character(R_df_collapser_month_st$dates_min_CONV_COLAPSER),
    as.character(Convert_R_dtTime$st_month)
  )
  #expect_equal(
  # spk_df_collapser_month_st %>%
  #    collect() %>%
  #    select(dates_min_CONV_COLAPSER),
  # R_df_collapser_month_st %>%
  #  collect() %>%
  # select(dates_min_CONV_COLAPSER)
  #)
  expect_equal(spk_typ, "DateType")
  expect_equal(class(R_df_collapser_month_st$dates_min_CONV_COLAPSER),
               c("Date"))
})


# Test 6:Collapser for param=month,end -----------------------------------

R_df_collapser_month_end <-
  collapser(
    Convert_R_dtTime,
    measure_vars = "dates_min_CONV",
    unit = "month",
    side = "end",
    time_zone = "IST",
    output_suffix = "COLAPSER"
  )
spk_df_collapser_month_end <-
  collapser(
    Convert_spk_dttime,
    measure_vars = "dates_min_CONV",
    unit = "month",
    side = "end",
    time_zone = "IST",
    output_suffix = "COLAPSER"
  )

spk_typ <-
  sdf_schema(spk_df_collapser_month_end)$dates_min_CONV_COLAPSER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(
    as.character(R_df_collapser_month_end$dates_min_CONV_COLAPSER),
    as.character(Convert_R_dtTime$end_month)
  )
  #expect_equal(
  # spk_df_collapser_month_end %>%
  #  collect() %>%
  # select(dates_min_CONV_COLAPSER),
  #R_df_collapser_month_end %>%
  # collect() %>%
  #select(dates_min_CONV_COLAPSER)
  #)
  expect_equal(spk_typ, "DateType")
  expect_equal(class(R_df_collapser_month_end$dates_min_CONV_COLAPSER),
               c("Date"))
})

# Test 7:Collapser for param=year,start -----------------------------------

R_df_collapser_year_st <-
  collapser(
    Convert_R_dtTime,
    measure_vars = "dates_min_CONV",
    unit = "year",
    side = "start",
    time_zone = "PST",
    output_suffix = "COLAPSER"
  )
spk_df_collapser_year_st <-
  collapser(
    Convert_spk_dttime,
    measure_vars = "dates_min_CONV",
    unit = "year",
    side = "start",
    time_zone = "IST",
    output_suffix = "COLAPSER"
  )

Convert_R_dtTime$st_year <- as.character("2018-01-01")
Convert_R_dtTime$end_year <- as.character("2018-12-31")

spk_typ <-
  sdf_schema(spk_df_collapser_year_st)$dates_min_CONV_COLAPSER$type

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(
    as.character(R_df_collapser_year_st$dates_min_CONV_COLAPSER),
    as.character(Convert_R_dtTime$st_year)
  )
  #expect_equal(
  # spk_df_collapser_year_st %>%
  #  collect() %>%
  #  select(dates_min_CONV_COLAPSER),
  #R_df_collapser_year_st %>%
  # collect() %>%
  #select(dates_min_CONV_COLAPSER)
  #)
  expect_equal(spk_typ, "DateType")
  expect_equal(class(R_df_collapser_year_st$dates_min_CONV_COLAPSER),
               c("Date"))
})

# Test 8:Collapser for param=year,end -----------------------------------

R_df_collapser_year_end <-
  collapser(
    Convert_R_dtTime,
    measure_vars = "dates_min_CONV",
    unit = "year",
    side = "end",
    time_zone = "UTC",
    output_suffix = "COLAPSER"
  )
spk_df_collapser_year_end <-
  collapser(
    Convert_spk_dttime,
    measure_vars = "dates_min_CONV",
    unit = "year",
    side = "end",
    time_zone = "IST",
    output_suffix = "COLAPSER"
  )

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(
    as.character(R_df_collapser_year_end$dates_min_CONV_COLAPSER),
    as.character(Convert_R_dtTime$end_year)
  )
  #expect_equal(
  # spk_df_collapser_year_end %>%
  #  collect() %>%
  # select(dates_min_CONV_COLAPSER),
  #  R_df_collapser_year_end %>%
  #   collect() %>%
  #  select(dates_min_CONV_COLAPSER)
  #)
  expect_equal(spk_typ, "DateType")
  expect_equal(class(R_df_collapser_year_end$dates_min_CONV_COLAPSER),
               c("Date"))
})


# Test 9:Collapser for param=minute,start -----------------------------------

R_df_collapser_min_start <-
  collapser(
    Convert_R_dtTime_sec,
    measure_vars = "dates_sec_CONV",
    unit = "minute",
    side = "start",
    time_zone = "UTC",
    output_suffix = "COLAPSER_test"
  )
spk_df_collapser_min_start <-
  collapser(
    Convert_spk_dtTime_sec,
    measure_vars = "dates_sec_CONV",
    unit = "minute",
    side = "start",
    time_zone = "UTC",
    output_suffix = "COLAPSER_test"
  )

Convert_R_dtTime$st_min <-
  as.POSIXct(paste0(substring(Convert_R_dtTime_sec$dates_sec, 0, 16), ":00"))
Convert_R_dtTime$end_min <-
  as.POSIXct(paste0(substring(Convert_R_dtTime_sec$dates_sec, 0, 16), ":59"))

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(
    as.character(R_df_collapser_min_start$dates_sec_CONV_COLAPSER_test),
    as.character(Convert_R_dtTime$st_min)
  )
  # expect_equal(
  # spk_df_collapser_min_start %>%
  # collect() %>%
  # select(dates_min_CONV_COLAPSER),
  # R_df_collapser_min_start %>%
  # collect() %>%
  # select(dates_min_CONV_COLAPSER)
  # )
  expect_equal(spk_typ, "DateType")
  expect_equal(
    class(R_df_collapser_min_start$dates_sec_CONV_COLAPSER_test),
    c("POSIXct", "POSIXt")
  )
})

# Test 10:Collapser for param=minute,end -----------------------------------

R_df_collapser_min_end <-
  collapser(
    Convert_R_dtTime_sec,
    measure_vars = "dates_sec_CONV",
    unit = "minute",
    side = "end",
    time_zone = "UTC",
    output_suffix = "COLAPSER"
  )
spk_df_collapser_min_end <-
  collapser(
    Convert_spk_dtTime_sec,
    measure_vars = "dates_sec_CONV",
    unit = "minute",
    side = "end",
    time_zone = "UTC",
    output_suffix = "COLAPSER"
  )

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(
    as.character(R_df_collapser_min_end$dates_sec_CONV_COLAPSER),
    as.character(Convert_R_dtTime$end_min)
  )
  # expect_equal(
  # spk_df_collapser_min_end %>%
  # collect() %>%
  # select(dates_min_CONV_COLAPSER),
  # R_df_collapser_min_end %>%
  # collect() %>%
  # select(dates_min_CONV_COLAPSER)
  # )
  expect_equal(spk_typ, "DateType")
  expect_equal(class(R_df_collapser_min_end$dates_sec_CONV_COLAPSER),
               c("POSIXct", "POSIXt"))
})

# Test 11:Test for collapsing of multiple measure variables-------------------------------------------

Convert_R_dtTime_sec <- Convert_R_dtTime_sec %>%
  mutate(., dates_sec_CONV1 = dates_sec_CONV + duration(30, "days"))

Convert_spk_dtTime_sec <- Convert_spk_dtTime_sec %>%
  mutate(., dates_sec_CONV1 = date_add(dates_sec_CONV,30))

R_df_collapser_min_end <-
  collapser(
    Convert_R_dtTime_sec,
    measure_vars = c("dates_sec_CONV", "dates_sec_CONV1"),
    unit = "minute",
    side = "end",
    time_zone = "UTC",
    output_suffix = "COLAPSER"
  )
spk_df_collapser_min_end <-
  collapser(
    Convert_spk_dtTime_sec,
    measure_vars = c("dates_sec_CONV", "dates_sec_CONV1"),
    unit = "minute",
    side = "end",
    time_zone = "UTC",
    output_suffix = "COLAPSER"
  )

test_that("compare output of both data R and Spark Dataframes", {
  expect_equal(
    as.character(R_df_collapser_min_end$dates_sec_CONV_COLAPSER),
    as.character(Convert_R_dtTime$end_min)
  )
   expect_equal(
    as.character(R_df_collapser_min_end$dates_sec_CONV1_COLAPSER),
    as.character(Convert_R_dtTime$end_min+duration(30, "days"))
  )
  # expect_equal(
  # spk_df_collapser_min_end %>%
  # collect() %>%
  # select(dates_min_CONV_COLAPSER),
  # R_df_collapser_min_end %>%
  # collect() %>%
  # select(dates_min_CONV_COLAPSER)
  # )
  expect_equal(spk_typ, "DateType")
  expect_equal(class(R_df_collapser_min_end$dates_sec_CONV_COLAPSER),
               c("POSIXct", "POSIXt"))
})
