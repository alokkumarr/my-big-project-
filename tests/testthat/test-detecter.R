
# Detecter Unit Tests -----------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("detecter function unit tests")


# Create toy dataset
set.seed(319)
id_vars <- seq(101, 200, by=1)
dates <- seq(from=Sys.Date()-365, to=Sys.Date(), by="day")
n <- 200
dat1 <- data.frame(index = 1:n,
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,0,0), ar = 0.7),
                                            rand.gen = function(n, ...) rt(n, df = 2))))

dat2 <- data.frame(index = 1:(n+1),
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,1,0), ar = 0.7),
                                            rand.gen = function(n, ...) rt(n, df = 2))))

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


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
dat1_tbl <- copy_to(sc, dat1, overwrite = TRUE)
dat2_tbl <- copy_to(sc, dat2, overwrite = TRUE)
dat3_tbl <- copy_to(sc, dat3, overwrite = TRUE)


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
