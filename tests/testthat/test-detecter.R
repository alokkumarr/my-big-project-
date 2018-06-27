# Detecter Unit Tests -----------------------------------------------------



# Set Up ------------------------------------------------------------------


library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)
library(lubridate)

context("detecter function unit tests")
Sys.time()



# Create Data sets --------------------------------------------------------

# Function to create group-by simulated data sets
new_df <- function(n, order, ar, df, grain, country, state){
  grain_fun <- match.fun(grain)
  tibble(
    index = 1:n,
    date_time = seq(from = Sys.time() - grain_fun(n-1),
                    to = Sys.time(),
                    by = grain)[1:n],
    val1 = as.numeric(arima.sim(n = n,
                                list(order = order[[1]], ar = ar[1]),
                                rand.gen = function(n, ...) rt(n, df = df[1]))),

    val2 = as.numeric(arima.sim(n = n,
                                list(order = order[[2]], ar = ar[2]),
                                rand.gen = function(n, ...) rt(n, df = df[2]))),
    country = country,
    state = state
  )
}


# Create datasets
n <- 200

# Stationary
dat1 <- data.frame(index = 1:n,
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,0,0), ar = 0.7),
                                            rand.gen = function(n, ...) rt(n, df = 2))))

# Non-stationary
dat2 <- data.frame(index = 1:(n+1),
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,1,0), ar = 0.7),
                                            rand.gen = function(n, ...) rt(n, df = 2))))

# With groups
dat3 <- rbind(data.frame(index = 1:n,
                         y = as.numeric(arima.sim(n = n,
                                                  list(order = c(1,0,0), ar = 0.5),
                                                  rand.gen = function(n, ...) rt(n, df = 3))),
                         group = "A"),
              data.frame(index = 1:n,
                         y = as.numeric(arima.sim(n = n,
                                                  list(order = c(1,0,0), ar = 0.8),
                                                  rand.gen = function(n, ...) rt(n, df = 1))),
                         group = "B"))

# Hourly data
hr_seq <- seq(Sys.time() - lubridate::hours(x=n), Sys.time(), by = "hour") %>% tail(n)
dat4 <- data.frame(date_time = hr_seq,
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,0,0), ar = 0.7),
                                            rand.gen = function(n, ...) rt(n, df = 2))))

# Manual Interventions
dat5 <- data.frame(index = 1:n,
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,0,0), ar = 0.7)))) %>%
  mutate(y = ifelse(index %in% c(50, 100, 150), -10, y))


# multiple groups and metrics
dat6 <- rbind(
  new_df(200, list(c(1,0,0), c(1,0,0)), c(.7, .5), df = c(2, 3),
         grain = "hours", country = "US", state = "NY"),
  new_df(200, list(c(1,0,0), c(1,0,0)), c(.8, .4), df = c(2, 3),
         grain = "hours", country = "US", state = "MA")
)



# Spark -------------------------------------------------------------------


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
dat1_tbl <- copy_to(sc, dat1, overwrite = TRUE)
dat2_tbl <- copy_to(sc, dat2, overwrite = TRUE)
dat3_tbl <- copy_to(sc, dat3, overwrite = TRUE)
dat4_tbl <- copy_to(sc, mutate(dat4, date_time = as.character(date_time)), overwrite = TRUE)
dat5_tbl <- copy_to(sc, dat5, overwrite = TRUE)
dat6_tbl <- copy_to(sc, mutate(dat6, date_time = as.character(date_time)), overwrite = TRUE)

# Format Spark tables
dat4_tbl <- mutate(dat4_tbl, date_time = unix_timestamp(date_time))
dat6_tbl <-  mutate(dat6_tbl, date_time = unix_timestamp(date_time))



# Apply Detecter ----------------------------------------------------------


r_detect1 <- dat1 %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)

r_detect2 <- dat2 %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)

r_detect3 <- dat3 %>%
  detecter(.,
           index_var = "index",
           group_vars = "group",
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)


r_detect5 <- dat5 %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "neg",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)


r_detect5b <- dat5 %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "both",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)


spk_detect1 <- dat1_tbl %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()

spk_detect2 <- dat2_tbl %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()

spk_detect3 <- dat3_tbl %>%
  detecter(.,
           index_var = "index",
           group_vars = "group",
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()

spk_detect5 <- dat5_tbl %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "neg",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()


spk_detect5b <- dat5_tbl %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "both",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()


# Creating Spark test 6 to test
spk_detect6 <- dat6_tbl %>%
  detecter(.,
           index_var = "date_time",
           group_vars = c("country", "state"),
           measure_vars = c("val1", "val2"),
           frequency = 24,
           direction = "both",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()



# Tests -------------------------------------------------------------------


test_that("detecter methods consistent", {
  expect_equal(
    spk_detect1 %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_detect1 %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(
    spk_detect2 %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_detect2 %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(
    spk_detect3 %>%
      select(index, group, measure, value, seasonal, trend, resid, anomaly) %>%
      arrange(group, measure, index) %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_detect3 %>%
      select(index, group, measure, value, seasonal, trend, resid, anomaly) %>%
      arrange(group, measure, index) %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(colnames(spk_detect1), colnames(r_detect1))
  expect_equal(colnames(spk_detect3), colnames(r_detect3))
})


test_that("detecter return correct dimensions", {
  expect_equal(nrow(r_detect1), nrow(dat1))
  expect_equal(nrow(r_detect3), nrow(dat3))
})


test_that("direction option works as expected", {

  r_detect1b <- dat1 %>%
    detecter(.,
             index_var = "index",
             group_vars = NULL,
             measure_vars = "y",
             frequency = 7,
             direction = "both",
             alpha = 0.02,
             max_anoms = 0.10,
             trend_window = .75)

  expect_true(all(r_detect1$index %in% r_detect1b$index))
})


test_that("detecter preserves index var date type", {

  dat1.1 <- dat1 %>%
    mutate(date = seq(from = Sys.Date() - n() +1, to=Sys.Date(), by = "day"))

  dat1.1_tbl <- dat1.1 %>%
    mutate(date = as.character(date)) %>%
    copy_to(sc, df = ., name = "dat1_1", overwrite = TRUE)
  dat1.1_tbl <- dat1.1_tbl %>%
    mutate(date = to_date(date))

  r_detect1.1 <-  detecter(dat1.1,
                           index_var = "date",
                           group_vars = NULL,
                           measure_vars = "y",
                           frequency = 7,
                           direction = "both",
                           alpha = 0.02,
                           max_anoms = 0.10,
                           trend_window = .75)

  spk_detect1.1 <-  detecter(dat1.1_tbl,
                             index_var = "date",
                             group_vars = NULL,
                             measure_vars = "y",
                             frequency = 7,
                             direction = "both",
                             alpha = 0.02,
                             max_anoms = 0.10,
                             trend_window = .75)
  expect_class(r_detect1.1$date, "Date")
  expect_equal(sdf_schema(spk_detect1.1)[[1]]$type, "DateType")
})



test_that("neg direction option works as expected", {

  r_detect5_anoms <- r_detect5 %>% filter(anomaly == 1)
  spk_detect5_anoms <- spk_detect5 %>% filter(anomaly == 1)

  expect_subset(c(50, 100, 150), r_detect5_anoms$index)
  expect_subset(c(50, 100, 150), spk_detect5_anoms$index)
})



test_that("both direction option works as expected", {

  r_detect5b_anoms <- r_detect5b %>% filter(anomaly == 1)
  spk_detect5b_anoms <- spk_detect5b %>% filter(anomaly == 1)

  expect_subset(c(50, 100, 150), r_detect5b_anoms$index)
  expect_subset(c(50, 100, 150), spk_detect5b_anoms$index)
})


test_that("Detecter with Multiple Group vars", {
  .group_vars <- c("country","state")
  .max_anoms <- .05

  r_detect_multi_Group <- dat6 %>%
    detecter(.,
             index_var = "index",
             group_vars = .group_vars,
             measure_vars = "val1",
             frequency = 7,
             direction = "pos",
             alpha = 0.01,
             max_anoms = .max_anoms,
             trend_window = .75)

  expect_equal(
    r_detect_multi_Group %>%
      group_by(country, state) %>%
      count(),
    dat6 %>%
      group_by(country, state) %>%
      count()
  )

  expect_true(
    all(
      r_detect_multi_Group %>%
      group_by(country, state) %>%
      summarise(anom_avg = mean(anomaly)) %>%
      ungroup() %>%
      pull(anom_avg) <= .max_anoms
    )
  )

})


test_that("Detecter with Multiple Measure vars", {
  .max_anoms <- .05
  r_detect_multi_mesure <- dat6 %>%
    detecter(.,
             index_var = "index",
             group_vars = "country",
             measure_vars = c('val1','val2'),
             frequency = 7,
             direction = "pos",
             alpha = 0.01,
             max_anoms = .max_anoms,
             trend_window = .75)

  expect_equal(
    r_detect_multi_mesure %>%
      group_by(country, measure) %>%
      count(),
    dat6 %>%
      melter(id_vars = c("index", "country"),
             measure_vars = c('val1','val2'),
             variable_name = "measure") %>%
      group_by(country, measure) %>%
      count()
  )

  expect_true(
    all(
      r_detect_multi_mesure %>%
        group_by(country, measure) %>%
        summarise(anom_avg = mean(anomaly)) %>%
        ungroup() %>%
        pull(anom_avg) <= .max_anoms
    )
  )

})


test_that("Detecter with hourly data", {

  r_detect_freq_with_hour <- dat4 %>%
    detecter(.,
             index_var = "date_time",
             group_vars = NULL,
             measure_vars = "y",
             frequency = 24,
             direction = "pos",
             alpha = 0.01,
             max_anoms = 0.05,
             trend_window = .75)


  spk_detect_freq_with_hour <- dat4_tbl %>%
    detecter(.,
             index_var = "date_time",
             group_vars = NULL,
             measure_vars = "y",
             frequency = 24,
             direction = "pos",
             alpha = 0.01,
             max_anoms = 0.05,
             trend_window = .75)

  expect_equal(
    r_detect_freq_with_hour %>%
      select(value, seasonal, trend, resid, anomaly) %>%
      round(5) ,
    spk_detect_freq_with_hour %>%
      collect() %>%
      select(value, seasonal, trend, resid, anomaly) %>%
      round(5)
  )
})

test_that("Range values align with anomalies", {

  expect_equal(
    r_detect1 %>%
      filter(anomaly == 1, value < upper) %>%
      nrow(),
    0
  )

  expect_equal(
    spk_detect1 %>%
      filter(anomaly == 1, value < upper) %>%
      nrow(),
    0
  )

  expect_equal(
    r_detect5b %>%
      filter(anomaly == 1, value > lower & value < upper) %>%
      nrow(),
    0
  )
})

